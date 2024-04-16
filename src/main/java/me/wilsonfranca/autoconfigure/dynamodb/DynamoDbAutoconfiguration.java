package me.wilsonfranca.autoconfigure.dynamodb;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@AutoConfiguration
@ConditionalOnClass(DynamoDbClient.class)
@EnableConfigurationProperties(DynamoDbProperties.class)
public class DynamoDbAutoconfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.create();
    }

    @Bean
    @ConditionalOnMissingBean(DynamoDbConnectionDetails.class)
    public PropertiesDynamoDbConnectionDetails propertiesDynamoDbConnectionDetails(DynamoDbProperties dynamoDbProperties) {
        return new PropertiesDynamoDbConnectionDetails(dynamoDbProperties);
    }

}
