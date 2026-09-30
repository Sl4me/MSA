package io.hexlet.forum.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.hexlet.forum.dto.CommentCreateDTO;
import io.hexlet.forum.dto.CommentDTO;
import io.hexlet.forum.dto.CommentUpdateDTO;
import io.hexlet.forum.service.CommentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // GET /api/topics/{topicId}/comments
    @GetMapping("/topics/{topicId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDTO> indexByTopic(@PathVariable Long topicId) {
        return commentService.getByTopicId(topicId);
    }

    // POST /api/topics/{topicId}/comments
    @PostMapping("/topics/{topicId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO create(@PathVariable Long topicId,
                             @Valid @RequestBody CommentCreateDTO dto) {
        return commentService.create(topicId, dto);
    }

    // GET /api/comments/{id}
    @GetMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDTO show(@PathVariable Long id) {
        return commentService.getById(id);
    }

    // PUT /api/comments/{id}
    @PutMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDTO update(@PathVariable Long id,
                             @Valid @RequestBody CommentUpdateDTO dto) {
        return commentService.update(id, dto);
    }

    // DELETE /api/comments/{id}
    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }
}