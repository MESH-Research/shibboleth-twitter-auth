package org.mla.cbox.shibboleth.idp.authn.impl;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.RequestContext;
import net.shibboleth.idp.authn.AbstractAuthenticationAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.profile.context.SpringRequestContext;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

public class InitializeTwitterContext extends AbstractAuthenticationAction {
    /** Twitter integration */
	@Nonnull private TwitterIntegration twitterIntegration;
    
    /** Class logger */
    @Nonnull private final Logger log = LoggerFactory.getLogger(InitializeTwitterContext.class);
    
    /** Constructor **/
    InitializeTwitterContext() {
    }
    
	@Override
	protected void doExecute (
            @Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext) {
        
        /* Create a new TwitterContext */
        final TwitterContext twitterContext = new TwitterContext();
        
        /* Set the Twitter integration details for the context */
        twitterContext.setTwitterIntegration(this.twitterIntegration);
        log.debug("{} Created TwitterContext using TwitterIntegration with consumer key {}", getLogPrefix(), this.twitterIntegration.getOauthConsumerKey());
        
        /* Create a new Twitter class instance and add it to the context */
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(this.twitterIntegration.getOauthConsumerKey(), this.twitterIntegration.getOauthConsumerSecret());
        twitterContext.setTwitter(twitter);
        
        /* Find the Spring context and from it the current flow execution URL */
        SpringRequestContext springRequestContext = (SpringRequestContext) profileRequestContext.getSubcontext(SpringRequestContext.class);
        RequestContext requestContext = springRequestContext.getRequestContext();
        String flowUrl = requestContext.getFlowExecutionUrl();
        
        /* Construct the callback URL using the flow execution URL and the server details */
        ServletExternalContext externalContext = (ServletExternalContext)requestContext.getExternalContext();
    	HttpServletRequest request = (HttpServletRequest) externalContext.getNativeRequest();
    	StringBuilder callbackUrlBuilder = new StringBuilder().append(request.getScheme())
       		 .append("://")
       		 .append(request.getServerName())
       		 .append(flowUrl)
       		 .append("&_eventId=proceed");
        
        String callbackUrl = callbackUrlBuilder.toString();
        
        /* Query Twitter for the request token and include the callback URL */
        try {
        	log.debug("{} Obtaining request token with callback URL {}", getLogPrefix(), callbackUrl);
        	RequestToken requestToken = twitter.getOAuthRequestToken(callbackUrl);
            twitterContext.setRequestToken(requestToken);
            log.debug("{} Obtained request token", getLogPrefix());
        } catch (TwitterException e) {
         	log.error("{} Error obtaining request token from Twitter: {}", getLogPrefix(), e.getErrorMessage());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return;
        }
        
        /* Save the context as a sub context to the authentication context */
        authenticationContext.addSubcontext(twitterContext, true);
        
        return;
	}
    
    /**
     * Get the TwitterIntegration 
     * 
     * @return the Twitter integration details 
     */
    @Nonnull public TwitterIntegration getTwitterIntegration(){
    	return this.twitterIntegration;
    }
    
    /**
     * Set the TwitterIntegration
     * 
     * @param twitterIntegration the Twitter integration details 
     * @return instance of this class
     */
    public InitializeTwitterContext setTwitterIntegration(@Nonnull TwitterIntegration twitterIntegration) {
    	this.twitterIntegration = twitterIntegration;
    	return this;
    }
}