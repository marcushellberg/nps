package app.vaadin.nps.service;

import app.vaadin.nps.model.TargetList;

public record TargetListDTO(
        Long id,
        String name,
        Integer emailCount
) {
    public static TargetListDTO fromEntity(TargetList targetList) {
        return new TargetListDTO(
                targetList.getId(),
                targetList.getName(),
                targetList.getEmails().size()
        );
    }
}
