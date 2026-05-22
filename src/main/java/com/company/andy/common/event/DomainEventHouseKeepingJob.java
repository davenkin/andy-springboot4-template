package com.company.andy.common.event;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;

import static com.company.andy.common.event.consume.ConsumingEvent.Fields.consumedAt;
import static com.company.andy.common.event.publish.PublishingDomainEvent.Fields.raisedAt;
import static com.company.andy.common.utils.Constants.CONSUMING_EVENT_COLLECTION;
import static com.company.andy.common.utils.Constants.PUBLISHING_EVENT_COLLECTION;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.mongodb.client.result.DeleteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

// This house keeping job removes old domain events in DB both for publishing and consuming side, freeing more spaces for new events

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventHouseKeepingJob {
  private final MongoTemplate mongoTemplate;

  @Retryable(multiplier = 3, maxRetries = 3)
  public void removeOldPublishingDomainEventsFromMongo(int days) {
    log.info("Start remove old publishing domain events from mongodb.");
    Query query = Query.query(where(raisedAt).lt(now().minus(days, DAYS)));
    DeleteResult result = mongoTemplate.remove(query, PUBLISHING_EVENT_COLLECTION);
    log.info("Removed {} old publishing domain events from mongodb which are more than {} days old.", result.getDeletedCount(), days);
  }

  @Retryable(multiplier = 3, maxRetries = 3)
  public void removeOldConsumingDomainEventsFromMongo(int days) {
    log.info("Start remove old consuming domain events from mongodb.");
    Query query = Query.query(where(consumedAt).lt(now().minus(days, DAYS)));
    DeleteResult result = mongoTemplate.remove(query, CONSUMING_EVENT_COLLECTION);
    log.info("Removed {} old consuming domain events from mongodb which are more than {} days old.", result.getDeletedCount(), days);
  }
}
