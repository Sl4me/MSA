package io.hexlet.forum.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.hexlet.forum.dto.TopicCreateDTO;
import io.hexlet.forum.dto.TopicDTO;
import io.hexlet.forum.dto.TopicUpdateDTO;
import io.hexlet.forum.exception.ResourceNotFoundException;
import io.hexlet.forum.mapper.TopicMapper;
import io.hexlet.forum.model.Topic;
import io.hexlet.forum.repository.TopicRepository;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    public TopicService(TopicRepository topicRepository, TopicMapper topicMapper) {
        this.topicRepository = topicRepository;
        this.topicMapper = topicMapper;
    }

    public List<TopicDTO> getAll() {
        return topicRepository.findAll().stream()
                .map(topicMapper::map)
                .toList();
    }

    public TopicDTO getById(Long id) {
        var topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + id));
        return topicMapper.map(topic);
    }

    public TopicDTO getBySlug(String slug) {
        var topic = topicRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + slug));
        return topicMapper.map(topic);
    }

    public TopicDTO create(TopicCreateDTO dto) {
        var topic = topicMapper.map(dto);
        topicRepository.save(topic);
        return topicMapper.map(topic);
    }

    public TopicDTO update(Long id, TopicUpdateDTO dto) {
        var topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + id));
        topicMapper.update(dto, topic);
        topicRepository.save(topic);
        return topicMapper.map(topic);
    }

    public void delete(Long id) {
        topicRepository.deleteById(id);
    }
}