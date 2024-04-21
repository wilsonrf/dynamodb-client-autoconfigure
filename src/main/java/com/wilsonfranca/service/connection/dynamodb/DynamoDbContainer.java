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
package com.wilsonfranca.service.connection.dynamodb;

import org.testcontainers.containers.GenericContainer;

/**
 * A container based on a {@link GenericContainer} for DynamoDBLocal.
 * @author Wilson da Rocha França
 * @since 1.0.0
 */
public final class DynamoDbContainer extends GenericContainer<DynamoDbContainer> {

    /**
     * The default image for the container.
     */
    private static final String DYNAMO_DB_IMAGE = "amazon/dynamodb-local";

    /**
     * The default tag for the container.
     */
    private static final String DYNAMO_DB_IMAGE_TAG = "latest";

    /**
     * Creates a new container with the default image and tag.
     */
    public DynamoDbContainer() {
        this(DYNAMO_DB_IMAGE, DYNAMO_DB_IMAGE_TAG);
    }

    /**
     * Creates a new container with the specified tag.
     *
     * @param tag the tag for the container
     */
    public DynamoDbContainer(String tag) {
        this(DYNAMO_DB_IMAGE, tag);
    }

    /**
     * Creates a new container with the specified image and tag.
     *
     * @param image the image for the container
     * @param tag the tag for the container
     */
    private DynamoDbContainer(String image, String tag) {
        super(image + ":" + tag);
    }
}
