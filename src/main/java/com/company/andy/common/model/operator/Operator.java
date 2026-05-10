package com.company.andy.common.model.operator;

import com.company.andy.common.model.Role;

import java.util.Set;

import static com.company.andy.common.model.Role.PLATFORM;
import static com.company.andy.common.model.operator.OperatorType.ORG_OPERATOR;
import static com.company.andy.common.model.operator.OperatorType.PLATFORM_OPERATOR;
import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

public record Operator(String id,
                       String name,
                       Set<Role> roles,
                       String orgId,
                       OperatorSource source,
                       OperatorType type,
                       String initiator) {
    public static final String PLATFORM_OPERATOR_ID = "PLATFORM_001";
    public static final String PLATFORM_OPERATOR_NAME = "PLATFORM";

    public static Operator createOrgOperator(String id, String name, Set<Role> roles, String orgId, OperatorSource source, String initiator) {
        requireNonBlank(id, "id must not be blank.");
        requireNonBlank(name, "name must not be blank.");
        requireNonNull(roles, "roles must not be null.");
        requireNonBlank(orgId, "orgId must not be blank.");
        requireNonNull(source, "source must not be null.");
        requireNonBlank(initiator, "initiator must not be blank.");

        return new Operator(id, name, Set.copyOf(roles), orgId, source, ORG_OPERATOR, initiator);
    }

    public static Operator createPlatformOperator(OperatorSource source, String initiator) {
        requireNonNull(source, "source must not be null.");
        requireNonBlank(initiator, "initiator must not be blank.");

        return new Operator(PLATFORM_OPERATOR_ID, PLATFORM_OPERATOR_NAME, Set.of(PLATFORM), null, source, PLATFORM_OPERATOR, initiator);
    }

    public boolean isOrgOperator() {
        return ORG_OPERATOR.equals(this.type);
    }

    public boolean isPlatformOperator() {
        return PLATFORM_OPERATOR.equals(this.type);
    }
}
