package com.company.andy.common.model.operator;

import com.company.andy.common.model.Role;
import lombok.EqualsAndHashCode;

import java.util.Set;

import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

// Represents a human operator, used by CommandService and QueryService for human user interaction
@EqualsAndHashCode
public class UserOperator implements Operator {
    private final String userId;
    private final String userName;
    private final Set<Role> roles;
    private final String orgId;

    private UserOperator(String userId, String userName, Role role, String orgId) {
        requireNonBlank(userId, "userId must not be blank.");
        requireNonBlank(userName, "userName must not be blank.");
        requireNonNull(role, "role must not be null.");
        requireNonBlank(orgId, "orgId must not be blank.");

        this.orgId = orgId;
        this.userId = userId;
        this.userName = userName;
        this.roles = Set.of(role);
    }

    public static UserOperator of(String id, String name, Role role, String orgId) {
        return new UserOperator(id, name, role, orgId);
    }

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public String getName() {
        return userName;
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public String getOrgId() {
        return orgId;
    }
}
