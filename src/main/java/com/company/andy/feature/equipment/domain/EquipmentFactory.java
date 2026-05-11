package com.company.andy.feature.equipment.domain;

import com.company.andy.common.model.actor.Actor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EquipmentFactory {

    public Equipment create(String name, Actor actor) {
        return new Equipment(name, actor);
    }
}
