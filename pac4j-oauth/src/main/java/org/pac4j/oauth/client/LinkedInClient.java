/*
  Copyright 2012 - 2013 Jerome Leleu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.oauth.client;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.exception.ClientException;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.linkedin.LinkedInAttributesDefinition;
import org.pac4j.oauth.profile.linkedin.LinkedInProfile;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.oauth.ProxyOAuth10aServiceImpl;

/**
 * This class is the OAuth client to authenticate user in LinkedIn.
 * <p />
 * It returns a {@link org.pac4j.oauth.profile.linkedin.LinkedInProfile}.
 * <p />
 * More information at https://developer.linkedin.com/documents/profile-api
 * 
 * @see org.pac4j.oauth.profile.linkedin.LinkedInProfile
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class LinkedInClient extends BaseOAuth10Client<LinkedInProfile> {
    
    public LinkedInClient() {
    }
    
    public LinkedInClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected LinkedInClient newClient() {
        return new LinkedInClient();
    }
    
    @Override
    protected void internalInit() throws ClientException {
        super.internalInit();
        this.service = new ProxyOAuth10aServiceImpl(new LinkedInApi(),
                                                    new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                    SignatureType.Header, null, null), this.proxyHost,
                                                    this.proxyPort);
    }
    
    @Override
    protected String getProfileUrl() {
        return "http://api.linkedin.com/v1/people/~";
    }
    
    @Override
    protected LinkedInProfile extractUserProfile(final String body) {
        final LinkedInProfile profile = new LinkedInProfile();
        for (final String attribute : OAuthAttributesDefinitions.linkedinDefinition.getAllAttributes()) {
            final String value = StringUtils.substringBetween(body, "<" + attribute + ">", "</" + attribute + ">");
            profile.addAttribute(attribute, value);
            if (LinkedInAttributesDefinition.URL.equals(attribute)) {
                final String id = StringUtils.substringBetween(value, "&amp;key=", "&amp;authToken=");
                profile.setId(id);
            }
        }
        return profile;
    }
}
