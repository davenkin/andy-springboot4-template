package com.company.andy.common.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

import static com.company.andy.common.utils.Constants.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class SecurityUtils {

    public static String getJwtUserName(Jwt jwt) {
        return jwt.getClaimAsString(JWT_CLAIM_PREFERRED_USERNAME);
    }

    public static String getJwtActorType(Jwt jwt) {
        String actorType = jwt.getClaimAsString(JWT_CLAIM_ACTOR_TYPE);
        if (isBlank(actorType)) {
            throw new InvalidBearerTokenException("Cannot obtain actor_type from JWT.");
        }
        return actorType;
    }

    public static String getJwtClient(Jwt jwt) {
        String clientId = jwt.getClaimAsString(JWT_CLAIM_AZP);
        if (isBlank(clientId)) {
            throw new InvalidBearerTokenException("Cannot obtain clientId(azp) from JWT.");
        }
        return clientId;
    }

    public static String getJwtOrgId(Jwt jwt) {
        String orgId = jwt.getClaimAsString(JWT_CLAIM_ORG_ID);
        if (isBlank(orgId)) {
            throw new InvalidBearerTokenException("Cannot obtain org_id from JWT.");
        }
        return orgId;
    }

    public static String getJwtMemberId(Jwt jwt) {
        String memberId = jwt.getClaimAsString(JWT_CLAIM_MEMBER_ID);
        if (isBlank(memberId)) {
            throw new InvalidBearerTokenException("Cannot obtain member_id from JWT.");
        }
        return memberId;
    }

    public static String getJwtSupervisorId(Jwt jwt) {
        String supervisorId = jwt.getClaimAsString(JWT_CLAIM_SUPERVISOR_ID);
        if (isBlank(supervisorId)) {
            throw new InvalidBearerTokenException("Cannot obtain supervisor_id from JWT.");
        }
        return supervisorId;
    }

    public static String getJwtDeviceId(Jwt jwt) {
        String deviceId = jwt.getClaimAsString(JWT_CLAIM_DEVICE_ID);
        if (isBlank(deviceId)) {
            throw new InvalidBearerTokenException("Cannot obtain device_id from JWT.");
        }
        return deviceId;
    }

}
