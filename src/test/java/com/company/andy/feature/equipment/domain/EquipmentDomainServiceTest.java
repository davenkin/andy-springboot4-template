package com.company.andy.feature.equipment.domain;

import com.company.andy.feature.org.equipment.domain.Equipment;
import com.company.andy.feature.org.equipment.domain.EquipmentDomainService;
import com.company.andy.feature.org.equipment.domain.EquipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EquipmentDomainServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentDomainService equipmentDomainService;

    @Test
    void should_update_name() {
        Mockito.when(equipmentRepository.existsByName(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        Equipment equipment = new Equipment("name", randomHumanUserOrgActor());

        equipmentDomainService.updateEquipmentName(equipment, "newName", randomHumanUserOrgActor());

        assertEquals("newName", equipment.getName());
    }
}