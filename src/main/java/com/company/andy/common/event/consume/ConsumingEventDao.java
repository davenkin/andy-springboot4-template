package com.company.andy.common.event.consume;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static com.company.andy.common.event.consume.ConsumingEvent.Fields.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

// Upon consuming, record the event in DB to avoid duplicated event consuming
@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumingEventDao {
    private final MongoTemplate mongoTemplate;

    // return "true" means this event has never been consumed before
    public boolean markEventAsConsumedByHandler(ConsumingEvent consumingEvent, AbstractEventHandler<?> handler) {
        Query query = query(where(eventId).is(consumingEvent.getEventId()).and(ConsumingEvent.Fields.handler).is(handler.getName()));

        Update update = new Update()
                .setOnInsert(type, consumingEvent.getType())
                .setOnInsert(event, consumingEvent.getEvent())
                .setOnInsert(consumedAt, consumingEvent.getConsumedAt());

        UpdateResult result = this.mongoTemplate.update(ConsumingEvent.class)
                .matching(query)
                .apply(update)
                .upsert();

        return result.getMatchedCount() == 0;
    }

    public boolean exists(String eventId) {
        Query query = query(where(ConsumingEvent.Fields.eventId).is(eventId));
        return this.mongoTemplate.exists(query, ConsumingEvent.class);
    }

    public boolean exists(String eventId, AbstractEventHandler<?> handler) {
        Query query = query(where(ConsumingEvent.Fields.eventId).is(eventId).and(ConsumingEvent.Fields.handler).is(handler.getName()));
        return this.mongoTemplate.exists(query, ConsumingEvent.class);
    }
}
