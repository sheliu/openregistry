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

package org.openregistry.core.audit;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;

/**
 * Sets the username from the Spring SecurityContext.
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 1.0.0
 */
public class SpringSecurityListener implements RevisionListener {

    public void newRevision(final Object o) {
        final SpringSecurityRevisionEntity entity = (SpringSecurityRevisionEntity) o;
        final SecurityContext context = SecurityContextHolder.getContext();
        final String comments =  ChangeComments.getComments();

//        entity.setUsername("anonymous");

        if (context != null) {
            if (context.getAuthentication() != null) {
                entity.setUsername(context.getAuthentication().getName());
            } else {
                //for non secure interfaces e.g. batch
                entity.setUsername("anonymous");
            }
        }

        if (StringUtils.isNotBlank(comments)) {
            entity.setComments(comments);
        }
    }
}
