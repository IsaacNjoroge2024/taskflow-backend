FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/taskflow-backend.jar app.jar

EXPOSE 8080

CMD ["java", "-Dserver.port=${PORT}", "-Dspring.profiles.active=prod", "-jar", "app.jar"]