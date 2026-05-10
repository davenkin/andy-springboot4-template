package com.company.andy.common.util;

import com.company.andy.common.model.operator.Operator;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Set;

import static com.company.andy.common.model.Role.ORG_ADMIN;
import static com.company.andy.common.model.operator.OperatorSource.HUMAN_USER;

@Hidden
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AboutController {
    private static final Instant DEPLOYED_TIME = Instant.now();
    private final EquipmentCommandService equipmentCommandService;
    private static final Operator SAMPLE_ORG_USER_OPERATOR = Operator.createOrgOperator("sampleUserId", "sampleUserName", Set.of(ORG_ADMIN), "sampleOrgId", HUMAN_USER, "/equipments");

    @GetMapping(value = "/about")
    public String about() {
        equipmentCommandService.createEquipment(new CreateEquipmentCommand("test"), SAMPLE_ORG_USER_OPERATOR);
        return "Running! Started at " + DEPLOYED_TIME;
    }

    @GetMapping("/favicon.ico")
    public void dummyFavicon() {
        //nop
    }
}
