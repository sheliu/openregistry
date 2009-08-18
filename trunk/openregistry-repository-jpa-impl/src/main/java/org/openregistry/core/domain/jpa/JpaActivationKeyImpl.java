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
package org.openregistry.core.domain.jpa;

import org.hibernate.envers.Audited;
import org.openregistry.core.domain.ActivationKey;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Date;
import java.util.Calendar;
import java.security.SecureRandom;

/**
 * Immutable implementation of the {@link org.openregistry.core.domain.ActivationKey} interface.
 * <p>
 * Note: its not TRULY immutable because of the JPA restrictions, but it exposes no setters to change values.
 *
 * @version $Revision$ $Date$
 * @since 1.0.0
 */
@Embeddable
public class JpaActivationKeyImpl implements ActivationKey {

    /** The array of printable characters to be used in our random string. */
    private static final char[] PRINTABLE_CHARACTERS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ2345679".toCharArray();

    private static final SecureRandom secureRandom = new SecureRandom();

    private static final int ID_LENGTH = 8;

    @Column(name = "activation_key")
    private String value;

    @Column(name = "act_key_end_date")
    @Temporal(TemporalType.DATE)
    private Date end;

    @Column(name = "act_key_start_date")
    @Temporal(TemporalType.DATE)
    private Date start;

    public JpaActivationKeyImpl() {
        final Calendar calendar = Calendar.getInstance();
        final Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        final Date endDate = calendar.getTime();
        this.start = startDate;
        this.end = endDate;
        this.value = constructNewValue();
    }

    public void setActivationKeyValues(final Date start, final Date end){
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        this.value = constructNewValue();
    }

    private String constructNewValue() {
        final byte[] random = new byte[ID_LENGTH];
        secureRandom.nextBytes(random);
        return convertBytesToString(random);
    }

    public void setActivationKeyValues(final Date end){
        setActivationKeyValues(new Date(), end);
    }

    private String convertBytesToString(final byte[] random) {
        final char[] output = new char[random.length];
        for (int i = 0; i < random.length; i++) {
            final int index = Math.abs(random[i] % PRINTABLE_CHARACTERS.length);
            output[i] = PRINTABLE_CHARACTERS[index];
        }

        return new String(output);
    }

    public String getValue() {
        return this.value;
    }

    public boolean isNotYetValid() {
       return (this.start == null || this.start.compareTo(new Date()) > 0);
    }

    public boolean isExpired() {
        return (this.end == null || this.end.compareTo(new Date()) < 0);
    }

    public boolean isValid() {
        return !isNotYetValid() && !isExpired();
    }

    public Date getStart() {
        return new Date(this.start.getTime());
    }

    public Date getEnd() {
        return new Date(this.end.getTime());
    }

    public void removeKeyValues(){
        this.value = null;
        this.start = null;
        this.end = null;
    }

    public int compareTo(final ActivationKey o) {
        Assert.notNull(o);

        return this.value.compareTo(o.getValue());
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaActivationKeyImpl)) return false;

        JpaActivationKeyImpl that = (JpaActivationKeyImpl) o;

        if (end != null ? !end.equals(that.end) : that.end != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;

        return true;
    }

    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result;
        return result;
    }
}