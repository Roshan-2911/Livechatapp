FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
#COPY --from=build /app/target/livechatapp-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.war app.war

EXPOSE 8080
ENTRYPOINT ["java", "-war", "app.war"]
