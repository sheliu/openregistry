/**
 * Copyright (C) 2009 Jasig, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openregistry.core.repository.jpa;

import java.util.List;
import java.util.Date;

import org.openregistry.core.repository.PersonRepository;
import org.openregistry.core.repository.RepositoryAccessException;
import org.openregistry.core.domain.*;
import org.openregistry.core.domain.sor.SorPerson;
import org.openregistry.core.domain.sor.SorRole;
import org.openregistry.core.domain.jpa.JpaPersonImpl;
import org.openregistry.core.domain.jpa.sor.JpaSorPersonImpl;
import org.openregistry.core.domain.jpa.sor.JpaSorRoleImpl;
import org.openregistry.core.service.SearchCriteria;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Person repository implementation built on top of JPA.
 *
 * @author Dmitriy Kopylenko
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 1.0.0
 */
@Repository (value = "personRepository")
public class JpaPersonRepository implements PersonRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Person findByInternalId(final Long id) throws RepositoryAccessException {
        return this.entityManager.find(JpaPersonImpl.class, id);
    }
    
    public SorPerson findSorByInternalId(final Long id) throws RepositoryAccessException {
        return this.entityManager.find(JpaSorPersonImpl.class, id);
    }

    public Person findByIdentifier(final String identifierType, final String identifierValue) throws RepositoryAccessException {
        return (Person) this.entityManager.createQuery("Select p from person p join p.identifiers i join i.type t where t.name = :name and i.value = :value").setParameter("name", identifierType).setParameter("value", identifierValue).getSingleResult();
    }

    public SorPerson findByPersonIdAndSorIdentifier(final Long personId, final String sorSourceIdentifier) {
        return (SorPerson) this.entityManager.createQuery("Select s from sorPerson s where s.sourceSorIdentifier = :sorSourceIdentifier and s.personId = :personId").setParameter("sorSourceIdentifier", sorSourceIdentifier).setParameter("personId", personId).getSingleResult();
    }

    public SorRole findSorRoleByInternalId(final Long id) throws RepositoryAccessException {
        return this.entityManager.find(JpaSorRoleImpl.class, id);
    }

    public List<Person> searchByCriteria(final SearchCriteria searchCriteria) throws RepositoryAccessException {
        final String givenName = searchCriteria.getGivenName();
        final String familyName = searchCriteria.getFamilyName();
        final Date birthDate = searchCriteria.getDateOfBirth();

        if (birthDate == null) {
            return this.entityManager.createQuery("select p from person p join p.names n where n.given like :given and n.family  like :family").setParameter("given", "%" + givenName + "%").setParameter("family", "%" + familyName + "%").getResultList();
        } else {
            return this.entityManager.createQuery("select p from person p join p.names n where n.given like :given and n.family  like :family and p.dateOfBirth = :dateOfBirth").setParameter("given", "%" + givenName + "%").setParameter("family", "%" + familyName + "%").setParameter("dateOfBirth", birthDate).getResultList();
        }
    }

    public List<Person> findByFamilyName(final String family) throws RepositoryAccessException {
    	return this.entityManager.createQuery("SELECT p FROM person p JOIN p.names n WHERE n.family = :name")
    	.setParameter("name", family).getResultList();
    }

    public Person savePerson(final Person person) throws RepositoryAccessException {
        return this.entityManager.merge(person);
    }

    public SorPerson saveSorPerson(final SorPerson person) throws RepositoryAccessException {
        return this.entityManager.merge(person);
    }

    public void addPerson(Person person) throws RepositoryAccessException {
        this.entityManager.persist(person);
    }

    public void deleteSorRole(final SorPerson person, final SorRole role) {
        this.entityManager.merge(person);
    }

    public void updateRole(final Person person, final Role role) {
        this.entityManager.merge(person);
    }

    public SorRole saveSorRole(final SorRole role) throws RepositoryAccessException {
        return this.entityManager.merge(role);
    }

    public SorPerson findSorPersonByPersonIdAndSorRoleId(final Long personId, final Long roleId) {
        return (SorPerson) this.entityManager.createQuery("select s from sorPerson s join s.roles r where r.roleId = :roleId and s.personId = :personId").setParameter("roleId", roleId).setParameter("personId", personId).getSingleResult();
    }

    public SorPerson findBySorIdentifierAndSource(final String sorSourceIdentifier, final String sorId) {
        return (SorPerson) this.entityManager.createQuery("select s from sorPerson s where s.sourceSorIdentifier = :sorSourceIdentifier and s.sorId = :sorId").setParameter("sorSourceIdentifier", sorSourceIdentifier).setParameter("sorId", sorId).getSingleResult();
    }

    public void deleteSorPerson(final SorPerson sorPerson) {
        SorPerson sorPersonToDelete = this.entityManager.getReference(sorPerson.getClass(), sorPerson.getId());
        this.entityManager.remove(sorPersonToDelete);
    }

    /**
     * Returns the SoR records for a particular person.
     *
     * @param person the person
     * @return a list of sorPerson records.
     */
    public List<SorPerson> getSoRRecordsForPerson(final Person person){
        return (List<SorPerson>) this.entityManager.createQuery("select s from sorPerson s where s.personId = :personId").setParameter("personId", person.getId()).getResultList();
    }

    public Number getCountOfSoRRecordsForPerson(final Person person) {
        return (Number) this.entityManager.createQuery("select count(s) from sorPerson s where s.personId = :personId").setParameter("personId", person.getId()).getSingleResult();
    }

    public void deletePerson(final Person person) {
        this.entityManager.createQuery("Delete from person p where p.id = :personId").setParameter("personId", person.getId()).executeUpdate();
    }
}
