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
package org.pac4j.core.context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a mocked web context with settable request method/headers/parameters and session attributes (for tests purpose).
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class MockWebContext implements WebContext {
    
    protected final Map<String, String> parameters = new HashMap<String, String>();
    
    protected final Map<String, String> headers = new HashMap<String, String>();
    
    protected final Map<String, Object> session = new HashMap<String, Object>();
    
    protected String method = "GET";
    
    protected String writtenResponse = null;
    
    protected MockWebContext() {
    }
    
    /**
     * Create a new instance.
     * 
     * @return a new instance
     */
    public static MockWebContext create() {
        return new MockWebContext();
    }
    
    /**
     * Add request parameters for mock purpose.
     * 
     * @param parameters
     * @return this mock web context
     */
    public MockWebContext addRequestParameters(final Map<String, String> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }
    
    /**
     * Add a request parameter for mock purpose.
     * 
     * @param key
     * @param value
     * @return this mock web context
     */
    public MockWebContext addRequestParameter(final String key, final String value) {
        this.parameters.put(key, value);
        return this;
    }
    
    /**
     * Add a request header for mock purpose.
     * 
     * @param key
     * @param value
     * @return this mock web context
     */
    public MockWebContext addRequestHeader(final String key, final String value) {
        this.headers.put(key, value);
        return this;
    }
    
    /**
     * Add a session attribute for mock purpose.
     * 
     * @param name
     * @param value
     * @return this mock web context
     */
    public MockWebContext addSessionAttribute(final String name, final Object value) {
        setSessionAttribute(name, value);
        return this;
    }
    
    /**
     * Set the request method for mock purpose.
     * 
     * @param method
     * @return this mock web context
     */
    public MockWebContext setRequestMethod(final String method) {
        this.method = method;
        return this;
    }
    
    /**
     * Return the written response.
     * 
     * @return the written response
     */
    public String getWrittenResponse() {
        return this.writtenResponse;
    }
    
    public String getRequestParameter(final String name) {
        return this.parameters.get(name);
    }
    
    public String getRequestHeader(final String name) {
        return this.headers.get(name);
    }
    
    public void setSessionAttribute(final String name, final Object value) {
        this.session.put(name, value);
    }
    
    public Object getSessionAttribute(final String name) {
        return this.session.get(name);
    }
    
    public String getRequestMethod() {
        return this.method;
    }
    
    public void invalidateSession() {
        this.session.clear();
    }
    
    public void WriteResponse(final String data) throws IOException {
        if (this.writtenResponse == null) {
            this.writtenResponse = data;
        } else {
            this.writtenResponse += data;
        }
    }
    
    public Map<String, String[]> getRequestParameters() {
        final Map<String, String[]> map = new HashMap<String, String[]>();
        for (final String key : this.parameters.keySet()) {
            final String value = this.parameters.get(key);
            final String[] values = new String[] {
                value
            };
            map.put(key, values);
        }
        return map;
    }
}
