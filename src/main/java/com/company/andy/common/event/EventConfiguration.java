package com.company.andy.common.event;

import com.company.andy.common.configuration.profile.DisableForIT;
import com.company.andy.common.event.publish.DomainEventPublishJob;
import com.company.andy.common.event.publish.PublishingDomainEvent;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.bson.Document;
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.MessageListener;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.ExponentialBackOff;
import tools.jackson.databind.json.JsonMapper;

import static com.company.andy.common.util.Constants.PUBLISHING_EVENT_COLLECTION;

@Slf4j
@DisableForIT
@Configuration(proxyBeanMethods = false)
public class EventConfiguration {
    private static final String dltSuffix = "-dlt";

    @Bean(destroyMethod = "stop")
    MessageListenerContainer mongoDomainEventChangeStreamListenerContainer(MongoTemplate mongoTemplate,
                                                                           TaskExecutor taskExecutor,
                                                                           DomainEventPublishJob domainEventPublishJob) {
        MessageListenerContainer container = new DefaultMessageListenerContainer(mongoTemplate, taskExecutor);

        // Get notification on DomainEvent insertion in MongoDB, then publish staged domain events to messaging middleware such as Kafka
        container.register(ChangeStreamRequest.builder(
                        (MessageListener<ChangeStreamDocument<Document>, PublishingDomainEvent>) message -> {
                            domainEventPublishJob.publishStagedDomainEvents(100);
                        })
                .collection(PUBLISHING_EVENT_COLLECTION)
                .filter(new Document("$match", new Document("operationType", OperationType.INSERT.getValue())))
                .build(), PublishingDomainEvent.class);
        container.start();
        return container;
    }

    @Bean
    public DefaultKafkaProducerFactoryCustomizer defaultKafkaProducerFactoryCustomizer(JsonMapper objectMapper) {
        return producerFactory -> producerFactory.setValueSerializer(new JacksonJsonSerializer<>(objectMapper));
    }

    @Bean
    public DefaultKafkaConsumerFactoryCustomizer defaultKafkaConsumerFactoryCustomizer(JsonMapper objectMapper) {
        return consumerFactory -> {
            JacksonJsonDeserializer valueDeserializer = new JacksonJsonDeserializer<>(objectMapper);
            valueDeserializer.addTrustedPackages("*");

            //we must wrap the JsonDeserializer into an ErrorHandlingDeserializer, otherwise deserialization error will result in endless message retry
            consumerFactory.setValueDeserializer(new ErrorHandlingDeserializer<>(valueDeserializer));
        };
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        ExponentialBackOff backOff = new ExponentialBackOff(500L, 2);
        backOff.setMaxAttempts(2); // the message will be processed at most [2 + 1 = 3] times
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> {
                    String dlt = record.topic() + dltSuffix;
                    log.error("Error consuming message[key={}], moving to dead letter topic[{}].", record.key(), dlt, ex);
                    return new TopicPartition(dlt, record.partition());
                }
        );
        return new DefaultErrorHandler(recoverer, backOff);
    }
}


