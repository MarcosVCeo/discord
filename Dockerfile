FROM maven:3.9.7-amazoncorretto-21 AS build
COPY . .
RUN mvn compile
RUN mvn package

FROM openjdk:21
COPY --from=build target/discord-1-jar-with-dependencies.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
