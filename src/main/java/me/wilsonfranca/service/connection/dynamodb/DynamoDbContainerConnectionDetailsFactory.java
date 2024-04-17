package me.wilsonfranca.service.connection.dynamodb;

import me.wilsonfranca.autoconfigure.dynamodb.DynamoDbConnectionDetails;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;
import org.testcontainers.containers.Container;

public class DynamoDbContainerConnectionDetailsFactory extends ContainerConnectionDetailsFactory<Container<?>, DynamoDbConnectionDetails> {

    public DynamoDbContainerConnectionDetailsFactory() {
        super("dynamodb");
    }

    @Override
    protected DynamoDbConnectionDetails getContainerConnectionDetails(ContainerConnectionSource<Container<?>> source) {
        return new DynamoDbContainerConnectionDetails(source);
    }

    protected static class DynamoDbContainerConnectionDetails
            extends ContainerConnectionDetails<Container<?>>
            implements DynamoDbConnectionDetails {

        protected DynamoDbContainerConnectionDetails(ContainerConnectionSource<Container<?>> source) {
            super(source);
        }

        @Override
            public String endpointOverride() {
                return "http://" + getContainer().getHost() + ":" + getContainer().getFirstMappedPort();
            }
        }
}
