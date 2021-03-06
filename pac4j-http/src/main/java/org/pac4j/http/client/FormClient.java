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
package org.pac4j.http.client;

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.ClientException;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.UsernamePasswordAuthenticator;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;
import org.pac4j.http.profile.ProfileCreator;

/**
 * This class is the client to authenticate users through HTTP form.
 * <p />
 * The login url of the form must be defined through the {@link #setLoginUrl(String)} method. For authentication, the user is redirected to
 * this login form. The username and password inputs must be posted on the callback url. Their names can be defined by using the
 * {@link #setUsernameParameter(String)} and {@link #setPasswordParameter(String)} methods.
 * <p />
 * It returns a {@link org.pac4j.http.profile.HttpProfile}.
 * 
 * @see org.pac4j.http.profile.HttpProfile
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class FormClient extends BaseHttpClient {
    
    private String loginUrl;
    
    public final static String DEFAULT_USERNAME_PARAMETER = "username";
    
    private String usernameParameter = DEFAULT_USERNAME_PARAMETER;
    
    public final static String DEFAULT_PASSWORD_PARAMETER = "password";
    
    private String passwordParameter = DEFAULT_PASSWORD_PARAMETER;
    
    public FormClient() {
    }
    
    public FormClient(final String loginUrl, final UsernamePasswordAuthenticator usernamePasswordAuthenticator) {
        setLoginUrl(loginUrl);
        setUsernamePasswordAuthenticator(usernamePasswordAuthenticator);
    }
    
    public FormClient(final String loginUrl, final UsernamePasswordAuthenticator usernamePasswordAuthenticator,
                      final ProfileCreator profileCreator) {
        this(loginUrl, usernamePasswordAuthenticator);
        setProfileCreator(profileCreator);
    }
    
    @Override
    protected BaseClient<UsernamePasswordCredentials, HttpProfile> newClient() {
        final FormClient newClient = new FormClient();
        newClient.setLoginUrl(this.loginUrl);
        newClient.setUsernameParameter(this.usernameParameter);
        newClient.setPasswordParameter(this.passwordParameter);
        return newClient;
    }
    
    @Override
    protected void internalInit() throws ClientException {
        super.internalInit();
        CommonHelper.assertNotBlank("loginUrl", this.loginUrl);
    }
    
    public String getRedirectionUrl(final WebContext context) throws ClientException {
        init();
        return this.loginUrl;
    }
    
    public UsernamePasswordCredentials getCredentials(final WebContext context) throws ClientException {
        init();
        final String username = context.getRequestParameter(this.usernameParameter);
        final String password = context.getRequestParameter(this.passwordParameter);
        if (CommonHelper.isNotBlank(username) && CommonHelper.isNotBlank(password)) {
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password,
                                                                                            getType());
            logger.debug("usernamePasswordCredentials : {}", credentials);
            return credentials;
        }
        final String message = "Username and password cannot be blank";
        logger.error(message);
        throw new CredentialsException(message);
    }
    
    public String getLoginUrl() {
        return this.loginUrl;
    }
    
    public void setLoginUrl(final String loginUrl) {
        this.loginUrl = loginUrl;
    }
    
    public String getUsernameParameter() {
        return this.usernameParameter;
    }
    
    public void setUsernameParameter(final String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }
    
    public String getPasswordParameter() {
        return this.passwordParameter;
    }
    
    public void setPasswordParameter(final String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }
    
    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "failureUrl", getFailureUrl(),
                                     "type", getType(), "loginUrl", this.loginUrl, "usernameParameter",
                                     this.usernameParameter, "passwordParameter", this.passwordParameter,
                                     "usernamePasswordAuthenticator", getUsernamePasswordAuthenticator(),
                                     "profileCreator", getProfileCreator());
    }
}
