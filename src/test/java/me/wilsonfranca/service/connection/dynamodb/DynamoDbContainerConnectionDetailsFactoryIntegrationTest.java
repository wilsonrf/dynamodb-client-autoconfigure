package me.wilsonfranca.service.connection.dynamodb;

import me.wilsonfranca.autoconfigure.dynamodb.DynamoDbAutoconfiguration;
import me.wilsonfranca.autoconfigure.dynamodb.DynamoDbConnectionDetails;
import me.wilsonfranca.service.connection.dynamodb.DynamoDbContainerConnectionDetailsFactory.DynamoDbContainerConnectionDetails;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
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
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

@SpringJUnitConfig
@Testcontainers(disabledWithoutDocker = true)
class DynamoDbContainerConnectionDetailsFactoryIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDbContainerConnectionDetailsFactoryIntegrationTest.class);
    private static final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
    private static final int DYNAMODB_PORT = 8000;

    @Container
    @ServiceConnection(value = "dynamoDb")
    static final GenericContainer dynamoDb = new GenericContainer("amazon/dynamodb-local:latest")
            .withExposedPorts(DYNAMODB_PORT)
            .withLogConsumer(logConsumer);

    @Configuration(proxyBeanMethods = false)
    @ImportAutoConfiguration(DynamoDbAutoconfiguration.class)
    static class TestConfiguration {
    }

    @Autowired
    DynamoDbClient dynamoDbClient;

    @Autowired
    DynamoDbConnectionDetails dynamoDbConnectionDetails;

    @Test
    void clientCanConnectWithServiceConnection() {
        assertThat(dynamoDbConnectionDetails).isInstanceOf(DynamoDbContainerConnectionDetails.class);
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