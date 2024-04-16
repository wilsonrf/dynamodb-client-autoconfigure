package me.wilsonfranca.autoconfigure.dynamodb;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.testcontainers.containers.GenericContainer;
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

@Testcontainers(disabledWithoutDocker = true)
class DynamoDbAutoconfigurationIntegrationTest {

    static final int DYNAMO_DB_PORT = 8000;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DynamoDbAutoconfigurationIntegrationTest.class);
    private static final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);

    @Container
    static final GenericContainer dynamoDb = new GenericContainer("amazon/dynamodb-local:latest")
            .withLogConsumer(logConsumer)
            .withExposedPorts(DYNAMO_DB_PORT);

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DynamoDbAutoconfiguration.class))
            .withPropertyValues("dynamodb.endpoint-override=http://localhost:" + dynamoDb.getMappedPort(DYNAMO_DB_PORT));

    private DynamoDbTable<TestTable> testTableDynamoDbTable;

    @Test
    void clientCanConnectWithEndpointOverride() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("dynamoDbClient"));
            assertNotNull(context.getBean(DynamoDbClient.class));
            DynamoDbClient dynamoDbClient = context.getBean(DynamoDbClient.class);
            testTableDynamoDbTable = DynamoDbEnhancedClient
                    .builder()
                    .dynamoDbClient(dynamoDbClient)
                    .build()
                    .table("test", TEST_TABLE_SCHEMA);
            testTableDynamoDbTable.createTable(builder -> builder.provisionedThroughput(t ->
                    t.readCapacityUnits(5L).writeCapacityUnits(5L)));
            testTableDynamoDbTable.putItem(new TestTable("1"));
            assertThat(testTableDynamoDbTable.scan().items().stream().toList()).hasSize(1);
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