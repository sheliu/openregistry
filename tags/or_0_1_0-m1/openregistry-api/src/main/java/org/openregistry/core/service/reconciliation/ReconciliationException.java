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
package org.openregistry.core.service.reconciliation;

import java.util.List;

/**
 * @version $Revision$ $Date$
 * @since 0.1
 */
public class ReconciliationException extends Exception implements ReconciliationResult {

    private final ReconciliationResult reconciliationResult;

    public ReconciliationException(final ReconciliationResult reconciliationResult) {
        this.reconciliationResult = reconciliationResult;
    }

    public ReconciliationType getReconciliationType() {
        return this.reconciliationResult.getReconciliationType();
    }

    public List<PersonMatch> getMatches() {
        return this.reconciliationResult.getMatches();
    }

    public boolean noPeopleFound() {
        return this.reconciliationResult.noPeopleFound();
    }

    public boolean personAlreadyExists() {
        return this.reconciliationResult.personAlreadyExists();
    }

    public boolean multiplePeopleFound() {
        return this.reconciliationResult.multiplePeopleFound();
    }
}
