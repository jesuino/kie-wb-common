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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabel;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Templated
public class DefaultFormGroupViewImpl implements IsElement,
                                                 DefaultFormGroupView {

    @Inject
    @DataField
    private FieldLabel fieldLabel;

    @DataField
    protected SimplePanel fieldContainer = new SimplePanel();

    @Inject
    @DataField
    protected Div helpBlock;

    @Override
    public void render(Widget widget,
                       FieldDefinition fieldDefinition) {

        render(widget.getElement().getId(),
               widget,
               fieldDefinition);
    }

    @Override
    public void render(String inputId,
                       Widget widget,
                       FieldDefinition fieldDefinition) {

        fieldLabel.renderForInputId(inputId,
                                    fieldDefinition);

        fieldContainer.clear();
        fieldContainer.add(widget);
    }

    @Override
    public void clearError() {

        DOMUtil.removeEnumStyleName(getElement(),
                                    ValidationState.ERROR);
        DOMUtil.addEnumStyleName(getElement(),
                                 ValidationState.NONE);
        helpBlock.setTextContent("");
    }

    @Override
    public void showError(String error) {
        DOMUtil.removeEnumStyleName(getElement(),
                                    ValidationState.NONE);
        DOMUtil.addEnumStyleName(getElement(),
                                 ValidationState.ERROR);
        helpBlock.setTextContent(error);
    }

    @Override
    public IsWidget getFieldLabel() {
        return ElementWrapperWidget.getWidget(fieldLabel.getElement());
    }
}
