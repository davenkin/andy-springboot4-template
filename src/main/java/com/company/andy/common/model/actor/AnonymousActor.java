package com.company.andy.common.model.actor;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.company.andy.common.model.actor.ActorSource.ANONYMOUS;
import static com.company.andy.common.model.actor.ActorType.ANONYMOUS_ACTOR;
import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class AnonymousActor extends Actor {
    private static final String ANONYMOUS_ACTOR_ID = "ANONYMOUS";
    private static final String ANONYMOUS_ACTOR_NAME = "Anonymous";

    private AnonymousActor(String initiator) {
        super(ANONYMOUS_ACTOR_ID, ANONYMOUS_ACTOR_NAME, ANONYMOUS_ACTOR, ANONYMOUS, initiator);
    }

    public static AnonymousActor createAnonymousActor(String initiator) {
        return new AnonymousActor(initiator);
    }
}
