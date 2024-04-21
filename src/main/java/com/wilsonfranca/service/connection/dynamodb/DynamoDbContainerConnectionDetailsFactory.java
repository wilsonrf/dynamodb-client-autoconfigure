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

import com.wilsonfranca.autoconfigure.dynamodb.DynamoDbConnectionDetails;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;

/**
 * A factory for creating {@link DynamoDbConnectionDetails} from a {@link DynamoDbContainer}.
 * @author Wilson da Rocha França
 * @since 1.0.0
 */
public class DynamoDbContainerConnectionDetailsFactory extends ContainerConnectionDetailsFactory<DynamoDbContainer, DynamoDbConnectionDetails> {

    private static final String DYNAMODB_CONNECTION_NAME = "dynamodb";

    /**
     * Creates a new {@link DynamoDbContainerConnectionDetailsFactory}.
     */
    public DynamoDbContainerConnectionDetailsFactory() {
        super(DYNAMODB_CONNECTION_NAME);
    }

    /**
     * Creates a new {@link DynamoDbContainerConnectionDetails} from the given {@link ContainerConnectionSource}.
     *
     * @param source the source for the connection details.
     * @return the connection details.
     */
    @Override
    protected DynamoDbConnectionDetails getContainerConnectionDetails(ContainerConnectionSource<DynamoDbContainer> source) {
        return new DynamoDbContainerConnectionDetails(source);
    }

    /**
     * {@link DynamoDbConnectionDetails} implementation for a {@link DynamoDbContainer}.
     */
    protected static class DynamoDbContainerConnectionDetails
            extends ContainerConnectionDetails<DynamoDbContainer>
            implements DynamoDbConnectionDetails {

        /**
         * Creates a new {@link DynamoDbContainerConnectionDetails} from the given {@link ContainerConnectionSource}.
         *
         * @param source the source for the connection details.
         */
        protected DynamoDbContainerConnectionDetails(ContainerConnectionSource<DynamoDbContainer> source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String endpointOverride() {
            return "http://" + getContainer().getHost() + ":" + getContainer().getFirstMappedPort();
        }
    }
}
