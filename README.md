# Spring Boot and Vaadin NPS demo app

## Pre-requisites

- Java 21
- Docker
- API keys as environment variables
    - Google OAuth2
        - `GOOGLE_CLIENT_ID`
        - `GOOGLE_CLIENT_SECRET`

## Running the app

Run `NpsApplication` class in your IDE or run `./mvnw spring-boot:run` in the terminal.

This will automatically start a PostgreSQL database through the included docker-compose file and
initialize the database with some sample data.


