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

import io.hexlet.forum.dto.CommentCreateDTO;
import io.hexlet.forum.dto.CommentDTO;
import io.hexlet.forum.dto.CommentUpdateDTO;
import io.hexlet.forum.exception.GlobalExceptionHandler;
import io.hexlet.forum.exception.ResourceNotFoundException;
import io.hexlet.forum.service.CommentService;

@WebMvcTest(CommentController.class)
@Import(GlobalExceptionHandler.class)
public class CommentControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentDTO testCommentDTO;

    @BeforeEach
    public void setUp() {
        testCommentDTO = new CommentDTO();
        testCommentDTO.setId(1L);
        testCommentDTO.setAuthorId(1L);
        testCommentDTO.setTopicId(1L);
        testCommentDTO.setBody("Test comment");
        testCommentDTO.setCreatedAt(LocalDate.now());
        testCommentDTO.setUpdatedAt(LocalDate.now());
    }

    @Test
    @DisplayName("GET /api/topics/{topicId}/comments: список комментариев")
    public void testIndexByTopic() throws Exception {
        when(commentService.getByTopicId(1L)).thenReturn(List.of(testCommentDTO));

        mockMvc.perform(get("/api/topics/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].body").value("Test comment"));
    }

    @Test
    @DisplayName("POST /api/topics/{topicId}/comments: создаёт комментарий")
    public void testCreate() throws Exception {
        var createDTO = new CommentCreateDTO();
        createDTO.setAuthorId(1L);
        createDTO.setTopicId(1L);
        createDTO.setBody("Test comment");

        when(commentService.create(eq(1L), any(CommentCreateDTO.class))).thenReturn(testCommentDTO);

        mockMvc.perform(post("/api/topics/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.body").value("Test comment"));

        verify(commentService).create(eq(1L), any(CommentCreateDTO.class));
    }

    @Test
    @DisplayName("GET /api/comments/{id}: возвращает комментарий")
    public void testShow() throws Exception {
        when(commentService.getById(1L)).thenReturn(testCommentDTO);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("Test comment"));
    }

    @Test
    @DisplayName("GET /api/comments/{id}: 404, если комментария нет")
    public void testShowNotFound() throws Exception {
        when(commentService.getById(999L))
                .thenThrow(new ResourceNotFoundException("Comment not found: 999"));

        mockMvc.perform(get("/api/comments/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Comment not found: 999"));
    }

    @Test
    @DisplayName("PUT /api/comments/{id}: обновляет комментарий")
    public void testUpdate() throws Exception {
        var updateDTO = new CommentUpdateDTO();
        updateDTO.setBody("Updated comment");

        when(commentService.update(eq(1L), any(CommentUpdateDTO.class))).thenReturn(testCommentDTO);

        mockMvc.perform(put("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("Test comment"));

        verify(commentService).update(eq(1L), any(CommentUpdateDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/comments/{id}: удаляет комментарий")
    public void testDelete() throws Exception {
        doNothing().when(commentService).delete(1L);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNoContent());

        verify(commentService).delete(1L);
    }
}