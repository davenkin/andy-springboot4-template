package com.company.andy.common.model.operator;

import com.company.andy.common.model.Role;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

import static com.company.andy.common.model.Role.PLATFORM;
import static lombok.AccessLevel.PRIVATE;

// Represents a non-human operator, used in non-human interaction scenarios such as EventHandlers and background Jobs

@EqualsAndHashCode
@NoArgsConstructor(access = PRIVATE)
public class PlatformOperator implements Operator {
    public static final PlatformOperator PLATFORM_OPERATOR = new PlatformOperator();
    public static final String PLATFORM_ID = "PLATFORM001";
    public static final String PLATFORM_NAME = "PLATFORM";

    @Override
    public String getId() {
        return PLATFORM_ID;
    }

    @Override
    public String getName() {
        return PLATFORM_NAME;
    }

    @Override
    public Set<Role> getRoles() {
        return Set.of(PLATFORM);
    }

    @Override
    public String getOrgId() {
        throw new IllegalArgumentException("PlatformOperator does not support getOrgId().");
    }
}
