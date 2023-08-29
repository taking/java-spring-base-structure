FROM eclipse-temurin:17.0.7_7-jdk AS builder
LABEL kr.taking.backend.authors="https://github.com/taking"

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY backend nw
COPY application.properties_docker nw/src/resources/application.properties
RUN chmod +x ./gradlew
RUN ./gradlew bootJar
RUN ["/bin/bash", "-c", "ls"]

FROM eclipse-temurin:17.0.7_7-jdk
COPY --from=builder backend/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8888
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]