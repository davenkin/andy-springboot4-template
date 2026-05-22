package com.company.andy.feature.maintenance.domain;

import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.feature.equipment.domain.Equipment;
import org.junit.jupiter.api.Test;

import static com.company.andy.TestFixture.randomDescription;
import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static com.company.andy.common.model.OrgRole.ORG_ADMIN;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomEquipmentName;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomEquipmentStatus;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaintenanceRecordTest {

    @Test
    void should_create_maintenance_record() {
        OrgActor actor = randomHumanUserOrgActor(ORG_ADMIN);
        Equipment equipment = new Equipment(randomEquipmentName(), actor);

        MaintenanceRecord maintenanceRecord = new MaintenanceRecord(equipment,
                randomEquipmentStatus(),
                randomDescription(),
                actor);

        assertNotNull(maintenanceRecord.getId());
    }
}
