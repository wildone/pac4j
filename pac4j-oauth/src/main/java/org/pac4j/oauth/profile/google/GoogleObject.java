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
package org.pac4j.oauth.profile.google;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a common Google object (value + type).
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class GoogleObject extends JsonObject {
    
    private static final long serialVersionUID = -7068363744738485038L;
    
    private String value;
    
    private String type;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.value = (String) JsonHelper.convert(Converters.stringConverter, json, "value");
        this.type = (String) JsonHelper.convert(Converters.stringConverter, json, "type");
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String getType() {
        return this.type;
    }
}
