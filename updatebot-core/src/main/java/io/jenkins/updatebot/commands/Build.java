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
package io.jenkins.updatebot.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.jenkins.updatebot.CommandNames;
import io.jenkins.updatebot.Configuration;
import io.jenkins.updatebot.github.PullRequests;
import io.jenkins.updatebot.model.GitRepositoryConfig;
import io.jenkins.updatebot.model.GithubOrganisation;
import io.jenkins.updatebot.repository.LocalRepository;
import io.jenkins.updatebot.support.FileHelper;

/**
 * Build any open pull requests
 */
@Parameters(commandNames = CommandNames.BUILD, commandDescription = "Build open Pull Requests.")
public class Build extends ModifyFilesCommandSupport {
	
	@Parameter(order = 0, names = {"--filter", "-f"}, description = "The filter")
    private String filter;
	
	private static final transient Logger LOG = LoggerFactory.getLogger(Build.class);
	
    public ParentContext run(Configuration configuration) throws IOException {
        validateConfiguration(configuration);

        ParentContext parentContext = new ParentContext();
        List<LocalRepository> repositories = cloneOrPullRepositories(configuration);
        for (LocalRepository repository : repositories) {
            CommandContext context = createCommandContext(repository, configuration);
            parentContext.addChild(context);
            run(context);
        }
        return parentContext;
    }

    @Override
    public void run(CommandContext context) throws IOException {
        GHRepository ghRepository = context.gitHubRepository();
        if (ghRepository != null) {
        	if (filter != null) 
        		LOG.info("Filtering targets by "+filter);
            List<GHPullRequest> pullRequests = PullRequests.getOpenPullRequests(ghRepository, (String)null);
            for (GHPullRequest pullRequest : pullRequests) {
            	context.setPullRequest(pullRequest);
            	if (filter != null) {
	                File target = getTargetFile(context);
	                if (target != null) {
	                	if (!contains(target,filter))
	                		continue;
	                }
            	}
            	context.setPullRequest(pullRequest);
                
                pullRequest.comment("/test this");
                LOG.info("Prow comment sent to Pull Request " + pullRequest.getHtmlUrl());
            }
        }
    }
                
    private boolean contains(File target, String filter) {
		String content = "";
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(target.getAbsolutePath()) ) );
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return (content.contains(filter));
	}

	protected File getTargetFile(CommandContext context)      {
    	GHRepository ghRepository = context.gitHubRepository();
    
    	try {
			for(GithubOrganisation org: context.getConfiguration().loadRepositoryConfig().getGithub().getOrganisations()){
			    for(GitRepositoryConfig repo :org.getRepositories()){
			        if(ghRepository.getName().equalsIgnoreCase(repo.getName())){
			        	if (repo.getTarget() != null) {
			        		prepareDirectory(context);
			        		return new File(context.getRepository().getDir(),repo.getTarget());
			        	}
			        }
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
}
