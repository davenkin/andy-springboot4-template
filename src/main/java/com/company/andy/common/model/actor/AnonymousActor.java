package com.company.andy.common.model.actor;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import static com.company.andy.common.model.actor.ActorSource.ANONYMOUS;
import static com.company.andy.common.model.actor.ActorType.ANONYMOUS_ACTOR;
import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldNameConstants
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class AnonymousActor extends Actor {
    private AnonymousActor(String initiator) {
        super("ANONYMOUS", "ANONYMOUS", ANONYMOUS_ACTOR, ANONYMOUS, initiator);
    }

    public static AnonymousActor anonymousActor(String initiator) {
        return new AnonymousActor(initiator);
    }
}
