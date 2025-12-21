package com.company.andy.sample.equipment.domain;

import com.company.andy.common.model.operator.Operator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EquipmentFactory {

    public Equipment create(String name, Operator operator) {
        return new Equipment(name, operator);
    }
}
