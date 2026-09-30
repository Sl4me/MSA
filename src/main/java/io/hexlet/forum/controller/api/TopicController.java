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

import io.hexlet.forum.dto.TopicCreateDTO;
import io.hexlet.forum.dto.TopicDTO;
import io.hexlet.forum.dto.TopicUpdateDTO;
import io.hexlet.forum.service.TopicService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TopicDTO> index() {
        return topicService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TopicDTO create(@Valid @RequestBody TopicCreateDTO dto) {
        return topicService.create(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TopicDTO show(@PathVariable Long id) {
        return topicService.getById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TopicDTO update(@PathVariable Long id, @Valid @RequestBody TopicUpdateDTO dto) {
        return topicService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        topicService.delete(id);
    }
}