# 1. Build stage
FROM gradle:8-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

# 2. Run stage
FROM eclipse-temurin:21-jre
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/server/build/libs/*.jar /app/Finance_sanbe.jar
ENTRYPOINT ["java","-Djava.net.preferIPv4Stack=true","-jar","/app/Finance_sanbe.jar"]