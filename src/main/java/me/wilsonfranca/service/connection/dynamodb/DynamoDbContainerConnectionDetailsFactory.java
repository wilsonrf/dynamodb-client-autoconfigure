package me.wilsonfranca.service.connection.dynamodb;

import me.wilsonfranca.autoconfigure.dynamodb.DynamoDbConnectionDetails;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;

public class DynamoDbContainerConnectionDetailsFactory extends ContainerConnectionDetailsFactory<DynamoDbContainer, DynamoDbConnectionDetails> {

    public DynamoDbContainerConnectionDetailsFactory() {
        super("dynamodb");
    }

    @Override
    protected DynamoDbConnectionDetails getContainerConnectionDetails(ContainerConnectionSource<DynamoDbContainer> source) {
        return new DynamoDbContainerConnectionDetails(source);
    }

    protected static class DynamoDbContainerConnectionDetails
            extends ContainerConnectionDetails<DynamoDbContainer>
            implements DynamoDbConnectionDetails {

        protected DynamoDbContainerConnectionDetails(ContainerConnectionSource<DynamoDbContainer> source) {
            super(source);
        }

        @Override
            public String endpointOverride() {
                return "http://" + getContainer().getHost() + ":" + getContainer().getFirstMappedPort();
            }
        }
}
