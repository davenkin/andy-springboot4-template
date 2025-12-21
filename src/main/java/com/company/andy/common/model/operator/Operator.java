package com.company.andy.common.model.operator;

import com.company.andy.common.model.Role;

import java.util.Set;

// Operator should contain enough context data of the current user

public interface Operator {
    String getId();

    String getName();

    Set<Role> getRoles();

    String getOrgId();
}
