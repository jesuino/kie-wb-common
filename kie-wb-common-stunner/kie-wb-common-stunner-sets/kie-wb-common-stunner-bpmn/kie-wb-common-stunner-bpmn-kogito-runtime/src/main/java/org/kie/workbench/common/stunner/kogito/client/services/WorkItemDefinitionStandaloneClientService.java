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

package org.kie.workbench.common.stunner.kogito.client.services;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;

import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientService;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class WorkItemDefinitionStandaloneClientService implements WorkItemDefinitionClientService {

    private final Promises promises;
    private final WorkItemDefinitionCacheRegistry registry;
    private final WorkItemDefinitionParser parser;
    private Promise<Collection<WorkItemDefinition>> fetchWIDsPromise;
    private ResourceContentService resourceContentService;
    private boolean loadedWids;

    @Inject
    public WorkItemDefinitionStandaloneClientService(final Promises promises,
                                                     final WorkItemDefinitionCacheRegistry registry,
                                                     final WorkItemDefinitionParser parser,
                                                     final ResourceContentService resourceContentService) {
        this.promises = promises;
        this.registry = registry;
        this.parser = parser;
        this.resourceContentService = resourceContentService;
    }
    
    @PostConstruct
    public void loadWorkItemsDefinitions() {
        // for some reason the call method is called twice, hence I did this sort of cache mechanism
        // if it is called only one time so you don't need this
        loadedWids = false;
        fetchWIDsPromise = promises.create((success, failure) -> {
           if (loadedWids) {
               success.onInvoke(registry.items());
           } else {
               resourceContentService.list("**/*.wid").then(paths -> {
                   if (paths.length > 0) {
                       String path = paths[0];
                       DomGlobal.console.log("Loading WIDs from " + path);
                       resourceContentService.get(path).then(value -> {
                           Collection<WorkItemDefinition> wids = parser.parse(value);
                           wids.forEach(registry::register);
                           loadedWids = true;
                           success.onInvoke(wids);
                           return null;
                       });
                   } else {
                       DomGlobal.console.log("No WIDs to load");
                       loadedWids = true;
                       success.onInvoke(Collections.emptyList());
                   }
                   return null;
               });
           }
        });
    }

    @Override
    public Promise<Collection<WorkItemDefinition>> call(final Metadata input) {
        return fetchWIDsPromise;
    }
    
    @Produces
    @Default
    @Override
    public WorkItemDefinitionRegistry getRegistry() {
        return registry;
    }
}