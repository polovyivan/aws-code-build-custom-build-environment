package com.polovyi.ivan.awscodebuildtutorial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperToolsListResponse {
    private Set<DeveloperToolsResponse> developerTools;
}
