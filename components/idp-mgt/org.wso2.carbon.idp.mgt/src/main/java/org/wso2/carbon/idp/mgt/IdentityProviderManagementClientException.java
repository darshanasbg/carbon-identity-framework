/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.idp.mgt;

/**
 * Exception class for IDP Management client related exceptions.
 */
public class IdentityProviderManagementClientException extends IdentityProviderManagementException {

    private String description;

    public IdentityProviderManagementClientException(String message) {

        super(message);
    }

    public IdentityProviderManagementClientException(String message, Throwable cause) {

        super(message, cause);
    }

    public IdentityProviderManagementClientException(String errorCode, String message) {

        super(errorCode, message);
    }

    public IdentityProviderManagementClientException(String errorCode, String message, Throwable throwable) {

        super(errorCode, message, throwable);
    }

    public IdentityProviderManagementClientException(String errorCode, String message, String description) {

        super(errorCode, message);
        this.description = description;
    }

    public String getDescription() {

        return description;
    }
}
