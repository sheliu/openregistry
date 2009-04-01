package org.openregistry.core.domain.jpa;

import org.openregistry.core.domain.OrganizationalUnit;
import org.openregistry.core.domain.Type;
import org.openregistry.core.domain.Campus;
import org.openregistry.core.domain.internal.Entity;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;


/**
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 1.0.0
 */
@javax.persistence.Entity(name="organizationalUnit")
@Table(name="drd_organizational_units")
@Audited
public class JpaOrganizationalUnitImpl extends Entity implements OrganizationalUnit {

    @Id
    @Column(name="id")
    @GeneratedValue(generator="drd_organizational_units_seq",strategy = GenerationType.AUTO)
    @SequenceGenerator(name="drd_organizational_units_seq",sequenceName="drd_organizational_units_seq",initialValue=1,allocationSize=50)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="campus_id")
    private JpaCampusImpl campus;

    @ManyToOne(optional = false)
    @JoinColumn(name="organizational_unit_t")    
    private JpaTypeImpl organizationalUnitType;

    @Column(name="code",length = 100, nullable = true)
    private String localCode;

    @Column(name="name", length=100, nullable = false)
    private String name;

    @ManyToOne(optional=true)
    @JoinColumn(name="parent_organizational_unit_id")
    private JpaOrganizationalUnitImpl organizationalUnit;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organizationalUnit")
    private List<JpaRoleInfoImpl> roleInfos = new ArrayList<JpaRoleInfoImpl>();

    public Long getId() {
        return this.id;
    }

    public Type getOrganizationalUnitType() {
        return this.organizationalUnitType;
    }

    public String getLocalCode() {
        return this.localCode;
    }

    public String getName() {
        return this.name;
    }

    public Campus getCampus() {
        return this.campus;
    }

    public OrganizationalUnit getParentOrganizationalUnit() {
        return this.organizationalUnit;
    }
}
