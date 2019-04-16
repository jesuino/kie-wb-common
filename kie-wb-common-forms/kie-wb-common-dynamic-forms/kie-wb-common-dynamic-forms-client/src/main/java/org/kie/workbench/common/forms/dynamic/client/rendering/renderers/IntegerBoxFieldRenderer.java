/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.common.rendering.client.util.valueConverters.ValueConvertersFactory;
import org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox.IntegerBox;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;

@Dependent
public class IntegerBoxFieldRenderer extends FieldRenderer<IntegerBoxFieldDefinition, DefaultFormGroup>
        implements RequiresValueConverter {

    private static final String INTEGER_BOX = "Integer Box";
    private IntegerBox integerBox;

    @Inject
    public IntegerBoxFieldRenderer(IntegerBox integerBox) {
        this.integerBox = integerBox;
    }

    @Override
    public String getName() {
        return "IntegerBox";
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {

        Widget widget;

        String inputId = generateUniqueId();

        if (renderMode.equals(RenderMode.PRETTY_MODE)) {
            widget = new HTML();
            widget.getElement().setId(inputId);
        } else {
            integerBox.setId(inputId);
            integerBox.setPlaceholder(field.getPlaceHolder());
            integerBox.setMaxLength(field.getMaxLength());
            integerBox.setEnabled(!field.getReadOnly());
            widget = integerBox.asWidget();
            partsMapping.put(INTEGER_BOX, integerBox);
        }

        DefaultFormGroup formGroup = formGroupsInstance.get();

        formGroup.render(inputId,
                         widget,
                         field);

        return formGroup;
    }

    @Override
    public String getSupportedCode() {
        return IntegerBoxFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        integerBox.setEnabled(!readOnly);
    }

    @Override
    public Converter getConverter() {
        return ValueConvertersFactory.getConverterForType(field.getStandaloneClassName());
    }
    
    @Override
    public List<String> getRendererStylableParts() {
        return Arrays.asList(INTEGER_BOX);
    }
}
