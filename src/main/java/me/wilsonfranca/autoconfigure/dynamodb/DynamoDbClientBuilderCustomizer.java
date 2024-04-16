package me.wilsonfranca.autoconfigure.dynamodb;

import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

@FunctionalInterface
public interface DynamoDbClientBuilderCustomizer {

    void customize(DynamoDbClientBuilder builder);
}
