package me.wilsonfranca.autoconfigure.dynamodb;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

@AutoConfiguration
@ConditionalOnClass(DynamoDbClient.class)
@EnableConfigurationProperties(DynamoDbProperties.class)
public class DynamoDbAutoconfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DynamoDbClient dynamoDbClient(ObjectProvider<DynamoDbClientBuilderCustomizer> customizers) {
        DynamoDbClientBuilder builder = DynamoDbClient.builder();
        customizers.orderedStream().forEach(customizer -> customizer.customize(builder));
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean(DynamoDbConnectionDetails.class)
    public PropertiesDynamoDbConnectionDetails propertiesDynamoDbConnectionDetails(DynamoDbProperties dynamoDbProperties) {
        return new PropertiesDynamoDbConnectionDetails(dynamoDbProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "dynamodb.endpoint-override")
    public EndpointOverrideDynamoDbClientBuilderCustomizer endpointOverrideDynamoDbClientBuilderCustomizer(DynamoDbProperties dynamoDbProperties) {
        return new EndpointOverrideDynamoDbClientBuilderCustomizer(dynamoDbProperties.endpointOverride());
    }

}
