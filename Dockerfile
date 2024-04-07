FROM maven:3.8.4 as wrapper
WORKDIR /app
COPY . ./
RUN mvn -N io.takari:maven:0.7.7:wrapper -Dmaven=3.8.4

# Second stage: Build stage
FROM openjdk:17 as build
WORKDIR /app
COPY --from=wrapper /app .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Third stage: Runtime stage
FROM openjdk:17.0.2-jdk-slim
WORKDIR /app
COPY --from=build /app/target/airbnb-b11-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "airbnb-b11-0.0.1-SNAPSHOT.jar"]
EXPOSE 2024
