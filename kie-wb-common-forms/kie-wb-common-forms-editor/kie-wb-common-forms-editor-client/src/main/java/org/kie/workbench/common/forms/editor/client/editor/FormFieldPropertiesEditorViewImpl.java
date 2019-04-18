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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;

import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;



@Templated
@Dependent
public class FormFieldPropertiesEditorViewImpl implements FormFieldPropertiesEditorPresenter.View {
    
    @Inject
    @DataField
    HTMLDivElement parentDiv;
    
    @Inject
    @DataField
    HTMLDivElement contentDiv;
    
    @Inject
    @DataField
    @Named("span")
    HTMLElement panelTitle;
    
    @Inject
    @DataField
    HTMLDivElement propertiesDiv;
    
    @Inject
    @DataField
    HTMLOptionElement partOption;
    
    @Inject
    @DataField
    HTMLSelectElement partsSelect;

    private PropertyEditorWidget propertyEditorWidget;
    
    @Inject
    private Elemental2DomUtil util;
    
    private String PROPERTY_EDITOR_ID = Document.get().createUniqueId();

    private FormFieldPropertiesEditorPresenter presenter;
    
    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(parentDiv);
    }

    @Override
    public void init(FormFieldPropertiesEditorPresenter presenter) {
        this.presenter = presenter;
        propertyEditorWidget = new PropertyEditorWidget();
        util.appendWidgetToElement(propertiesDiv, propertyEditorWidget);
        partsSelect.onchange = e -> {
            presenter.onPartSelected(partsSelect.value);
            return null;
        };
    }

    @Override
    public void setFieldParts(List<String> items) {
        partsSelect.innerHTML = "";
        panelTitle.innerHTML = "Field Parts";
        items.stream().map(this::itemToOption).forEach(partsSelect::appendChild);
    }

    @Override
    public void showPropertiesEditor(List<PropertyEditorCategory> properties) {
        contentDiv.hidden = false;
        panelTitle.innerHTML = "Field Parts";
        propertyEditorWidget.handle(new PropertyEditorEvent(PROPERTY_EDITOR_ID, properties));
    }
    
    private HTMLOptionElement itemToOption(String item) {
        HTMLOptionElement option = (HTMLOptionElement) partOption.cloneNode(false);
        option.text = item;
        option.value = item;
        return option;
    }

    @Override
    public void noPartsToEdit() {
        panelTitle.innerHTML = "Field has no parts to edit";
        contentDiv.hidden = true;
    }
    
    protected void onPropertyEditorChange(@Observes PropertyEditorChangeEvent event) {
        PropertyEditorFieldInfo property = event.getProperty();
        if (property.getEventId().equalsIgnoreCase(PROPERTY_EDITOR_ID)) {
            String attrKey = property.getKey();
            String attrValue = event.getNewValue();
            presenter.onPropertyChanged(attrKey, attrValue);
        }
    }

}
