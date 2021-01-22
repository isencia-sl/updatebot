/*
 * Copyright 2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package io.jenkins.updatebot.model;

import io.jenkins.updatebot.support.Strings;

/**
 * Represents the configuration of a git repository
 */
public class GitRepositoryConfig extends DtoSupport {
    private String name;
    private String branch; // need to resolve branch name at runtime
    private boolean useSinglePullRequest; // use single pull request to push commits from upstream
    private Boolean excludeUpdateLoop;
    private Dependencies push;
    private Dependencies pull;
    private String target;

    public GitRepositoryConfig() {
    }

    public GitRepositoryConfig(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String nameText = Strings.notEmpty(name) ? "name='" + name + '\'' : "";
        String pushText = push != null ? "push=" + push : "";
        String pullText = pull != null ? "pull=" + pull : "";
        String branchText = branch != null ? "branch=" + branch : "";
        String singlePullRequestText = "useSinglePullRequest=" + useSinglePullRequest;

        return "GitRepositoryConfig{" +
                Strings.joinNotEmpty(", ", nameText, pushText, pullText, branchText, singlePullRequestText) + '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Dependencies getPush() {
        return push;
    }

    public void setPush(Dependencies push) {
        this.push = push;
    }

    public Dependencies getPull() {
        return pull;
    }

    public void setPull(Dependencies pull) {
        this.pull = pull;
    }

    public String getBranch() {
        return this.branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public boolean isUseSinglePullRequest() {
        return this.useSinglePullRequest;
    }

    public void setUseSinglePullRequest(boolean single) {
        this.useSinglePullRequest = single;
    }

    public Boolean getExcludeUpdateLoop() {
        return excludeUpdateLoop;
    }

    public void setExcludeUpdateLoop(Boolean excludeUpdateLoop) {
        this.excludeUpdateLoop = excludeUpdateLoop;
    }

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
