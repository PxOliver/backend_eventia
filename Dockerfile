# ---------- Etapa 1: construir el JAR con Maven ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos los archivos necesarios para compilar
COPY pom.xml .
COPY src ./src

# Compilamos el proyecto (sin tests para que sea más rápido)
RUN mvn clean package -DskipTests

# ---------- Etapa 2: imagen final para ejecutar la app ----------
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiamos el JAR generado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto interno (Render luego mapea el suyo)
EXPOSE 8080

# Variable PORT que Spring Boot usará (la sobreescribe Render)
ENV PORT=8080

# Comando para arrancar Spring Boot
CMD ["java", "-jar", "app.jar"]
