FROM adoptopenjdk/openjdk11
ARG JAR_FILE=deploy/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-jar","/application.jar"]