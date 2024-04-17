package me.wilsonfranca.service.connection.dynamodb;

import org.testcontainers.containers.GenericContainer;

public final class DynamoDbContainer extends GenericContainer<DynamoDbContainer> {

    public DynamoDbContainer(String imageName) {
        super(imageName);
    }
}
