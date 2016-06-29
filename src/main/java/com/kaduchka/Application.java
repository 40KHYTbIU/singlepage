package com.kaduchka;

import com.kaduchka.common.Direction;
import com.kaduchka.common.DirectionPropertyEditor;
import com.kaduchka.common.Fields;
import com.kaduchka.common.FieldsPropertyEditor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {

  @Bean
  public static CustomEditorConfigurer customEditorConfigurer() {
    CustomEditorConfigurer configurer = new CustomEditorConfigurer();
    Map<Class<?>, Class<? extends PropertyEditor>> propertyEditorMap = new HashMap<>();

    propertyEditorMap.put(Direction.class, DirectionPropertyEditor.class);
    propertyEditorMap.put(Fields.class, FieldsPropertyEditor.class);

    configurer.setCustomEditors(propertyEditorMap);
    return configurer;
  }

  @Bean
  public RedisConnectionFactory lettuceConnectionFactory(@Value("${redis.host:localhost}") String redisHost,
                                                         @Value("${redis.port:6379}") int redisPort) {
    return new LettuceConnectionFactory(redisHost, redisPort);
  }

  @Bean
  public StringRedisTemplate redisTemplate(@Value("${redis.host:localhost}") String redisHost,
                                           @Value("${redis.port:6379}") int redisPort) {
    StringRedisTemplate redisTemplate = new StringRedisTemplate();
    redisTemplate.setConnectionFactory(lettuceConnectionFactory(redisHost, redisPort));
    return redisTemplate;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
