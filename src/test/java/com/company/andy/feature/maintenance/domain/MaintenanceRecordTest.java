package com.company.andy.feature.maintenance.domain;

import com.company.andy.common.model.operator.Operator;
import com.company.andy.feature.equipment.EquipmentTextFixture;
import org.junit.jupiter.api.Test;

import static com.company.andy.CommonRandomTestFixture.randomDescription;
import static com.company.andy.CommonRandomTestFixture.randomOrgUserOperator;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaintenanceRecordTest {

    @Test
    void should_create_maintenance_record() {
        Operator operator = randomOrgUserOperator();

        MaintenanceRecord maintenanceRecord = new MaintenanceRecord("equipment",
                "name",
                EquipmentTextFixture.randomEquipmentStatus(),
                randomDescription(),
                operator);

        assertNotNull(maintenanceRecord.getId());
    }
}
