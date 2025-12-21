package com.company.andy.sample.maintenance.domain;

import com.company.andy.common.model.operator.UserOperator;
import org.junit.jupiter.api.Test;

import static com.company.andy.RandomTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaintenanceRecordTest {

    @Test
    void should_create_maintenance_record() {
        UserOperator operator = randomUserOperator();

        MaintenanceRecord maintenanceRecord = new MaintenanceRecord("equipment",
                "name",
                randomEquipmentStatus(),
                randomDescription(),
                operator);

        assertNotNull(maintenanceRecord.getId());
    }
}
