package com.company.andy.feature.org.equipment.domain;

import com.company.andy.common.model.actor.OrgActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Always use factories to create aggregate root objects,
// which makes the creation process of aggregate roots more explicit

@Component
@RequiredArgsConstructor
public class EquipmentFactory {

    public Equipment create(String name, OrgActor actor) {
        return new Equipment(name, actor);
    }
}
