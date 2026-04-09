# Dockerfile para a aplicação Spring Boot

# [DOCKER-001] Define a imagem base com JDK 21 (Eclipse Temurin - OpenJDK)
FROM eclipse-temurin:21-jdk

# [DOCKER-002] Copia o arquivo JAR gerado pelo Maven/Gradle para o container
# O wildcard (*) captura qualquer versão do JAR (ex: app-1.0.0.jar)
COPY target/*.jar app.jar

# [DOCKER-003] Define o comando de entrada para executar a aplicação
# java -jar /app.jar -> inicia a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "/app.jar"]