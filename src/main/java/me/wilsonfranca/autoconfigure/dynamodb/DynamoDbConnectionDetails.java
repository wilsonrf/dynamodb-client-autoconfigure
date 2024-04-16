package me.wilsonfranca.autoconfigure.dynamodb;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

public interface DynamoDbConnectionDetails extends ConnectionDetails {
    public String endpointOverride();
}
