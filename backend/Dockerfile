# Gebruik een lichte Java 17 runtime
FROM eclipse-temurin:17-jdk-alpine

# Auteur info
LABEL authors="gielvangaal"

# Zet de werkdirectory in de container
WORKDIR /app

# Kopieer de fat jar die Gradle bouwt naar de container
COPY build/libs/backend-fat.jar app.jar

# Exposeer poort 8080 (de Ktor server luistert hier)
EXPOSE 8080

# Start de Ktor server
CMD ["java", "-jar", "app.jar"]
