package me.wilsonfranca.autoconfigure.dynamodb;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dynamodb")
record DynamoDbProperties(String endpointOverride) {
}