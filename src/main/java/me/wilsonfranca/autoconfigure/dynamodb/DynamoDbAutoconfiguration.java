package me.wilsonfranca.autoconfigure.dynamodb;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@AutoConfiguration
@ConditionalOnClass(DynamoDbClient.class)
public class DynamoDbAutoconfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.create();
    }

}
