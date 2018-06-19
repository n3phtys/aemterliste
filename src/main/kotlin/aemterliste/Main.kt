package aemterliste

import kotlin.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Json

val divAemterliste = document.getElementById("aemterliste")!!
val divMailmanmails = document.getElementById("mailmanmailstxt")!!
val divAemtermails = document.getElementById("aemtermailstxt")!!
val divMails = document.getElementById("mailstxt")!!
val divBiernamenmails = document.getElementById("biernamenmailstxt")!!

val txtAndDivs = mapOf<String, Element>("mailmanmails.txt" to divMailmanmails, "aemtermails.txt" to divAemtermails, "biernamenmails.txt" to divBiernamenmails, "mails.txt" to divMails)


fun fillPreWithTextFile(pre: Element, textFilePath: String) {
    val request = XMLHttpRequest()
    request.onreadystatechange = {
        if (request.readyState == 4.toShort() && request.status == 200.toShort()) {
            pre.innerHTML = request.responseText
        }
    }
    request.open("GET", textFilePath, true)
    request.send()
}


@Suppress("NOTHING_TO_INLINE")
data class DatensatzContainer(val DATENSATZ: Json) {
    inline fun getAemter(): List<String> = (DATENSATZ.get("AMT")!! as String).split(",").map { it.trim() }
    inline fun getJahr(): String = DATENSATZ.get("JAHR")!! as String
    inline fun getNeuwahl(): String = DATENSATZ.get("NEUWAHL")!! as String
    inline fun getEmail(): String = DATENSATZ.get("E-MAIL")!! as String
    inline fun getVorname(): String = DATENSATZ.get("VORNAME-PRIVATPERSON")!! as String
    inline fun getNachname(): String = DATENSATZ.get("NACHNAME-PRIVATPERSON")!! as String
    inline fun getBiername(): String = DATENSATZ.get("BIERNAME")!! as String
    inline fun getFullName(): String = "${getVorname()} (${getBiername()}) ${getNachname()}"
    inline fun getFullNameAnchor(): String = "<a href=\"mailto:${getEmail()}\" target=\"_top\">${getFullName()}</a>"
}

typealias AemterJSON = Map<String, DatensatzContainer>


data class AemterContainer(val DATENSATZ: AemterDatensatz)


data class AemterDatensatz(val AMT: String)

typealias Aemter27JSON = Map<String, AemterContainer>

fun convertSinglePerson(amt: String, person: DatensatzContainer?) : String {
    val name = if (person != null) person.getFullNameAnchor() else "vakant"
    val neuwahl = if (person != null) "${person.getNeuwahl()} ${person.getJahr()}" else "N/A"
    return "<tr><td>${amt}</td><td>${name}</td><td>${neuwahl}</td></tr>\n"
}

fun buildAemterlisteHTML(targetElement: HTMLDivElement, aemter27JSON: Json, aemterJSON: Json) {

    val aemter: MutableMap<String, List<DatensatzContainer>> = mutableMapOf()

    var i = 1
    while (aemterJSON.get(i.toString()) != null ) {
        val value = JSON.parse<DatensatzContainer>(JSON.stringify(aemterJSON.get(i.toString())))
        value.getAemter().forEach {amt ->
            val list = (aemter.getOrPut(amt, {listOf()}) + value).sortedBy { it.getFullName() }
            aemter.put(amt, list)
        }
        i += 1
    }


    val aemter27: MutableList<String> =  mutableListOf()
    i = 1;
    while (aemter27JSON.get(i.toString()) != null ) {
        aemter27.add((JSON.parse<AemterContainer>(JSON.stringify(aemter27JSON.get(i.toString())))).DATENSATZ.AMT)
        i += 1
    }

    var table_mid = ""
     aemter27.forEach { amt ->
        val personen = aemter.get(amt)
        val str : String = if (personen != null && personen.size > 0) {
            personen.map { convertSinglePerson(amt, it) }.joinToString("\n")
        } else {
            convertSinglePerson(amt, null)
        }
        println("Rows: $str")
        table_mid += str + "\n"
    }


    val table_begin = "<div class='table-responsive'><table class='table'><thead><tr><th>Amt</th><th>Amtsträger</th><th>Neuwahl</th></tr></thead><tbody>"

    val table_end = "</tbody></table></div>"

    println(table_mid)

    targetElement.innerHTML = table_begin + table_mid  + table_end

}


fun fillAemteriste(pre: HTMLDivElement) {


    pre.innerHTML = "Waiting to load..."


    var aemter27: Json? = null

    val firstRequest = XMLHttpRequest()
    val secondRequest = XMLHttpRequest()
    firstRequest.onreadystatechange = {
        if (firstRequest.readyState == 4.toShort() && firstRequest.status == 200.toShort()) {
            aemter27 = JSON.parse(firstRequest.responseText)
            secondRequest.send()
        }
    }
    secondRequest.onreadystatechange = {
        if (secondRequest.readyState == 4.toShort() && secondRequest.status == 200.toShort()) {
            val aemter: Json = JSON.parse(secondRequest.responseText)
            buildAemterlisteHTML(pre, aemter27!!, aemter)
        }
    }
    firstRequest.open("GET", "aemter27.json", true)
    secondRequest.open("GET", "aemter.json", true)
    firstRequest.send()
}


fun main(args: Array<String>) {
    txtAndDivs.forEach { fillPreWithTextFile(it.value, it.key) }
    fillAemteriste(divAemterliste as HTMLDivElement)
}