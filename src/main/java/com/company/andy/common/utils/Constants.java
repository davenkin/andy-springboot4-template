package com.company.andy.common.utils;

public class Constants {
    public static final String MONGO_ID = "_id";
    public static final String ID = "id";
    public static final String TRACE_PARENT = "traceparent";
    public final static String CHINA_TIME_ZONE = "Asia/Shanghai";
    public static final String SYSTEM_ADMIN_ROLE = "SYSTEM_ADMIN";
    public static final String ANONYMOUS_ROLE = "ANONYMOUS";
    public static final String ROLE_PREFIX = "ROLE_";

    // Event
    public static final String KAFKA_DOMAIN_EVENT_TOPIC = "domain-event-topic";
    public static final String PUBLISHING_EVENT_COLLECTION = "publishing-event";
    public static final String CONSUMING_EVENT_COLLECTION = "consuming-event";

    // Cache
    public static final String CACHE_PREFIX = "Cache:";
    public static final String ORG_EQUIPMENTS_CACHE = "ORG_EQUIPMENTS";
    public static final String SYSTEM_SETTINGS_CACHE = "SYSTEM_SETTINGS";

    // Jwt
    public static final String JWT_CLAIM_ORG_ID = "org_id";
    public static final String JWT_CLAIM_PREFERRED_USERNAME = "preferred_username";
    public static final String JWT_CLAIM_REALM_ACCESS = "realm_access";
    public static final String JWT_CLAIM_REALM_ACCESS_ROLES = "roles";
    public static final String SYSTEM_ACTOR_ORG_ID_HEADER = "x-org-id";

    // Regex
    public static final String MOBILE_NUMBER_REGEX = "^[1]([3-9])[0-9]{9}$";
    public static final String EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
}
