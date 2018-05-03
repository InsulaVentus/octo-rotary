FROM openjdk:8-jdk

COPY target/universal/*.zip /application.zip

RUN set -x && \
    unzip -d /application /application.zip && \
    mv /application/*/* /application/ && \
    rm /application/bin/*.bat && \
    mv /application/bin/* /application/bin/start

EXPOSE 22 9000

CMD /application/bin/start -Dhttp.port=9000 -Dplay.crypto.secret=terces
