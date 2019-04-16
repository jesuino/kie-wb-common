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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.kie.workbench.common.forms.common.rendering.client.widgets.flatViews.impl.DateFlatView;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;

@Dependent
public class DatePickerFieldRenderer extends FieldRenderer<DatePickerFieldDefinition, DefaultFormGroup> {

    /**
     * 
     */
    private static final String PART_DATE_PICKER = "Date Picker";

    private Widget input;

    protected WidgetHandler handler;

    @Override
    public String getName() {
        return "DatePicker";
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {

        DefaultFormGroup formGroup = formGroupsInstance.get();

        if (renderMode.equals(RenderMode.PRETTY_MODE)) {
            formGroup.render(new DateFlatView(),
                             field);
        } else {
            String inputId = generateUniqueId();
            input = getDateWidget(inputId);
            formGroup.render(inputId,
                             input,
                             field);
        }

        return formGroup;
    }

    private Widget getDateWidget(String inputId) {
        if (field.getShowTime()) {
            DateTimePicker box = new DateTimePicker();
            box.setId(inputId);
            box.setName(fieldNS);
            box.setPlaceholder(field.getPlaceHolder());
            box.setEnabled(!field.getReadOnly());
            box.setAutoClose(true);
            box.setHighlightToday(true);
            box.setShowTodayButton(true);
            handler = readOnly -> box.setEnabled(!readOnly);
            partsMapping.put(PART_DATE_PICKER, box);
            return box;
        }

        DatePicker box = new DatePicker();
        box.setId(inputId);
        box.setName(fieldNS);
        box.setPlaceholder(field.getPlaceHolder());
        box.setEnabled(!field.getReadOnly());
        box.setAutoClose(true);
        box.setHighlightToday(true);
        box.setShowTodayButton(true);
        box.setContainer(RootPanel.get());
        handler = readOnly -> box.setEnabled(!readOnly);
        partsMapping.put(PART_DATE_PICKER, box);
        return box;
    }

    @Override
    public String getSupportedCode() {
        return DatePickerFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        handler.setReadOnly(readOnly);
    }

    protected interface WidgetHandler {

        void setReadOnly(boolean readOnly);
    }
    
    @Override
    public List<String> getRendererStylableParts() {
        return Arrays.asList(PART_DATE_PICKER);
    }
}
