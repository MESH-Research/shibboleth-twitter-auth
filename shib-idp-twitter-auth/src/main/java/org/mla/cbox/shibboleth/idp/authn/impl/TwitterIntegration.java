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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Twitter OAuth web application integration
 */
public class TwitterIntegration {
    /** Twitter OAuth Consumer Key */
    @Nonnull private String oauthConsumerKey;
    
    /** Twitter OAuth Consumer Secret */
    @Nonnull private String oauthConsumerSecret;
    
    /** Class logger */
    @Nonnull private final Logger log = LoggerFactory.getLogger(TwitterIntegration.class);
    
    /** Log prefix */
    @Nonnull private final String logPrefix = getClass().getSimpleName() + ":";
    
    /** Constructor */
    public TwitterIntegration() {
        
    }
    
    public String getOauthConsumerKey() {
        return this.oauthConsumerKey;
    }
    
    public String getOauthConsumerSecret() {
        return this.oauthConsumerSecret;
    }
    
    public TwitterIntegration setOauthConsumerKey(String key) {
        this.oauthConsumerKey = key;
        return this;
    }
    
    public TwitterIntegration setOauthConsumerSecret(String secret) {
        this.oauthConsumerSecret = secret;
        return this;
    }
}
