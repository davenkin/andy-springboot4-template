package com.company.andy.feature.equipment.query;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.util.PagedResponse;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.IntStream;

import static com.company.andy.CommonRandomTestFixture.randomOrgUserActor;
import static com.company.andy.feature.equipment.EquipmentTextFixture.randomCreateEquipmentCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EquipmentQueryServiceIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentQueryService equipmentQueryService;

    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Test
    void should_page_equipments() {
        //Prepare data
        Actor actor = randomOrgUserActor();
        IntStream.range(0, 20).forEach(i -> {
            equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor);
        });

        // Fetch data
        PageEquipmentsQuery query = PageEquipmentsQuery.builder().pageSize(12).build();
        PagedResponse<QPagedEquipment> equipments = equipmentQueryService.pageEquipments(query, actor);

        // Verify results
        assertEquals(12, equipments.getContent().size());
    }
}
