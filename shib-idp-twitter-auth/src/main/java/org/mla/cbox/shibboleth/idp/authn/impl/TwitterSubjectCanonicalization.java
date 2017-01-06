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

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.authn.AbstractSubjectCanonicalizationAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.SubjectCanonicalizationException;
import net.shibboleth.idp.authn.context.SubjectCanonicalizationContext;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import com.google.common.base.Predicate;

/**
 * An action that operates on a {@link SubjectCanonicalizationContext} child of the current
 * {@link ProfileRequestContext}, and transforms the input {@link javax.security.auth.Subject}
 * into a principal name by searching for one and only one TwitterIdPrincipal custom principal.
 * 
 */
public class TwitterSubjectCanonicalization extends AbstractSubjectCanonicalizationAction {
    
    /** Supplies logic for pre-execute test */
    @Nonnull private final ActivationCondition embeddedPredicate;
    
    /** The custom Principal to operate on */
    @Nullable private TwitterIdPrincipal twitterIdPrincipal;
    
    /** Constructor */
    public TwitterSubjectCanonicalization() {
        embeddedPredicate = new ActivationCondition();
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext, 
            @Nonnull final SubjectCanonicalizationContext c14nContext) {

        if (embeddedPredicate.apply(profileRequestContext, c14nContext, true)) {
            twitterIdPrincipal = c14nContext.getSubject().getPrincipals(TwitterIdPrincipal.class).iterator().next();
            return super.doPreExecute(profileRequestContext, c14nContext);
        }
        
        return false;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext, 
            @Nonnull final SubjectCanonicalizationContext c14nContext) {
        
        c14nContext.setPrincipalName(twitterIdPrincipal.getName());
    }
     
    /** A predicate that determines if this action can run or not */
    public static class ActivationCondition implements Predicate<ProfileRequestContext> {

        /** {@inheritDoc} */
        @Override
        public boolean apply(@Nullable final ProfileRequestContext input) {
            
            if (input != null) {
                final SubjectCanonicalizationContext c14nContext =
                        input.getSubcontext(SubjectCanonicalizationContext.class, false);
                if (c14nContext != null) {
                    return apply(input, c14nContext, false);
                }
            }
            
            return false;
        }

        /**
         * Helper method that runs either as part of the Predicate or directly from
         * the TwitterSubjectCanonicalization#doPreExecute(ProfileRequestContext, SubjectCanonicalizationContext)
         * method above.
         * 
         * @param profileRequestContext the current profile request context
         * @param c14nContext   the current c14n context
         * @param duringAction  true iff the method is run from the action above
         * @return true iff the action can operate successfully on the candidate contexts
         */
        public boolean apply(@Nonnull final ProfileRequestContext profileRequestContext,
                @Nonnull final SubjectCanonicalizationContext c14nContext, final boolean duringAction) {

            final Set<TwitterIdPrincipal> twitterIdPrincipals;
            if (c14nContext.getSubject() != null) {
                twitterIdPrincipals = c14nContext.getSubject().getPrincipals(TwitterIdPrincipal.class);
            } else {
                twitterIdPrincipals = null;
            }
            
            if (duringAction) {
                if (twitterIdPrincipals == null || twitterIdPrincipals.isEmpty()) {
                    c14nContext.setException(
                            new SubjectCanonicalizationException("No TwitterIdPrincipals were found"));
                    ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.INVALID_SUBJECT);
                    return false;
                } else if (twitterIdPrincipals.size() > 1) {
                    c14nContext.setException(
                            new SubjectCanonicalizationException("Multiple TwitterIdPrincipals were found"));
                    ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.INVALID_SUBJECT);
                    return false;
                }
                
                return true;
            } else {
                return twitterIdPrincipals != null && twitterIdPrincipals.size() == 1;
            }
        }
    }
}
