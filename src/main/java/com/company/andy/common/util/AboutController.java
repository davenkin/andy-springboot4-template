package com.company.andy.common.util;

import com.company.andy.common.model.operator.UserOperator;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import static com.company.andy.common.model.Role.ORG_ADMIN;

@Slf4j
@Hidden
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AboutController {
    private static final Instant DEPLOYED_TIME = Instant.now();
    private final RestClient restClient;
    private final EquipmentCommandService equipmentCommandService;

    @GetMapping(value = "/about")
    public String about() {
        log.info("about 1");
        String body = this.restClient.get().uri("http://localhost:5125/about2").retrieve().body(String.class);
        log.info("about 2 {}", body);
        this.equipmentCommandService.createEquipment(CreateEquipmentCommand.builder().name("ab").build(), UserOperator.of("sampleUserId", "sampleUserName", ORG_ADMIN, "sampleOrgId"));
        return "Running! Started at " + DEPLOYED_TIME;
    }

    @GetMapping(value = "/about2")
    public String about2() {
        log.info("about 2");
        return "Running 2! Started at " + DEPLOYED_TIME;
    }


    @GetMapping("/favicon.ico")
    public void dummyFavicon() {
        //nop
    }
}
