FROM openjdk:alpine

WORKDIR /srv

WORKDIR /app

ADD . /app

RUN chmod 755 ./gradlew

RUN ./gradlew build

WORKDIR /usr/share/nginx/html
RUN cp -R /app/web/* /usr/share/nginx/html/

RUN cp /app/index.html /usr/share/nginx/html/index.html

FROM nginx:mainline-alpine

RUN apk add --no-cache curl

WORKDIR /usr/share/nginx/html

COPY --from=0 /usr/share/nginx/html /usr/share/nginx/html


EXPOSE 80

ENV SEWOBEUSER restuser

ENV SEWOBEPASSWORD 12345secret

ENV SEWOBEURL https://server30.der-moderne-verein.de/restservice/standard/v1.0/auswertungen/get_auswertung_data.php

COPY start.sh /app/start.sh
COPY query_sewobe.sh /app/query_sewobe.sh
COPY crontab /crontab
RUN chmod 755 /app/start.sh /app/query_sewobe.sh
RUN /usr/bin/crontab /crontab


RUN ls -la /usr/share/nginx/html

# RUN /usr/sbin/crond -f -l 8 &

WORKDIR /app

CMD /app/start.sh
