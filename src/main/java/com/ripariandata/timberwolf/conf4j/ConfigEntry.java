/**
 * Copyright 2012 Riparian Data
 * http://www.ripariandata.com
 * contact@ripariandata.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ripariandata.timberwolf.conf4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as being a target for a ConfigFileParser to update with data
 * from a configuration file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ConfigEntry
{
    /** The key name in the configuration file for this entry. */
    String name();

    /**
     * If false, will silently fail to set values on fields that already have
     * non-default values.  The default value for a field is whatever it was set
     * to when the ConfigFileParser was constructed.  If not explicitly initialized,
     * the default values are 0 for numeric types, false for booleans, '\u0000'
     * for chars and null for everything else.
     */
    boolean overwriteNonDefault() default true;
}