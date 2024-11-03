package app.vaadin.nps.service;

public record ResponseDTO(
    String token,
    int score
) {
}