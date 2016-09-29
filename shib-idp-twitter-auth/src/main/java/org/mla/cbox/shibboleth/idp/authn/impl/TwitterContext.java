package org.mla.cbox.shibboleth.idp.authn.impl;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.opensaml.messaging.context.BaseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Context, usually attached to {@link AuthenticationContext}, that carries a Twitter access token
 */
public class TwitterContext extends BaseContext {
    /** Twitter integration details */
	@Nullable private TwitterIntegration twitterIntegration = null;
    
	/** Twitter factory instance */
	@Nullable private Twitter twitter = null;
    
	/** Twitter access token */
	@Nullable private AccessToken accessToken = null;
    
	/** Twitter request token */
	@Nullable private RequestToken requestToken = null;
    
	/** Class logger */
    @Nonnull private final Logger log = LoggerFactory.getLogger(TwitterContext.class);
    
    /** Log prefix */
    @Nonnull private final String logPrefix = getClass().getSimpleName() + ":";
    
	/** Constructor */
    public TwitterContext() {
    }
    
    /**
     * Get the access token
     * 
     * @return the Twitter access token
     */
    @Nullable public AccessToken getAccessToken() {
    	return this.accessToken;
    }
    
    /**
     * Get the request token
     * 
     * @return the Twitter request token
     */
    @Nullable public RequestToken getRequestToken() {
    	return this.requestToken;
    }
    
    /**
     * Get the Twitter class instance
     * 
     * @return the Twitter class instance
     */
    @Nullable public Twitter getTwitter() {
    	return this.twitter;
    }
    
    /**
     * Get the Twitter integration
     * 
     * @return the Twitter integration object
     */
    @Nullable public TwitterIntegration getTwitterIntegration() {
    	return this.twitterIntegration;
    }
	
     
     /**
      * Compute the Twitter OAuth authentication URL
      * 
      * @return the URL
      */
     public String twitterLoginUrl() {
         return this.requestToken.getAuthenticationURL();
     }
     
    /** Set the access token.
     * 
     * @param token the Twitter access token
     * 
     * @return this context
     */
     public TwitterContext setAccessToken(AccessToken token) {
    	 this.accessToken = token;
    	 return this;
     }
     
    /** Set the request token.
     * 
     * @param token the Twitter request token
     * 
     * @return this context
     */
     public TwitterContext setRequestToken(RequestToken token) {
    	 this.requestToken = token;
    	 return this;
     }
     
     
    /**
     * Set the Twitter class instance.
     * 
     *  @param twitter the Twitter class instance
     *  
     *  @return this context
     */
     public TwitterContext setTwitter(@Nullable final Twitter twitter) {
    	 this.twitter = twitter;
         return this;
     }
     
    /**
     * Set the Twitter integration.
     * 
     * @param twitterIntegration the Twitter integration
     * 
     * @return this context
     */
    public TwitterContext setTwitterIntegration(@Nullable final TwitterIntegration twitterIntegration) {
    	this.twitterIntegration = twitterIntegration;
        return this;
    }
}