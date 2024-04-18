/*
 * Copyright 2024 Wilson da Rocha França
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.wilsonfranca.autoconfigure.dynamodb;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    @ConditionalOnBean(DynamoDbConnectionDetails.class)
    public EndpointOverrideDynamoDbClientBuilderCustomizer endpointOverrideDynamoDbClientBuilderCustomizer(DynamoDbConnectionDetails dynamoDbConnectionDetails) {
        return new EndpointOverrideDynamoDbClientBuilderCustomizer(dynamoDbConnectionDetails.endpointOverride());
    }

}
