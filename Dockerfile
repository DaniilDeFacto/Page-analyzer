FROM eclipse-temurin:20-jdk

WORKDIR /app

COPY . .

RUN ./app/gradlew --no-daemon dependencies

RUN ./app/gradlew --no-daemon build

ENV JAVA_OPTS "-Xmx512M -Xms512M"

CMD java -Dspring.profiles.active=prod -jar app/build/libs/app-1.0-SNAPSHOT-all.jar