# First stage: JDK with GraalVM
FROM ghcr.io/graalvm/native-image-community:21 AS build

WORKDIR /usr/src/app

COPY . .

# Build with both production and build profiles
RUN ./mvnw -Pnative -Pproduction native:compile -DskipTests -Dspring.profiles.active=build

# Second stage: Lightweight debian-slim image
FROM debian:bookworm-slim

WORKDIR /app

# Copy the native binary from the build stage
COPY --from=build /usr/src/app/target/nps /app/nps

# Run the application with production profile
ENV SPRING_PROFILES_ACTIVE=prod
CMD ["/app/nps"]