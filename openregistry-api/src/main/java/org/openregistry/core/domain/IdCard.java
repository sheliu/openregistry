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

package org.openregistry.core.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Id Card  attached to person
 * Identity card  is generally generated by Openregistry and may not be provided  by any sor
 * down stream card systems would be provided with Api's to modify this
 * @author Muhammad Siddique
 * Date: 4/15/13
 * Time: 4:16 PM
 */
public interface IdCard extends Serializable  {
    /**
     * Actual card number of the id card
     * this number may be set by car number generator  or loaded from legacy system
     * Card Number has to be unique

     */
     String getCardNumber();
     void setCardNumber(String cardNumber);

    /**
     * card security value  in addition to the  card number which is embossed or printed on the card
     * this number may be randomly generated by Or implementation or it can be set by downstream card system
     */
    String getCardSecurityValue();
    void setCardSecurityValue(String cardSecurityValue);

    /**
     *
     * bar code printed on the car for machine readable devices
     * it has to be unique
     * can be generated by OR
     * this number can also be set on this idCard object from legacy system
     */

    String getBarCode();
    void setBarCode(String barCode);


    /**
     * proximity number embeded on the card for proximity sensors
     * set by downstream card printing service
     * @return
     */

    String getProximityNumber();
    void setProximityNumber(String proximityNumber);

    /**
     * The date the IdCard was created.  CANNOT be null.
     * @return a copy of the creation date
     */
    Date getCreationDate();

    /**
     * The date the identifier was created.  CANNOT be null.
     * <p>
     * This method is usable when importing legacy data into OR
     * @return a copy of the creation date
     */
    void setCreationDate(Date creationDate);

    /**
     * the date when this card would be expired
     * this may be null and can be set by downstream system
     *@return expiration date of the card
     */

    Date getExpirationDate();
    void setExpirationDate(Date expirationDate);


    /**
     * the date when any update is performed on this card
     * initially this date would be systdate and then any update on the card (setting bar code, setting expiration date) would change this updatedate
     *
     * @return the date when this was last updated
     */

    Date getUpdateDate();
    /**
     * Defaults to true.
     * there should be only one primary id card number associated with person
     * a person may have many non primary id cards
     *
     * @return whether its the primary version of this identifier, or one that exists for historical reasons.
     */
    boolean isPrimary();
    void setPrimary(boolean value);


}
