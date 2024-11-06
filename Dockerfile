# Build stage
FROM maven:3-eclipse-temurin AS build
WORKDIR /app

# Copy Maven files
COPY pom.xml .

# Copy additional config files for frontend build
COPY package.json package-lock.json tsconfig.json types.d.ts vite.config.ts vite.generated.ts ./

# Copy source files
COPY src ./src

# Build the application
RUN mvn -Pproduction -DskipTests package

# Final stage
FROM openjdk:21-jdk-slim
COPY --from=build /app/target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar", "-spring.profiles.active=prod"]