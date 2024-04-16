package me.wilsonfranca.autoconfigure.dynamodb;

import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

public class EndpointOverrideDynamoDbClientBuilderCustomizer implements DynamoDbClientBuilderCustomizer, Ordered {

    private final String endpointOverride;

    public EndpointOverrideDynamoDbClientBuilderCustomizer(String endpointOverride) {
        this.endpointOverride = endpointOverride;
    }

    @Override
    public void customize(DynamoDbClientBuilder builder) {
        if (StringUtils.hasText(endpointOverride)) {
            builder.endpointOverride(URI.create(endpointOverride));
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
