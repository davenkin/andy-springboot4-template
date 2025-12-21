package com.company.andy.common.event.publish;

import com.company.andy.common.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.company.andy.common.event.publish.DomainEventPublishStatus.*;
import static com.company.andy.common.event.publish.PublishingDomainEvent.Fields.*;
import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static com.company.andy.common.util.Constants.MONGO_ID;
import static java.util.Objects.requireNonNull;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

// Before publishing to messaging middleware, events are staged(saved) into database in the same transaction that handles business logic
@Slf4j
@Component
@RequiredArgsConstructor
public class PublishingDomainEventDao {
    private static final int MAX_PUBLISH_COUNT = 3;
    private final MongoTemplate mongoTemplate;

    public void stage(List<DomainEvent> events) {
        requireNonNull(events, "Domain events must not be null.");
        List<PublishingDomainEvent> publishingDomainEvents = events.stream().map(PublishingDomainEvent::new).toList();
        mongoTemplate.insertAll(publishingDomainEvents);
    }

    public List<DomainEvent> stagedEvents(String startId, int limit) {
        requireNonBlank(startId, "Start ID must not be blank.");

        Query query = query(where(status).in(CREATED, PUBLISH_FAILED)
                .and(MONGO_ID).gt(startId)
                .and(publishedCount).lt(MAX_PUBLISH_COUNT))
                .with(by(ASC, raisedAt))
                .limit(limit);
        return mongoTemplate.find(query, PublishingDomainEvent.class).stream().map(PublishingDomainEvent::getEvent).toList();
    }


    public void successPublish(String eventId) {
        requireNonBlank(eventId, "Domain event ID must not be blank.");
        Query query = Query.query(where(MONGO_ID).is(eventId));
        Update update = new Update();
        update.set(status, PUBLISH_SUCCEED.name()).inc(publishedCount);
        mongoTemplate.updateFirst(query, update, PublishingDomainEvent.class);
    }

    public void failPublish(String eventId) {
        requireNonBlank(eventId, "Domain event ID must not be blank.");
        Query query = Query.query(where(MONGO_ID).is(eventId));
        Update update = new Update();
        update.set(status, PUBLISH_FAILED.name()).inc(publishedCount);
        mongoTemplate.updateFirst(query, update, PublishingDomainEvent.class);
    }

    public PublishingDomainEvent byId(String eventId) {
        requireNonBlank(eventId, "Event ID must not be blank.");
        Query query = Query.query(where(MONGO_ID).is(eventId));
        return mongoTemplate.findOne(query, PublishingDomainEvent.class);
    }

}
