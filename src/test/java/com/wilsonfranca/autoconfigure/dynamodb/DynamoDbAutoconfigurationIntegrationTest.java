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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

/**
 * Integration tests for {@link DynamoDbAutoconfiguration}.
 * @author Wilson da Rocha França
 */
@Testcontainers(disabledWithoutDocker = true)
class DynamoDbAutoconfigurationIntegrationTest {

    static final int DYNAMO_DB_PORT = 8000;
    private static final Logger logger = LoggerFactory.getLogger(DynamoDbAutoconfigurationIntegrationTest.class);
    private static final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);

    @Container
    private static final DynamoDbContainer dynamoDb = new DynamoDbContainer()
            .withLogConsumer(logConsumer)
            .withExposedPorts(DYNAMO_DB_PORT);

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DynamoDbAutoconfiguration.class));


    @Test
    void clientCanConnectWithEndpointOverride() {
        contextRunner
                .withPropertyValues("dynamodb-client.endpoint-override=http://localhost:" + dynamoDb.getMappedPort(DYNAMO_DB_PORT))
                .run(context -> {
            assertTrue(context.containsBean("dynamoDbClient"));
            assertNotNull(context.getBean(DynamoDbClient.class));
            DynamoDbClient dynamoDbClient = context.getBean(DynamoDbClient.class);
                    DynamoDbTable<TestTable> testTableDynamoDbTable = DynamoDbEnhancedClient
                    .builder()
                    .dynamoDbClient(dynamoDbClient)
                    .build()
                    .table("test", TEST_TABLE_SCHEMA);
            testTableDynamoDbTable.createTable(builder -> builder.provisionedThroughput(t ->
                    t.readCapacityUnits(5L).writeCapacityUnits(5L)));
            testTableDynamoDbTable.putItem(new TestTable("1"));
            assertThat(testTableDynamoDbTable.scan().items().stream().toList()).hasSize(1);
            testTableDynamoDbTable.deleteTable();

        });
    }

    @DynamoDbBean
    private static class TestTable {
        private String id;

        public TestTable() {
        }

        public TestTable(String id) {
            this.id = id;
        }

        @DynamoDbPartitionKey
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    private static final StaticTableSchema<TestTable> TEST_TABLE_SCHEMA = StaticTableSchema.builder(TestTable.class)
            .newItemSupplier(TestTable::new)
            .addAttribute(String.class, a -> a.name("id")
                    .getter(TestTable::getId)
                    .setter(TestTable::setId)
                    .tags(primaryPartitionKey()))
            .build();
}