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

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

/**
 * Callback interface that can be implemented by beans wishing to customize the
 * {@link DynamoDbClientBuilder} via a {@link DynamoDbClientBuilderCustomizer} whilst building a
 * {@link DynamoDbClient}.
 * @author Wilson da Rocha França
 * @since 1.0.0
 */
@FunctionalInterface
public interface DynamoDbClientBuilderCustomizer {

    /**
     * Customize the {@link DynamoDbClientBuilder}.
     * @param builder the builder to customize
     */
    void customize(DynamoDbClientBuilder builder);
}
