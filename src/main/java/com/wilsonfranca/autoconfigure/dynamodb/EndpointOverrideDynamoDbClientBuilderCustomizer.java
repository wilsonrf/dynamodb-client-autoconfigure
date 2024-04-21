/*
 * Copyright 2024 Wilson da Rocha França
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wilsonfranca.autoconfigure.dynamodb;

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
