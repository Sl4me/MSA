package io.hexlet.forum.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import io.hexlet.forum.dto.TopicCreateDTO;
import io.hexlet.forum.dto.TopicDTO;
import io.hexlet.forum.dto.TopicUpdateDTO;
import io.hexlet.forum.exception.GlobalExceptionHandler;
import io.hexlet.forum.exception.ResourceNotFoundException;
import io.hexlet.forum.service.TopicService;

@WebMvcTest(TopicController.class)
@Import(GlobalExceptionHandler.class)
public class TopicControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TopicService topicService;

    @Autowired
    private ObjectMapper objectMapper;

    private TopicDTO testTopicDTO;

    @BeforeEach
    public void setUp() {
        testTopicDTO = new TopicDTO();
        testTopicDTO.setId(1L);
        testTopicDTO.setAuthorId(1L);
        testTopicDTO.setSlug("test-slug");
        testTopicDTO.setTitle("Test title");
        testTopicDTO.setBody("Test body");
        testTopicDTO.setCreatedAt(LocalDate.now());
        testTopicDTO.setUpdatedAt(LocalDate.now());
    }

    @Test
    @DisplayName("GET /api/topics: возвращает список тем")
    public void testIndex() throws Exception {
        when(topicService.getAll()).thenReturn(List.of(testTopicDTO));

        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test title"));
    }

    @Test
    @DisplayName("POST /api/topics: создаёт тему")
    public void testCreate() throws Exception {
        var createDTO = new TopicCreateDTO();
        createDTO.setAuthorId(1L);
        createDTO.setSlug("test-slug");
        createDTO.setTitle("Test title");
        createDTO.setBody("Test body");

        when(topicService.create(any(TopicCreateDTO.class))).thenReturn(testTopicDTO);

        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.slug").value("test-slug"));

        verify(topicService).create(any(TopicCreateDTO.class));
    }

    @Test
    @DisplayName("GET /api/topics/{id}: возвращает тему по id")
    public void testShow() throws Exception {
        when(topicService.getById(1L)).thenReturn(testTopicDTO);

        mockMvc.perform(get("/api/topics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.body").value("Test body"));
    }

    @Test
    @DisplayName("GET /api/topics/{id}: 404, если темы нет")
    public void testShowNotFound() throws Exception {
        when(topicService.getById(999L))
                .thenThrow(new ResourceNotFoundException("Topic not found: 999"));

        mockMvc.perform(get("/api/topics/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Topic not found: 999"));
    }

    @Test
    @DisplayName("PUT /api/topics/{id}: обновляет тему")
    public void testUpdate() throws Exception {
        var updateDTO = new TopicUpdateDTO();
        updateDTO.setTitle("Updated title");

        when(topicService.update(eq(1L), any(TopicUpdateDTO.class))).thenReturn(testTopicDTO);

        mockMvc.perform(put("/api/topics/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test title"));

        verify(topicService).update(eq(1L), any(TopicUpdateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/topics/{id}: удаляет тему")
    public void testDelete() throws Exception {
        doNothing().when(topicService).delete(1L);

        mockMvc.perform(delete("/api/topics/1"))
                .andExpect(status().isNoContent());

        verify(topicService).delete(1L);
    }
}