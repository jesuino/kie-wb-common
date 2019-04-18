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

package org.kie.workbench.common.forms.editor.client.editor;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorFieldSelectedEvent;
import org.kie.workbench.common.forms.model.FieldPart;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;

@ApplicationScoped
public class FormFieldPropertiesEditorPresenter {
    
    View view;

    private List<FieldPart> fieldParts;
    
    LayoutEditorCssHelper cssHelper;

    private FieldPart selectedPart;
    

    public interface View extends UberView<FormFieldPropertiesEditorPresenter> {
        
        void setFieldParts(List<String> items);
        
        void showPropertiesEditor(List<PropertyEditorCategory> properties);

        void noPartsToEdit();
    }
    
    public FormFieldPropertiesEditorPresenter() {
    }

    @Inject
    public FormFieldPropertiesEditorPresenter(View view, LayoutEditorCssHelper helper) {
        this.view = view;
        this.cssHelper = helper;
        view.init(this);
    }

    public void onPartSelected(String partId) {
        fieldParts.stream().filter(p -> p.getFieldPartId().equals(partId)).findFirst().ifPresent(part -> {
            selectedPart = part;
            List<PropertyEditorCategory> allCategories = cssHelper.allCategories(part.getProperties());
            view.showPropertiesEditor(allCategories);
        });
    }
    
    public void onEditorElementSelect(@Observes FormEditorFieldSelectedEvent event) {
        this.fieldParts = event.getFieldParts();
        if (fieldParts.isEmpty()) {
            view.noPartsToEdit();
        } else {
            List<String> items = fieldParts.stream().map(FieldPart::getFieldPartId).collect(Collectors.toList());
            view.setFieldParts(items);
            onPartSelected(fieldParts.get(0).getFieldPartId());
        }
    }
    
    public View getView() {
        return view;
    }
    
    public void onPropertyChanged(String property, String value) {
        selectedPart.getProperties().put(property, value);
    }

}