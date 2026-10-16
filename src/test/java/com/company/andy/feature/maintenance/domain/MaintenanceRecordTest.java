package com.company.andy.feature.maintenance.domain;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.domain.Equipment;
import org.junit.jupiter.api.Test;

import static com.company.andy.TestFixture.randomDescription;
import static com.company.andy.TestFixture.randomMemberActor;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomEquipmentName;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomEquipmentStatus;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaintenanceRecordTest {

    @Test
    void should_create_maintenance_record() {
        Actor actor = randomMemberActor();
        Equipment equipment = new Equipment(randomEquipmentName(), actor.requireOrgId());

        MaintenanceRecord maintenanceRecord = new MaintenanceRecord(equipment,
                randomEquipmentStatus(),
                randomDescription(),
                actor.requireOrgId());

        assertNotNull(maintenanceRecord.getId());
    }
}
