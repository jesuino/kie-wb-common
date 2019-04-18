/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.IsWidget;

import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError.ConfigErrorDisplayer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldPart;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeListener;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.uberfire.ext.layout.editor.api.css.CssValue;

public abstract class FieldRenderer<F extends FieldDefinition, FORM_GROUP extends FormGroup> {

    protected FormRenderingContext renderingContext;
    protected String fieldNS;
    protected F field;
    protected FormFieldImpl formField = null;
    protected List<FieldChangeListener> fieldChangeListeners = new ArrayList<>();
    protected Map<String, IsWidget> partsMapping = new HashMap<>();

    @Inject
    protected ManagedInstance<FORM_GROUP> formGroupsInstance;

    @Inject
    private ConfigErrorDisplayer errorDisplayer;

    public void init(FormRenderingContext renderingContext, F field) {
        this.renderingContext = renderingContext;
        this.field = field;
        this.fieldNS = renderingContext.getNamespace() + FormRenderingContext.NAMESPACE_SEPARATOR + field.getName();
        fieldChangeListeners.clear();
    }

    public IsWidget renderWidget() {
        FieldConfigStatus configStatus = checkFieldConfig();

        if (!configStatus.isWellConfigured()) {
            errorDisplayer.render(configStatus.getConfigErrors());

            return errorDisplayer;
        } else {

            FormGroup formGroup = getFormGroup(renderingContext.getRenderMode());

            formField = new FormFieldImpl(field,
                                          formGroup) {
                @Override
                protected void doSetReadOnly(boolean readOnly) {
                    if (renderingContext.getRenderMode().equals(RenderMode.PRETTY_MODE)) {
                        return;
                    }
                    FieldRenderer.this.setReadOnly(readOnly);
                }

                @Override
                public boolean isRequired() {
                    return field.getRequired();
                }

                @Override
                public boolean isContentValid() {
                    return FieldRenderer.this.isContentValid();
                }

                @Override
                public Collection<FieldChangeListener> getChangeListeners() {
                    return getFieldChangeListeners();
                }
            };

            formField.setReadOnly(renderingContext.getRenderMode().equals(RenderMode.READ_ONLY_MODE));

            registerCustomFieldValidators(formField);
            applyStyleToRendererParts(formGroup);

            return ElementWrapperWidget.getWidget(formGroup.getElement());
        }
    }


    protected abstract FormGroup getFormGroup(RenderMode renderMode);

    protected String generateUniqueId() {
        return Document.get().createUniqueId();
    }

    public FormField getFormField() {
        return formField;
    }

    public F getField() {
        return field;
    }

    protected FieldConfigStatus checkFieldConfig() {
        return new FieldConfigStatus(getConfigErrors());
    }

    protected List<String> getConfigErrors() {
        return null;
    }

    /**
     * Access Parts supported by this renderer
     * 
     * @return
     *  A HashMap with the parts ids and the corresponding widget
     */
    public List<String> getRendererStylableParts() {
        return Collections.emptyList();
    }
    
    private void applyStyleToRendererParts(FormGroup formGroup) {
        List<FieldPart> fieldsParts = renderingContext.getRootForm().getFieldParts(field);
        List<String> allStylableParts = new ArrayList<>();
        allStylableParts.addAll(getRendererStylableParts());
        allStylableParts.addAll(formGroup.getStylableParts());
        // remove leftovers parts from other field Renderers
        fieldsParts.removeIf(p -> !allStylableParts.contains(p.getFieldPartId()));
        // create fields parts that may be missing
        createFieldsParts(allStylableParts);
        // get the parts coming from the form group
        formGroup.getStylableParts().forEach(partId -> {
            formGroup.getStylablePart(partId).ifPresent(w -> partsMapping.put(partId, w));
        });
        fieldsParts.stream()
                   .forEach(part -> {
                       IsWidget partWidget = partsMapping.get(part.getFieldPartId());
                       if (partWidget != null) {
                           part.getProperties().forEach( (k, v)  -> {
                               CssValue value = new CssValue(k, v);
                               partWidget.asWidget().getElement().getStyle().setProperty(value.getPropertyInCamelCase(), v);   
                           });
                       }
                    });
    }
    
    /**
     * Create Field Parts for this field given the parts IDs
     * 
     * @param rendererParts
     */
    private void createFieldsParts(List<String> rendererParts) {
        String fieldId = field.getId();
        for (String partId : rendererParts) {
            Optional<FieldPart> fieldPartOp = renderingContext.getRootForm().getFieldPart(field, partId);
            if (!fieldPartOp.isPresent()) {
                renderingContext.getRootForm().getFieldsParts().add(new FieldPart(fieldId, partId));
            }
        }
    }
    

    
    public abstract String getName();

    public abstract String getSupportedCode();

    protected abstract void setReadOnly(boolean readOnly);

    protected boolean isContentValid() {
        return true;
    }

    protected void registerCustomFieldValidators(FormFieldImpl field) {

    }

    private Collection<FieldChangeListener> getFieldChangeListeners() {
        return fieldChangeListeners;
    }

    protected class FieldConfigStatus {

        protected List<String> configErrors;

        public FieldConfigStatus(List<String> configErrors) {
            this.configErrors = configErrors;
        }

        public List<String> getConfigErrors() {
            return configErrors;
        }

        public boolean isWellConfigured() {
            return configErrors == null || configErrors.isEmpty();
        }
    }

    @PreDestroy
    public void preDestroy() {
        formGroupsInstance.destroyAll();
    }
}
