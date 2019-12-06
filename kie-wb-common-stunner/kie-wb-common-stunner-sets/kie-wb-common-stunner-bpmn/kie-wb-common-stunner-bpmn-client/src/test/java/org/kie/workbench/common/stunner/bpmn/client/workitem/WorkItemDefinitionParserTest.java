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

package org.kie.workbench.common.stunner.bpmn.client.workitem;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorkItemDefinitionParserTest {

    final static String WID = "  [\n" +
                              "    [\n" +
                              "      \"name\" : \"Email\",\n" +
                              "      \"parameters\" : [\n" +
                              "        \"From\" : new StringDataType(),\n" +
                              "        \"To\" : new StringDataType(),\n" +
                              "        \"Subject\" : new StringDataType(),\n" +
                              "        \"Body\" : new StringDataType()\n" +
                              "      ],\n" +
                              "      \"displayName\" : \"Email\",\n" +
                              "      \"icon\" : \"defaultemailicon.gif\"\n" +
                              "    ],\n" +
                              "  \n" +
                              "    [\n" +
                              "      \"name\" : \"Log\",\n" +
                              "      \"parameters\" : [\n" +
                              "        \"Message\" : new StringDataType()\n" +
                              "      ],\n" +
                              "      \"displayName\" : \"Log\",\n" +
                              "      \"icon\" : \"defaultlogicon.gif\"\n" +
                              "    ],\n" +
                              "  \n" +
                              "    [\n" +
                              "      \"name\" : \"WebService\",\n" +
                              "      \"parameters\" : [\n" +
                              "          \"Url\" : new StringDataType(),\n" +
                              "           \"Namespace\" : new StringDataType(),\n" +
                              "           \"Interface\" : new StringDataType(),\n" +
                              "           \"Operation\" : new StringDataType(),\n" +
                              "           \"Parameter\" : new StringDataType(),\n" +
                              "           \"Endpoint\" : new StringDataType(),\n" +
                              "           \"Mode\" : new StringDataType()\n" +
                              "      ],\n" +
                              "      \"results\" : [\n" +
                              "          \"Result\" : new ObjectDataType(),\n" +
                              "      ],\n" +
                              "      \"displayName\" : \"WS\",\n" +
                              "      \"icon\" : \"defaultservicenodeicon.png\"\n" +
                              "    ],\n" +
                              "  \n" +
                              "    [\n" +
                              "      \"name\" : \"Rest\",\n" +
                              "      \"parameters\" : [\n" +
                              "          \"ContentData\" : new StringDataType(),\n" +
                              "          \"Url\" : new StringDataType(),\n" +
                              "          \"Method\" : new StringDataType(),\n" +
                              "          \"ConnectTimeout\" : new StringDataType(),\n" +
                              "          \"ReadTimeout\" : new StringDataType(),\n" +
                              "          \"Username\" : new StringDataType(),\n" +
                              "          \"Password\" : new StringDataType()\n" +
                              "      ],\n" +
                              "      \"results\" : [\n" +
                              "          \"Result\" : new ObjectDataType(),\n" +
                              "      ],\n" +
                              "      \"displayName\" : \"REST\",\n" +
                              "      \"icon\" : \"defaultservicenodeicon.png\"\n" +
                              "    ],\n" +
                              "  \n" +
                              "    [\n" +
                              "       \"name\" : \"BusinessRuleTask\",\n" +
                              "       \"parameters\" : [\n" +
                              "         \"Language\" : new StringDataType(),\n" +
                              "         \"KieSessionName\" : new StringDataType(),\n" +
                              "         \"KieSessionType\" : new StringDataType()\n" +
                              "       ],\n" +
                              "       \"displayName\" : \"Business Rule Task\",\n" +
                              "       \"icon\" : \"defaultbusinessrulesicon.png\",\n" +
                              "       \"category\" : \"Decision tasks\"\n" +
                              "     ],\n" +
                              "  \n" +
                              "     [\n" +
                              "       \"name\" : \"DecisionTask\",\n" +
                              "       \"parameters\" : [\n" +
                              "         \"Language\" : new StringDataType(),\n" +
                              "         \"Namespace\" : new StringDataType(),\n" +
                              "         \"Model\" : new StringDataType(),\n" +
                              "         \"Decision\" : new StringDataType()\n" +
                              "       ],\n" +
                              "       \"displayName\" : \"Decision Task\",\n" +
                              "       \"icon\" : \"defaultdecisionicon.png\",\n" +
                              "       \"category\" : \"Decision tasks\"\n" +
                              "     ],\n" +
                              "  \n" +
                              "     [\n" +
                              "      \"name\" : \"Milestone\",\n" +
                              "      \"parameters\" : [\n" +
                              "          \"Condition\" : new StringDataType()\n" +
                              "      ],\n" +
                              "      \"displayName\" : \"Milestone\",\n" +
                              "      \"icon\" : \"defaultmilestoneicon.png\",\n" +
                              "      \"category\" : \"Milestone\"\n" +
                              "      ]\n" +
                              "  ]";

    final static String INVALID_WID = "  [\n" +
            "    [\n" +
            "      \"name\" : \"Email\",\n" +
            "      \"parameters\" : [\n" +
            "        \"From\" : new StringDataType(),\n" +
            "        \"To\" : new StringDataType(),\n" +
            "        \"Subject\" : new StringDataType(),\n" +
            "        \"Body\" : new StringDataType()\n" +
            "      !!! BROKEN !!!,\n" +
            "      \"displayName\" : \"Email\",\n" +
            "      \"icon\" : \"defaultemailicon.gif\"\n" +
            "    ]\n" +
            "  ]";

    final static String WID_ONE_LINER = "  [[\"name\" : \"Email\",\"parameters\" : [\"From\" : new StringDataType()," +
            "\"To\" : new StringDataType(),\"Subject\" : new StringDataType(),\"Body\" : new StringDataType()]," +
            "\"displayName\" : \"Email\",\"icon\" : \"defaultemailicon.gif\"]]";

    final static String EMAIL_WID_EXTRACTED_PARAMETERS = "\"From\" : new StringDataType(),\"To\" : new StringDataType()," +
                                                         "\"Subject\" : new StringDataType(),\"Body\" : new StringDataType()";

    final static String REST_WID_EXTRACTED_PARAMETERS = "\"ContentData\" : new StringDataType(),\"Url\" : new StringDataType()," +
                                                        "\"Method\" : new StringDataType(),\"ConnectTimeout\" : new StringDataType()," +
                                                        "\"ReadTimeout\" : new StringDataType(),\"Username\" : new StringDataType()," +
                                                        "\"Password\" : new StringDataType()";

    WorkItemDefinitionParser parser = new WorkItemDefinitionParser();

    @Test
    public void emptyWidsTest() {
        List<WorkItemDefinition> defs = parser.parse("");
        assertTrue(defs.isEmpty());
        defs = parser.parse("[]");
        assertTrue(defs.isEmpty());
        defs = parser.parse("[\n]");
        assertTrue(defs.isEmpty());
        defs = parser.parse(null);
        assertTrue(defs.isEmpty());
    }

    @Test
    public void testUnclosedBracketsWid() {
        List<WorkItemDefinition> definitions = parser.parse(INVALID_WID);
        Assertions.assertThat(definitions).isEmpty();
    }

    @Test
    public void testWidOneLiner() {
        List<WorkItemDefinition> definitions = parser.parse(WID_ONE_LINER);
        Assertions.assertThat(definitions).hasSize(1);
    }

    @Test
    public void widParseTest() {
        List<WorkItemDefinition> defs = parser.parse(WID);
        assertEquals(7, defs.size());
        WorkItemDefinition wid1 = defs.get(0);
        assertEquals("Email", wid1.getName());
        assertEquals("Email", wid1.getDisplayName());
        assertEquals("defaultemailicon.gif", wid1.getIconDefinition().getUri());
        assertTrue(wid1.getResults().isEmpty());
        assertEquals(EMAIL_WID_EXTRACTED_PARAMETERS, wid1.getParameters());

        WorkItemDefinition wid2 = defs.get(3);

        assertEquals("Rest", wid2.getName());
        assertEquals("REST", wid2.getDisplayName());
        assertEquals("defaultservicenodeicon.png", wid2.getIconDefinition().getUri());
        assertTrue(wid1.getResults().isEmpty());

        assertEquals(REST_WID_EXTRACTED_PARAMETERS, wid2.getParameters());
        assertEquals("\"Result\" : new ObjectDataType(),", wid2.getResults());

    }
}
