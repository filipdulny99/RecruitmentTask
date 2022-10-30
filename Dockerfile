FROM openjdk:17-jdk-alpine
COPY target/RecruitmentTask-0.0.1-SNAPSHOT.jar app.jar
COPY src src
ENTRYPOINT ["java","-jar","/app.jar"]
