/*
* Copyright (C) 2017 Modern Language Association
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
* except in compliance with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software distributed under
* the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the specific language governing
* permissions and limitations under the License.
*/
package org.mla.cbox.shibboleth.idp.authn.impl;

import javax.annotation.Nonnull;
import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import net.shibboleth.idp.authn.principal.CloneablePrincipal;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import twitter4j.auth.AccessToken;

/** Principal based on ID asserted by Twitter */
public class TwitterIdPrincipal implements CloneablePrincipal {
    /** User ID asserted by Twitter */
    private String userId;
    
    /**
     * Constructor
     */
    public TwitterIdPrincipal() {
        
    }
    
    /**
     * Constructor from Twitter access token
     */
    public TwitterIdPrincipal(AccessToken token) {
        this.userId = Long.toString(token.getUserId());
    }
    
    /**
     * Get the user ID
     * 
     * @return userId, always asserted by Twitter
     */
    public String getUserId() {
        return this.userId;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @NotEmpty public String getName() {
        return this.userId;
    }
    
    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return this.userId.hashCode();
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (other instanceof TwitterIdPrincipal) {
            return this.userId.equals(((TwitterIdPrincipal) other).getUserId());
        }

        return false;
    }
    
    /** Serialize to JSON */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("TwitterIdPrincipal", this.getUserId()).toString();
    }
    
    /** {@inheritDoc} */
    @Override
    public TwitterIdPrincipal clone() throws CloneNotSupportedException {
        TwitterIdPrincipal copy = (TwitterIdPrincipal) super.clone();
        copy.userId = this.userId;
        return copy;
    }
}
