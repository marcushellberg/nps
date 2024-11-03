package app.vaadin.nps.service;

import app.vaadin.nps.model.TargetList;

public record TargetListFormDTO(
        Long id,
        String name
) {
    public TargetList toEntity() {
        TargetList targetList = new TargetList();
        targetList.setId(this.id);
        targetList.setName(this.name);
        return targetList;
    }

    public static TargetListFormDTO fromDTO(TargetListDTO dto) {
        return new TargetListFormDTO(
                dto.id(),
                dto.name()
        );
    }
}