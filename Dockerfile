FROM openjdk:17-jdk-slim

WORKDIR /app

COPY taskExecutor/target/taskExecutorService-1.0-SNAPSHOT.jar /app/taskExecutorService-1.0-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "taskExecutorService-1.0-SNAPSHOT.jar"]
