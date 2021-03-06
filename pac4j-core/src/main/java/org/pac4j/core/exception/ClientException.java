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
package org.pac4j.core.exception;

/**
 * This class represents the root exception for the library.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class ClientException extends Exception {
    
    private static final long serialVersionUID = -7849182901733016593L;
    
    public ClientException(final String message) {
        super(message);
    }
    
    public ClientException(final Throwable t) {
        super(t);
    }
    
    public ClientException(final String message, final Throwable t) {
        super(message, t);
    }
}
