package org.elasticsearch.enforcer.rules;/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class BannedAttachedArtifactsTests {

    BannedAttachedArtifacts enforcer;
    EnforcerRuleHelper helper;

    @Before
    public void setup() throws Exception {
        enforcer = new BannedAttachedArtifacts();
        helper = mock(EnforcerRuleHelper.class);
        when(helper.getLog()).thenReturn(mock(Log.class));
        MavenProject project = mock(MavenProject.class);
        when(helper.evaluate("${project}")).thenReturn(project);

        // add some artifacts
        List<Artifact> artifacts = new ArrayList<>();
        Artifact zip = mock(Artifact.class);
        when(zip.getFile()).thenReturn(Files.createTempFile("banned-artifacts", ".zip").toFile());
        artifacts.add(zip);
        Artifact foo = mock(Artifact.class);
        when(foo.getFile()).thenReturn(Files.createTempFile("good", ".foo").toFile());
        artifacts.add(foo);
        when(project.getAttachedArtifacts()).thenReturn(artifacts);
    }

    @Test
    public void testNoRules() throws Exception {
        enforcer.execute(helper);
    }

    @Test
    public void testNonMatchingRule() throws Exception {
        enforcer.setExcludes(Collections.singletonList(".*jar"));
        enforcer.execute(helper);
    }

    @Test
    public void testMatchingRule() {
        enforcer.setExcludes(Collections.singletonList(".*zip"));
        try {
            enforcer.execute(helper);
            fail("an exception should have been thrown");
        } catch (EnforcerRuleException e) {
            assertThat(e.getMessage(), containsString("found banned attached artifact"));
        }
    }

    @Test
    public void testMatchingAndNonMatchingRule() {
        List<String> rules = new ArrayList<>();
        rules.add(".*jar");
        rules.add(".*zip");
        Collections.shuffle(rules);

        enforcer.setExcludes(rules);
        try {
            enforcer.execute(helper);
            fail("an exception should have been thrown");
        } catch (EnforcerRuleException e) {
            assertThat(e.getMessage(), containsString("found banned attached artifact"));
        }
    }
}
