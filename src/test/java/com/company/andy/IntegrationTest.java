package com.company.andy;

import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.event.DomainEventType;
import com.company.andy.common.event.consume.EventConsumer;
import com.company.andy.common.event.publish.PublishingDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import redis.embedded.RedisServer;

import java.io.IOException;

import static com.company.andy.common.util.CommonUtils.mongoConcatFields;
import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@ActiveProfiles("it")
//@ActiveProfiles("it-local")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class IntegrationTest {
    private static RedisServer redisServer;

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    @Autowired
    protected EventConsumer eventConsumer;

    @Autowired
    protected Environment environment;

    @BeforeAll
    public static void beforeAll() {
        try {
            // RedisServer is only used by application-it.yaml, but not application-it-local.yaml as it uses real Redis server from local machine
            redisServer = new RedisServer(6126);
            redisServer.start();
        } catch (IOException e) {
            log.error("Failed to start embedded Redis server.", e);
        }
    }

    @AfterAll
    public static void afterAll() throws IOException {
        redisServer.stop();
    }

    protected <T extends DomainEvent> T latestEventFor(String arId, DomainEventType type, Class<T> eventClass) {
        requireNonBlank(arId, "arId must not be blank.");
        requireNonNull(type, "type must not be null.");
        requireNonNull(eventClass, "eventClass must not be null.");

        Query query = query(where(mongoConcatFields(PublishingDomainEvent.Fields.event, DomainEvent.Fields.arId)).is(arId)
                .and(mongoConcatFields(PublishingDomainEvent.Fields.event, DomainEvent.Fields.type)).is(type))
                .with(by(DESC, PublishingDomainEvent.Fields.raisedAt));
        PublishingDomainEvent domainEvent = mongoTemplate.findOne(query, PublishingDomainEvent.class);
        return domainEvent == null ? null : (T) domainEvent.getEvent();
    }
}
