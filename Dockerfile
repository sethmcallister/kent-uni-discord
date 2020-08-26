FROM openjdk:8-jdk-alpine

RUN addgroup -S discord && adduser -S discord -G discord

USER discord:discord

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} discord.jar

ENTRYPOINT ["java", "-jar", "/discord.jar"]
