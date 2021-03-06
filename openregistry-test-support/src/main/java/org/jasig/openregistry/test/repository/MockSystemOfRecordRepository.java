/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.openregistry.test.repository;

import org.openregistry.core.domain.sor.SoRSpecification;
import org.openregistry.core.repository.SystemOfRecordRepository;

import java.util.Collection;
import java.util.Map;

/**
 * @version $Revision$ $Date$
 * @since 0.1
 */
public final class MockSystemOfRecordRepository implements SystemOfRecordRepository {

    public SoRSpecification findSoRSpecificationById(final String sorSourceId) {
        return new SoRSpecification() {

            public String getSoR() {
                return "foo";
            }

            public boolean isAllowedValueForProperty(String property, String value) {
                return true;
            }

            public boolean isRequiredProperty(String property) {
                return false;
            }

            public boolean isWithinRequiredSize(String property, Collection collection) {
                return true;
            }

            @Override
            public boolean isWithinRequiredSize(String property, Map map) {
                return true;
            }

            public boolean isInboundInterfaceAllowed(Interfaces interfaces) {
                return true;
            }

            public Map<Interfaces, String> getNotificationSchemesByInterface() {
                return null;
            }

            public boolean isDisallowedProperty(String property) {
                return false;
            }

            @Override
            public String getName() {
                return "Name";
            }

            @Override
            public String getDescription() {
                return "Description";
            }
        };
    }
}
