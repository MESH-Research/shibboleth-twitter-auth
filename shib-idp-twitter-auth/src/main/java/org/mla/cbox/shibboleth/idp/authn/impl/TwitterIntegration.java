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