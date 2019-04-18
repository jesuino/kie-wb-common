/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.model;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * 
 */
@Portable
public class FieldPart {
    
    private String fieldId;
    private String fieldPartId;
    
    private Map<String, String> properties = new HashMap<>();

    public FieldPart(@MapsTo("fieldId") String fieldId, @MapsTo("fieldPartId") String fieldPartId) {
        super();
        this.fieldId = fieldId;
        this.fieldPartId = fieldPartId;
        properties = new HashMap<>();
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldPartId() {
        return fieldPartId;
    }

    public void setFieldPartId(String fieldPartId) {
        this.fieldPartId = fieldPartId;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldId == null) ? 0 : fieldId.hashCode());
        result = prime * result + ((fieldPartId == null) ? 0 : fieldPartId.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FieldPart other = (FieldPart) obj;
        if (fieldId == null) {
            if (other.fieldId != null) {
                return false;
            }
        } else if (!fieldId.equals(other.fieldId)) {
            return false;
        } if (fieldPartId == null) {
            if (other.fieldPartId != null) {
                return false;
            }
        } else if (!fieldPartId.equals(other.fieldPartId)) {
            return false;
        } if (properties == null) {
            if (other.properties != null) {
                return false;
            }
        } else if (!properties.equals(other.properties)) {
            return false;
        }
        return true;
    }
    
    

}
