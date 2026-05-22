package com.company.andy.feature.demo.domain;

import com.company.andy.common.infrastructure.AbstractMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DemoReservationRepository extends AbstractMongoRepository<DemoReservation> {
}
