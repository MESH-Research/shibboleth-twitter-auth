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
import javax.annotation.Nullable;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.shibboleth.idp.authn.AbstractValidationAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

/**
 * An action that extracts the Twitter Oauth verifier from the query
 * string then queries Twitter for the access token.
 *
 */
public class ProcessRedirectFromTwitter extends AbstractValidationAction {
    @Nullable private TwitterContext twitterContext;
    
    /** Class logger */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ProcessRedirectFromTwitter.class);

    /** Constructor */
    ProcessRedirectFromTwitter() {
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext) {
        
        if (!super.doPreExecute(profileRequestContext, authenticationContext)) {
            return false;
        }
        
        if (authenticationContext.getAttemptedFlow() == null) {
            log.info("{} No attempted flow within authentication context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return false;
        }
        
        /* Ensure that we have a TwitterContext established during initialization of flow */
        this.twitterContext = authenticationContext.getSubcontext(TwitterContext.class);
        if (this.twitterContext == null) {
            log.info("{} No TwitterContext available within authentication context", getLogPrefix());
            handleError(profileRequestContext, authenticationContext, AuthnEventIds.NO_CREDENTIALS,
                    AuthnEventIds.NO_CREDENTIALS);
            return false;
        } 
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext) {
        
        /* Ensure we were passed the incoming HTTP request */
        final HttpServletRequest servletRequest = getHttpServletRequest();
        if (servletRequest == null) {
            log.debug("{} Profile action does not contain an HttpServletRequest", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return;
        }
        
        /* Obtain the Twitter Oauth verifier from the query string */
        final String verifier = servletRequest.getParameter("oauth_verifier");
        if (verifier == null || verifier.isEmpty()) {
            log.debug("{} No Twitter Oauth verifier in request", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return;
        }
        
        log.debug("{} Twitter returned verifier {}", getLogPrefix(), verifier);
        
        /* Query Twitter for the access token */
        AccessToken accessToken = null;
        try {
            accessToken = this.twitterContext.getTwitter().getOAuthAccessToken(twitterContext.getRequestToken(), verifier);
        } catch (TwitterException e) {
            log.warn("{} exception obtaining access token from Twitter: {}", getLogPrefix(), e.getMessage());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return;
        }
        
        log.info("{} Login by '{}' succeeded", getLogPrefix(), accessToken.getUserId());
        
        /* Attach the access token to the Twitter context */
        this.twitterContext.setAccessToken(accessToken);
        
        /* Complete the authentication flow by building the authentication result */
        buildAuthenticationResult(profileRequestContext, authenticationContext);
        ActionSupport.buildProceedEvent(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull protected Subject populateSubject(@Nonnull final Subject subject) {
        subject.getPrincipals().add(new TwitterIdPrincipal(this.twitterContext.getAccessToken()));
        return subject;
    }
}
