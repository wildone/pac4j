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

import java.util.concurrent.TimeUnit;

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.ClientException;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.OAuthProfile;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a base implementation for an OAuth protocol client based on the Scribe library. It should work for all OAuth clients. In
 * subclasses, some methods are to be implemented / customized for specific needs depending on the client.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class BaseOAuthClient<U extends OAuthProfile> extends BaseClient<OAuthCredentials, U> {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuthClient.class);
    
    protected OAuthService service;
    
    protected String key;
    
    protected String secret;
    
    // 0,5 second
    protected int connectTimeout = 500;
    
    // 3 seconds
    protected int readTimeout = 3000;
    
    protected String proxyHost = null;
    
    protected int proxyPort = 8080;
    
    @Override
    protected void internalInit() throws ClientException {
        CommonHelper.assertNotBlank("key", this.key);
        CommonHelper.assertNotBlank("secret", this.secret);
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
    }
    
    @Override
    public BaseOAuthClient<U> clone() {
        final BaseOAuthClient<U> newClient = (BaseOAuthClient<U>) super.clone();
        newClient.setKey(this.key);
        newClient.setSecret(this.secret);
        newClient.setConnectTimeout(this.connectTimeout);
        newClient.setReadTimeout(this.readTimeout);
        newClient.setProxyHost(this.proxyHost);
        newClient.setProxyPort(this.proxyPort);
        return newClient;
    }
    
    /**
     * Get the redirection url.
     * 
     * @param context
     * @return the redirection url
     * @throws ClientException
     */
    public String getRedirectionUrl(final WebContext context) throws ClientException {
        init();
        try {
            return retrieveRedirectionUrl(context);
        } catch (final OAuthException e) {
            throw new ClientException(e);
        }
    }
    
    /**
     * Retrieve the redirection url.
     * 
     * @param context
     * @return the redirection url
     */
    protected abstract String retrieveRedirectionUrl(final WebContext context);
    
    /**
     * Get the credentials from the web context.
     * 
     * @param context
     * @return the credentials
     * @throws ClientException
     */
    public OAuthCredentials getCredentials(final WebContext context) throws ClientException {
        init();
        try {
            return retrieveCredentials(context);
        } catch (final OAuthException e) {
            throw new ClientException(e);
        }
    }
    
    /**
     * Retrieve the credentials from the web context.
     * 
     * @param context
     * @return the credentials
     * @throws ClientException
     */
    protected OAuthCredentials retrieveCredentials(final WebContext context) throws ClientException {
        boolean errorFound = false;
        final OAuthCredentialsException oauthCredentialsException = new OAuthCredentialsException(
                                                                                                  "Failed to retrieve OAuth credentials, error parameters found");
        String errorMessage = "";
        for (final String key : OAuthCredentialsException.ERROR_NAMES) {
            final String value = context.getRequestParameter(key);
            if (value != null) {
                errorFound = true;
                errorMessage += key + " : '" + value + "'; ";
                oauthCredentialsException.setErrorMessage(key, value);
            }
        }
        if (errorFound) {
            logger.error(errorMessage);
            throw oauthCredentialsException;
        } else {
            return getOAuthCredentials(context);
        }
    }
    
    /**
     * Get the OAuth credentials from the web context.
     * 
     * @param context
     * @return the OAuth credentials
     * @throws OAuthCredentialsException
     */
    protected abstract OAuthCredentials getOAuthCredentials(final WebContext context) throws OAuthCredentialsException;
    
    /**
     * Get the user profile from the credentials.
     * 
     * @param credentials
     * @return the user profile
     * @throws ClientException
     */
    public U getUserProfile(final OAuthCredentials credentials) throws ClientException {
        init();
        try {
            final Token token = getAccessToken(credentials);
            return retrieveUserProfileFromToken(token);
        } catch (final OAuthException e) {
            throw new ClientException(e);
        }
    }
    
    /**
     * Get the user profile from the access token.
     * 
     * @param accessToken
     * @return the user profile
     * @throws ClientException
     */
    public U getUserProfile(final String accessToken) throws ClientException {
        init();
        try {
            final Token token = new Token(accessToken, "");
            return retrieveUserProfileFromToken(token);
        } catch (final OAuthException e) {
            throw new ClientException(e);
        }
    }
    
    /**
     * Get the access token from OAuth credentials.
     * 
     * @param credentials
     * @return the access token
     * @throws OAuthCredentialsException
     */
    protected abstract Token getAccessToken(OAuthCredentials credentials) throws OAuthCredentialsException;
    
    /**
     * Retrieve the user profile from the access token.
     * 
     * @param accessToken
     * @return the user profile
     * @throws ClientException
     */
    protected U retrieveUserProfileFromToken(final Token accessToken) throws ClientException {
        final String body = sendRequestForData(accessToken, getProfileUrl());
        if (body == null) {
            throw new HttpCommunicationException("Not data found for accessToken : " + accessToken);
        }
        final U profile = extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        return profile;
    }
    
    /**
     * Retrieve the url of the profile of the authenticated user for the provider.
     * 
     * @return the url of the user profile given by the provider
     */
    protected abstract String getProfileUrl();
    
    /**
     * Make a request to get the data of the authenticated user for the provider.
     * 
     * @param accessToken
     * @param dataUrl
     * @return the user data response
     * @throws HttpCommunicationException
     */
    protected String sendRequestForData(final Token accessToken, final String dataUrl)
        throws HttpCommunicationException {
        logger.debug("accessToken : {} / dataUrl : {}", accessToken, dataUrl);
        final long t0 = System.currentTimeMillis();
        final ProxyOAuthRequest request = new ProxyOAuthRequest(Verb.GET, dataUrl, this.proxyHost, this.proxyPort);
        if (this.connectTimeout != 0) {
            request.setConnectTimeout(this.connectTimeout, TimeUnit.MILLISECONDS);
        }
        if (this.readTimeout != 0) {
            request.setReadTimeout(this.readTimeout, TimeUnit.MILLISECONDS);
        }
        this.service.signRequest(accessToken, request);
        // for Google
        if (this instanceof GoogleClient) {
            request.addHeader("GData-Version", "3.0");
        } else if (this instanceof WordPressClient) {
            request.addHeader("Authorization", "Bearer " + accessToken.getToken());
        }
        final Response response = request.send();
        final int code = response.getCode();
        final String body = response.getBody();
        final long t1 = System.currentTimeMillis();
        logger.debug("Request took : " + (t1 - t0) + " ms for : " + dataUrl);
        logger.debug("response code : {} / response body : {}", code, body);
        if (code != 200) {
            logger.error("Failed to get user data, code : " + code + " / body : " + body);
            throw new HttpCommunicationException(code, body);
        }
        return body;
    }
    
    /**
     * Extract the user profile from the response (JSON, XML...) of the profile url.
     * 
     * @param body
     * @return the user profile object
     */
    protected abstract U extractUserProfile(String body);
    
    /**
     * Add the access token to the profile (as an attribute).
     * 
     * @param profile
     * @param accessToken
     */
    protected void addAccessTokenToProfile(final U profile, final Token accessToken) {
        if (profile != null) {
            final String token = accessToken.getToken();
            logger.debug("add access_token : {} to profile", token);
            profile.setAccessToken(token);
        }
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public void setSecret(final String secret) {
        this.secret = secret;
    }
    
    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getSecret() {
        return this.secret;
    }
    
    public int getConnectTimeout() {
        return this.connectTimeout;
    }
    
    public int getReadTimeout() {
        return this.readTimeout;
    }
    
    public String getProxyHost() {
        return this.proxyHost;
    }
    
    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }
    
    public int getProxyPort() {
        return this.proxyPort;
    }
    
    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }
}
