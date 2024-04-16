package me.wilsonfranca.autoconfigure.dynamodb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

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

}