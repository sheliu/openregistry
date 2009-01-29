package org.openregistry.core.domain.jpa;

import org.openregistry.core.domain.Region;
import org.openregistry.core.domain.Country;
import org.openregistry.core.domain.internal.Entity;

import javax.persistence.*;
import java.util.List;

/**
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 1.0.0
 *
 */
@javax.persistence.Entity(name="region")
@Table(name="ctd_regions")
public class JpaRegionImpl extends Entity implements Region {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ctd_reqion_seq")
    private Long id;

    @Column(name="name",nullable = false,length=100)
    private String name;

    @Column(name="code",nullable=false,length=3)
    private String code;

    @ManyToOne
    @JoinColumn(name="country_id", nullable=false)
    private JpaCountryImpl country;

    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
    private List<JpaAddressImpl> addresses;

    protected Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    public Country getCountry() {
        return this.country;
    }
}
