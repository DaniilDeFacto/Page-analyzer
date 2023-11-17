FROM eclipse-temurin:20-jdk

WORKDIR /app

COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .

RUN ./gradlew --no-daemon dependencies

COPY app/src app/src
COPY app/config app/config

RUN ./gradlew --no-daemon build

ENV JAVA_OPTS "-Xmx512M -Xms512M"
EXPOSE 7070

CMD java -jar app/build/libs/app-1.0-SNAPSHOT-all.jar