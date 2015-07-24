/*
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

package org.elasticsearch.enforcer.rules;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class BannedAttachedArtifacts implements EnforcerRule {

    private List<String> excludes = Collections.emptyList();

    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
        Log log = helper.getLog();

        List<Pattern> excludedPatterns = new ArrayList<Pattern>(excludes.size());
        for (String exclude : excludes) {
            excludedPatterns.add(Pattern.compile(exclude));
        }

        try {
            MavenProject project = (MavenProject) helper.evaluate("${project}");
            List<Artifact> artifactList = project.getAttachedArtifacts();
            for (Artifact artifact : artifactList) {
                String artifactFileName = artifact.getFile().getName();
                log.debug("evaluating artifact [" + artifactFileName + "]");
                for (Pattern pattern : excludedPatterns) {
                    if (pattern.matcher(artifactFileName).matches()) {
                        throw new EnforcerRuleException("found banned attached artifact: artifact [" + artifactFileName + "] matched exclude pattern [" + pattern.toString() + "]");
                    }
                }
            }

        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Unable to lookup expression", e);
        }

    }

    public boolean isCacheable() {
        return false;
    }

    public boolean isResultValid(EnforcerRule enforcerRule) {
        return false;
    }

    public String getCacheId() {
        return null;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }
}
