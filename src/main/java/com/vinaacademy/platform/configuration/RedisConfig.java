package com.vinaacademy.platform.configuration;

import com.vinaacademy.platform.feature.email.mq.redis.EmailSubscriber;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static com.vinaacademy.platform.feature.email.mq.redis.EmailQueueConstant.EMAIL_CHANNEL;

@ConditionalOnProperty(name = "spring.data.redis.enabled", havingValue = "true")
@Configuration
@Log4j2
public class RedisConfig {
    @Autowired
    private LettuceConnectionFactory connectionFactory;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        if (!isRedisAvailable()) {
            log.error("Redis is not available");
            return null;
        }

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisMessageListenerContainer emailMessageContainer(RedisConnectionFactory connectionFactory, EmailSubscriber emailSubscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(emailSubscriber, new ChannelTopic(EMAIL_CHANNEL));
        return container;
    }

//    private boolean isRedisAvailable() {
//        try (RedisClient redisClient = RedisClient.create("redis://127.0.0.1:6479")) {
//            RedisCommands<String, String> commands = redisClient.connect().sync();
//            String result = commands.ping();
//            return "PONG".equals(result);
//        } catch (Exception e) {
//            log.error("Redis connection failed: ", e);
//            return false;
//        }
//    }

    private boolean isRedisAvailable() {
        try {
            connectionFactory.getConnection().ping();
            return true;
        } catch (Exception e) {
            log.error("Error connecting to Redis: ", e);
            return false;
        }
    }

}
