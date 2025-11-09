package no.hvl.group17.feedapp.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue voteEventsQueue() {
        return new Queue("vote-events", false);
    }


    @Bean
    public Queue pollEventsQueue() {
        return new Queue("poll-events", false);
    }
}