package com.company.andy.common.model.actor;

public enum ActorType {
    MEMBER,// Members under an org
    SUPERVISOR,// Platform users which does not belong to any org
    ORG_SERVICE_CLIENT, // Service client only representing an org and can access resources only under that org
    PLATFORM_SERVICE_CLIENT, // Service client representing the platform and can access platform level resources as well as org level resources
    DEVICE,
    ROBOT,
    WEBHOOK,
    ANONYMOUS
}
