/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal;

import org.gradle.internal.service.scopes.Scope;
import org.gradle.internal.service.scopes.ServiceScope;
import org.gradle.util.GradleVersion;

/**
 * Locates documentation for various features.
 */
@ServiceScope(Scope.Global.class)
public class DocumentationRegistry {
    private final static String BASE_URL = "https://docs.gradle.org/" + GradleVersion.current();

    public DocumentationRegistry() {
    }

    /**
     * Returns the location of the documentation for the given feature, referenced by id. The location may be local or remote.
     */
    public String getDocumentationFor(String id) {
        return String.format("%s/userguide/%s.html", BASE_URL, id);
    }

    public String getDocumentationFor(String id, String section) {
        return getDocumentationFor(id) + "#" + section;
    }

    public String getDslRefForProperty(Class<?> clazz, String property) {
        String className = clazz.getName();
        return String.format("%s/dsl/%s.html#%s:%s", BASE_URL, className, className, property);
    }

    public String getSampleIndex() {
        return BASE_URL + "/samples";
    }

    public String getSampleFor(String id) {
        return String.format("%s/samples/sample_%s.html", BASE_URL, id);
    }
}
