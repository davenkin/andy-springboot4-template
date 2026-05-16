package com.company.andy.feature.maintenance.domain;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.EquipmentTestFixture;
import org.junit.jupiter.api.Test;

import static com.company.andy.TestFixture.randomDescription;
import static com.company.andy.TestFixture.randomOrgUserActor;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomEquipmentStatus;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaintenanceRecordTest {

    @Test
    void should_create_maintenance_record() {
        Actor actor = randomOrgUserActor();

        MaintenanceRecord maintenanceRecord = new MaintenanceRecord("equipment",
                "name",
                randomEquipmentStatus(),
                randomDescription(),
                actor);

        assertNotNull(maintenanceRecord.getId());
    }
}
