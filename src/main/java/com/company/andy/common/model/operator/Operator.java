package com.company.andy.common.model.operator;

import com.company.andy.common.model.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

import static com.company.andy.common.model.Role.PLATFORM;
import static com.company.andy.common.model.operator.OperatorType.ORG_OPERATOR;
import static com.company.andy.common.model.operator.OperatorType.PLATFORM_OPERATOR;
import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

@Getter
@EqualsAndHashCode
public class Operator {
    public static final String PLATFORM_OPERATOR_ID = "PLATFORM_001";
    public static final String PLATFORM_OPERATOR_NAME = "PLATFORM";

    private final String id;
    private final String name;
    private final Set<Role> roles;
    private final String orgId;
    private final OperatorSource source;
    private final OperatorType type;

    public static Operator createOrgOperator(String id, String name, Set<Role> roles, String orgId, OperatorSource source) {
        requireNonBlank(id, "id must not be blank.");
        requireNonBlank(name, "name must not be blank.");
        requireNonNull(roles, "roles must not be null.");
        requireNonBlank(orgId, "orgId must not be blank.");
        requireNonNull(source, "source must not be null.");

        return new Operator(id, name, Set.copyOf(roles), orgId, source, ORG_OPERATOR);
    }

    public static Operator createPlatformOperator(OperatorSource source) {
        requireNonNull(source, "source must not be null.");

        return new Operator(PLATFORM_OPERATOR_ID, PLATFORM_OPERATOR_NAME, Set.of(PLATFORM), null, source, PLATFORM_OPERATOR);
    }

    private Operator(String id, String name, Set<Role> roles, String orgId, OperatorSource source, OperatorType type) {
        this.id = id;
        this.name = name;
        this.roles = roles;
        this.orgId = orgId;
        this.source = source;
        this.type = type;
    }

}
