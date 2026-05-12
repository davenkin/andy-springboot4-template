package com.company.andy.feature.equipment.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.company.andy.CommonRandomTestFixture.randomOrgUserActor;
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
        Equipment equipment = new Equipment("name", randomOrgUserActor());

        equipmentDomainService.updateEquipmentName(equipment, "newName", randomOrgUserActor());

        assertEquals("newName", equipment.getName());
    }
}