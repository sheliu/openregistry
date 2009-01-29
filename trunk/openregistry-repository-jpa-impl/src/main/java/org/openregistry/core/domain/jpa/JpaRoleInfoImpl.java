package org.openregistry.core.domain.jpa;

import org.openregistry.core.domain.RoleInfo;
import org.openregistry.core.domain.Department;
import org.openregistry.core.domain.Campus;
import org.openregistry.core.domain.Type;
import org.openregistry.core.domain.internal.Entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.0
 */
@javax.persistence.Entity(name="roleInfo")
@Table(name="prs_roles")
public class JpaRoleInfoImpl extends Entity implements RoleInfo {

    @Id
    private Long id;

    @Column(name="title",nullable = false, length = 100)
    private String title;

    @ManyToOne
    @JoinColumn(name="department_id")
    private JpaDepartmentImpl department;

    @ManyToOne
    @JoinColumn(name="campus_id")
    private JpaCampusImpl campus;

    @ManyToOne(optional = false)
    @JoinColumn(name="affiliation_t")
    private JpaTypeImpl affiliationType;

    @Column(name="code",nullable = false,length = 3)
    private String code;

    @OneToMany(mappedBy = "roleInfo")
    private List<JpaRoleImpl> roles = new ArrayList<JpaRoleImpl>();

    protected Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public Department getDepartment() {
        return this.department;
    }

    public Campus getCampus() {
        return this.campus;
    }

    public Type getAffiliationType() {
        return this.affiliationType;
    }

    public String getCode() {
        return this.code;
    }
}
