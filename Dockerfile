# ---- Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY openapi ./openapi
COPY src ./src
RUN mvn -q -e -U -DskipTests=true package

# ---- Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY --from=build /app/target/demo-users-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]