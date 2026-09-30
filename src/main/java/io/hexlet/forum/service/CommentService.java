package io.hexlet.forum.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.hexlet.forum.dto.CommentCreateDTO;
import io.hexlet.forum.dto.CommentDTO;
import io.hexlet.forum.dto.CommentUpdateDTO;
import io.hexlet.forum.exception.ResourceNotFoundException;
import io.hexlet.forum.mapper.CommentMapper;
import io.hexlet.forum.repository.CommentRepository;
import io.hexlet.forum.repository.TopicRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository,
                          TopicRepository topicRepository,
                          CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.topicRepository = topicRepository;
        this.commentMapper = commentMapper;
    }

    public List<CommentDTO> getByTopicId(Long topicId) {
        // Проверяем, что тема существует
        if (!topicRepository.existsById(topicId)) {
            throw new ResourceNotFoundException("Topic not found: " + topicId);
        }
        return commentRepository.findByTopicId(topicId).stream()
                .map(commentMapper::map)
                .toList();
    }

    public CommentDTO getById(Long id) {
        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + id));
        return commentMapper.map(comment);
    }

    public CommentDTO create(Long topicId, CommentCreateDTO dto) {
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));

        var comment = commentMapper.map(dto);
        comment.setTopic(topic); // привязываем к теме из URL
        commentRepository.save(comment);
        return commentMapper.map(comment);
    }

    public CommentDTO update(Long id, CommentUpdateDTO dto) {
        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + id));
        commentMapper.update(dto, comment);
        commentRepository.save(comment);
        return commentMapper.map(comment);
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
}