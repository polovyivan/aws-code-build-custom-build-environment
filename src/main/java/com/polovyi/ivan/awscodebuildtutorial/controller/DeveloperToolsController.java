package com.polovyi.ivan.awscodebuildtutorial.controller;

import com.polovyi.ivan.awscodebuildtutorial.dto.DeveloperToolsResponse;
import com.polovyi.ivan.awscodebuildtutorial.service.DeveloperToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/developer-tools")
public record DeveloperToolsController(DeveloperToolsService developerToolsService) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Set<DeveloperToolsResponse> getAllDeveloperTools() {
        return developerToolsService.getAllTools();
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DeveloperToolsResponse getDeveloperToolById(@PathVariable Integer id) {
        return developerToolsService.getDeveloperToolById(id);
    }
}
