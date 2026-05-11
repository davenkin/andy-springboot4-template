package com.company.andy.feature.maintenance.domain;

import com.company.andy.CommonRandomTestFixture;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.EquipmentTextFixture;
import org.junit.jupiter.api.Test;

import static com.company.andy.CommonRandomTestFixture.randomDescription;
import static com.company.andy.CommonRandomTestFixture.randomOrgUserActor;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaintenanceRecordTest {

    @Test
    void should_create_maintenance_record() {
        Actor actor = CommonRandomTestFixture.randomOrgUserActor();

        MaintenanceRecord maintenanceRecord = new MaintenanceRecord("equipment",
                "name",
                EquipmentTextFixture.randomEquipmentStatus(),
                randomDescription(),
                actor);

        assertNotNull(maintenanceRecord.getId());
    }
}
