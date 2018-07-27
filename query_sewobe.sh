#!/bin/sh


echo "SEWOBE Anfragen laufen"

curl -X POST -F "USERNAME=$SEWOBEUSER" -F "PASSWORT=$SEWOBEPASSWORD" -F 'AUSWERTUNG_ID=170' $SEWOBEURL > "/usr/share/nginx/html/aemter.json"
curl -X POST -F "USERNAME=$SEWOBEUSER" -F "PASSWORT=$SEWOBEPASSWORD" -F 'AUSWERTUNG_ID=27' $SEWOBEURL > "/usr/share/nginx/html/aemter27.json"
curl -X POST -F "USERNAME=$SEWOBEUSER" -F "PASSWORT=$SEWOBEPASSWORD" -F 'AUSWERTUNG_ID=102' $SEWOBEURL > "/usr/share/nginx/html/sewobe102.json"
curl -X POST -F "USERNAME=$SEWOBEUSER" -F "PASSWORT=$SEWOBEPASSWORD" -F 'AUSWERTUNG_ID=158' $SEWOBEURL > "/usr/share/nginx/html/sewobe158.json"


echo "Ende von Anfragen"
