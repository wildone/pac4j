/*
  Copyright 2012 -2013 Jerome Leleu

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
package org.pac4j.core.util;

import org.pac4j.core.exception.ClientException;

/**
 * This class is an object that can be (re-)initialized through the {@link #init()} and the {@link #reinit()} methods, the
 * {@link #internalInit()} must be implemented in sub-classes.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class InitializableObject {
    
    private boolean initialized = false;
    
    /**
     * Initialize the object.
     * 
     * @throws ClientException
     */
    public void init() throws ClientException {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    internalInit();
                    this.initialized = true;
                }
            }
        }
    }
    
    /**
     * Force (again) the initialization of the object.
     * 
     * @throws ClientException
     */
    public synchronized void reinit() throws ClientException {
        internalInit();
        this.initialized = true;
    }
    
    /**
     * Internal initialization of the object.
     * 
     * @throws ClientException
     */
    protected abstract void internalInit() throws ClientException;
}
