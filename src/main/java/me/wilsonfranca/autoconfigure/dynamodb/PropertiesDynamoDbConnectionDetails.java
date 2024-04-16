package me.wilsonfranca.autoconfigure.dynamodb;

public class PropertiesDynamoDbConnectionDetails implements DynamoDbConnectionDetails {

    private final DynamoDbProperties dynamoDbProperties;

    public PropertiesDynamoDbConnectionDetails(DynamoDbProperties dynamoDbProperties) {
        this.dynamoDbProperties = dynamoDbProperties;
    }

    @Override
    public String endpointOverride() {
        return this.dynamoDbProperties.endpointOverride();
    }
}
