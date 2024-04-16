package me.wilsonfranca.autoconfigure.dynamodb;

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
        contextRunner.run(context -> {
            assertTrue(context.containsBean("dynamoDbClient"));
        });
    }

    @Test
    void shouldCreatePropertiesConnectionDetails(){
        contextRunner
                .withUserConfiguration(DynamoDbTestConfiguration.class)
                .withPropertyValues("dynamodb.endpoint-override=http://localhost:8000")
                .run(context -> {
                    assertThat(context).hasSingleBean(PropertiesDynamoDbConnectionDetails.class);
                }
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

    private static class CustomDynamoDbConnectionDetails implements DynamoDbConnectionDetails {

        @Override
        public String endpointOverride() {
            return "http://custom.localhost:8000";
        }
    }

}