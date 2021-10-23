package com.polovyi.ivan.awscodebuildtutorial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperToolsResponse {
    private Integer id;
    private String name;
}
