package com.company.andy.feature.maintenance.domain;

import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.feature.org.maintenance.domain.MaintenanceRecord;
import org.junit.jupiter.api.Test;

import static com.company.andy.TestFixture.randomDescription;
import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomEquipmentStatus;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaintenanceRecordTest {

    @Test
    void should_create_maintenance_record() {
        OrgActor actor = randomHumanUserOrgActor();

        MaintenanceRecord maintenanceRecord = new MaintenanceRecord("equipment",
                "name",
                randomEquipmentStatus(),
                randomDescription(),
                actor);

        assertNotNull(maintenanceRecord.getId());
    }
}
