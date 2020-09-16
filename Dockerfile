FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} jwt-auth-api.jar
ENTRYPOINT ["java","-jar","/jwt-auth-api.jar"]