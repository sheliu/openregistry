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

package org.openregistry.core.domain.jpa;

import org.openregistry.core.domain.*;
import org.openregistry.core.domain.internal.Entity;
import org.hibernate.envers.Audited;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Date;

/**
 * Unique Constraints assumes each calculated person can have only one entry for each identifier type
 * For example, a person can't have multiple SSN or NetIDs
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 1.0.0
 */
@javax.persistence.Entity(name="identifier")

@Table(name="prc_identifiers", uniqueConstraints= @UniqueConstraint(columnNames={"identifier_t", "identifier"}))
@Audited
public class JpaIdentifierImpl extends Entity implements Identifier {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "prc_identifiers_seq")
    @SequenceGenerator(name="prc_identifiers_seq",sequenceName="prc_identifiers_seq",initialValue=1,allocationSize=50)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="person_id")
    private JpaPersonImpl person;

    @ManyToOne(optional=false)
    @JoinColumn(name="identifier_t")
    private JpaIdentifierTypeImpl type;

    @Column(name="identifier", length=100, nullable=false)
    private String value;

    @Column(name="is_primary", nullable=false)
    private boolean primary = true;

    @Column(name="is_deleted", nullable=false)
    private boolean deleted = false;

    @Column(name="creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @Column(name="deleted_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;

    public JpaIdentifierImpl() {
        // nothing to do
    }

    public JpaIdentifierImpl(final JpaPersonImpl person) {
        this.person = person;
    }

    public JpaIdentifierImpl(final JpaPersonImpl person, final JpaIdentifierTypeImpl jpaIdentifierType, final String value) {
        this(person);
        this.type = jpaIdentifierType;
        this.value = value;

        this.type.getIdentifiers().add(this);
    }

    protected Long getId() {
        return this.id;
    }

    public IdentifierType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isPrimary() {
    	return this.primary;
    }

    public boolean isDeleted() {
    	return this.deleted;
    }

    @Override
    public Date getCreationDate() {
        return this.creationDate;
    }

    @Override
    public Date getDeletedDate() {
        return this.deletedDate;
    }

    public void setType(final IdentifierType type) {
        Assert.isInstanceOf(JpaIdentifierTypeImpl.class, type, "Requires type JpaIdentifierTypeImpl.");
        this.type = (JpaIdentifierTypeImpl) type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPrimary(boolean value) {
    	this.primary = value;
    }

    public void setDeleted(boolean value) {
        if (value == this.deleted) {
            return;
        }
        
    	this.deleted = value;

        if (value) {
            this.deletedDate = new Date();
        } else {
            this.deletedDate = null;
        }
    }

    @Override
    public String toString() {
        return this.value;
    }
}
