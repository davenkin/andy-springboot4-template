package com.company.andy.feature.equipment.domain;

import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static com.company.andy.common.model.OrgRole.ORG_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EquipmentDomainServiceTest {

  @Mock
  private EquipmentRepository equipmentRepository;

  @InjectMocks
  private EquipmentDomainService equipmentDomainService;

  @Test
  void should_update_name() {
    Mockito.when(equipmentRepository.existsByName(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
    Equipment equipment = new Equipment("name", randomHumanUserOrgActor(ORG_ADMIN));

    equipmentDomainService.updateEquipmentName(equipment, "newName", randomHumanUserOrgActor(ORG_ADMIN));

    assertEquals("newName", equipment.getName());
  }
}