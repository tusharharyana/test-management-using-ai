package com.example.demo.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EVALUATION_QUEUE =
            "ai.evaluation.queue";

    public static final String EVALUATION_EXCHANGE =
            "ai.evaluation.exchange";

    public static final String EVALUATION_ROUTING_KEY =
            "ai.evaluation.routing.key";


    @Bean
    public Queue evaluationQueue() {

        return new Queue(
                EVALUATION_QUEUE,
                true
        );
    }


    @Bean
    public DirectExchange evaluationExchange() {

        return new DirectExchange(
                EVALUATION_EXCHANGE
        );
    }


    @Bean
    public Binding evaluationBinding(
            Queue evaluationQueue,
            DirectExchange evaluationExchange
    ) {

        return BindingBuilder
                .bind(evaluationQueue)
                .to(evaluationExchange)
                .with(EVALUATION_ROUTING_KEY);
    }
}