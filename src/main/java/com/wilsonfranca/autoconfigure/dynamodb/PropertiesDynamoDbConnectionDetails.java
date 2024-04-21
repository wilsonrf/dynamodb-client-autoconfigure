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
/**
 * Adapts {@link DynamoDbProperties} to {@link DynamoDbConnectionDetails}.
 * @author Wilson da Rocha França
 * @since 1.0.0
 */
public class PropertiesDynamoDbConnectionDetails implements DynamoDbConnectionDetails {

    /**
     * The DynamoDB properties to be adapted.
     */
    private final DynamoDbProperties dynamoDbProperties;

    /**
     * Creates a new {@link PropertiesDynamoDbConnectionDetails}.
     * @param dynamoDbProperties The DynamoDB properties.
     */
    public PropertiesDynamoDbConnectionDetails(DynamoDbProperties dynamoDbProperties) {
        this.dynamoDbProperties = dynamoDbProperties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String endpointOverride() {
        return this.dynamoDbProperties.endpointOverride();
    }
}
