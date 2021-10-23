package com.polovyi.ivan.awscodebuildtutorial.service;

import com.polovyi.ivan.awscodebuildtutorial.dto.DeveloperToolsResponse;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class DeveloperToolsService {

    private static final Set<DeveloperToolsResponse> developerTools;

    static {
        developerTools = new LinkedHashSet<>();
        developerTools.add(new DeveloperToolsResponse(1, "CodeCommit"));
        developerTools.add(new DeveloperToolsResponse(2, "CodeBuild"));
        developerTools.add(new DeveloperToolsResponse(3, "CodeArtifact"));
        developerTools.add(new DeveloperToolsResponse(4, "CodeDeploy"));
        developerTools.add(new DeveloperToolsResponse(5, "CodePipeline"));
    }

    public Set<DeveloperToolsResponse> getAllTools() {
        return developerTools;
    }

    public DeveloperToolsResponse getDeveloperToolById(Integer id) {

        return developerTools.stream().filter(t->t.getId().equals(id)).findFirst().orElse(null);
    }
}
