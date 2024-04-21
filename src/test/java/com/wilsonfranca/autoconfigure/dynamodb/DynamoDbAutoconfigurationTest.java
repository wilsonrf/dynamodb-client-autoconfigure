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
package com.wilsonfranca.autoconfigure.dynamodb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DynamoDbAutoconfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DynamoDbAutoconfiguration.class));


    @Test
    void clientExists(){
        contextRunner.run(context -> assertTrue(context.containsBean("dynamoDbClient")));
    }

    @Test
    void shouldCreatePropertiesConnectionDetails(){
        contextRunner
                .withUserConfiguration(DynamoDbTestConfiguration.class)
                .withPropertyValues("dynamodb.endpoint-override=http://localhost:8000")
                .run(context -> assertThat(context).hasSingleBean(PropertiesDynamoDbConnectionDetails.class)
            );
    }

    @Test
    void whenProvideCustomConnectionDetailsThenUseIt(){
        contextRunner
                .withBean(DynamoDbConnectionDetails.class, CustomDynamoDbConnectionDetails::new)
                .run(context -> {
                    assertThat(context).hasSingleBean(CustomDynamoDbConnectionDetails.class);
                    assertThat(context).doesNotHaveBean(PropertiesDynamoDbConnectionDetails.class);
                }
        );
    }

    @Test
    void shouldHaveEndpointOverrideCustomizerWhenEndpointOverrideIsProvided(){
        contextRunner
                .withPropertyValues("dynamodb.endpoint-override=http://localhost:8000")
                .run(context -> assertThat(context).hasSingleBean(EndpointOverrideDynamoDbClientBuilderCustomizer.class)
        );
    }

    private static class CustomDynamoDbConnectionDetails implements DynamoDbConnectionDetails {

        @Override
        public String endpointOverride() {
            return "http://custom.localhost:8000";
        }
    }

}