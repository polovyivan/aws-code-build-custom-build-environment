package com.polovyi.ivan.awscodebuildtutorial;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.polovyi.ivan.awscodebuildtutorial.dto.DeveloperToolsResponse;
import com.polovyi.ivan.awscodebuildtutorial.service.DeveloperToolsService;
import lombok.SneakyThrows;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class AwsCodeBuildTutorialApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeveloperToolsService developerToolsService;

    protected MockHttpServletResponse response;

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldReturnAllDeveloperTools() throws Exception {
        doReturn(Stream.of(new DeveloperToolsResponse(1, "CodeCommit")).collect(Collectors.toSet()))
                .when(developerToolsService).getAllTools();
        response = mockMvc.perform(get("/developer-tools")).andReturn()
                .getResponse();
        assertFalse(stringJsonToList(response.getContentAsString(), DeveloperToolsResponse.class).isEmpty());
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void shouldReturnDeveloperToolByGivenId() throws Exception {
        DeveloperToolsResponse mockResponse = new DeveloperToolsResponse(1, "CodeCommit");
        doReturn(mockResponse)
                .when(developerToolsService).getDeveloperToolById(anyInt());
        response = mockMvc.perform(get("/developer-tools/{id}", 1)).andReturn()
                .getResponse();
        DeveloperToolsResponse developerToolsResponse = stringJsonToObject(response.getContentAsString(),
                DeveloperToolsResponse.class);
        assertEquals(mockResponse, developerToolsResponse);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @SneakyThrows
    public <T> List<T> stringJsonToList(String json, Class<T> clazz) {
        return mapper.readValue(json, new TypeReference<List<T>>() {
            @Override
            public Type getType() {
                return mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            }
        });
    }

    @SneakyThrows
    public <T> T stringJsonToObject(String json, Class<T> clazz) {
        return mapper.readValue(json, clazz);
    }

}
