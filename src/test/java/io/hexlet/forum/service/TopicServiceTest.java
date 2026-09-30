package io.hexlet.forum.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hexlet.forum.dto.TopicCreateDTO;
import io.hexlet.forum.dto.TopicDTO;
import io.hexlet.forum.dto.TopicUpdateDTO;
import io.hexlet.forum.exception.ResourceNotFoundException;
import io.hexlet.forum.mapper.TopicMapper;
import io.hexlet.forum.model.Topic;
import io.hexlet.forum.model.User;
import io.hexlet.forum.repository.TopicRepository;

@ExtendWith(MockitoExtension.class)
public class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private TopicMapper topicMapper;

    @InjectMocks
    private TopicService topicService;

    private Topic testTopic;
    private TopicDTO testTopicDTO;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("alice");
        testUser.setEmail("alice@example.com");

        testTopic = new Topic();
        testTopic.setId(1L);
        testTopic.setAuthor(testUser);
        testTopic.setSlug("test-slug");
        testTopic.setTitle("Test title");
        testTopic.setBody("Test body");

        testTopicDTO = new TopicDTO();
        testTopicDTO.setId(1L);
        testTopicDTO.setAuthorId(1L);
        testTopicDTO.setSlug("test-slug");
        testTopicDTO.setTitle("Test title");
        testTopicDTO.setBody("Test body");
    }

    @Test
    @DisplayName("getAll: возвращает список всех тем")
    public void testGetAll() {
        when(topicRepository.findAll()).thenReturn(List.of(testTopic));
        when(topicMapper.map(testTopic)).thenReturn(testTopicDTO);

        List<TopicDTO> result = topicService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test title");
        verify(topicRepository).findAll();
    }

    @Test
    @DisplayName("getById: находит тему по id")
    public void testGetById() {
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicMapper.map(testTopic)).thenReturn(testTopicDTO);

        TopicDTO result = topicService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test title");
    }

    @Test
    @DisplayName("getById: бросает ResourceNotFoundException, если темы нет")
    public void testGetByIdNotFound() {
        when(topicRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Topic not found: 999");
    }

    @Test
    @DisplayName("create: сохраняет тему и возвращает DTO")
    public void testCreate() {
        var createDTO = new TopicCreateDTO();
        createDTO.setAuthorId(1L);
        createDTO.setSlug("test-slug");
        createDTO.setTitle("Test title");
        createDTO.setBody("Test body");

        when(topicMapper.map(createDTO)).thenReturn(testTopic);
        when(topicRepository.save(testTopic)).thenReturn(testTopic);
        when(topicMapper.map(testTopic)).thenReturn(testTopicDTO);

        TopicDTO result = topicService.create(createDTO);

        assertThat(result.getTitle()).isEqualTo("Test title");
        verify(topicRepository).save(testTopic);
    }

    @Test
    @DisplayName("update: обновляет существующую тему")
    public void testUpdate() {
        var updateDTO = new TopicUpdateDTO();
        updateDTO.setTitle("Updated title");

        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(testTopic)).thenReturn(testTopic);
        when(topicMapper.map(testTopic)).thenReturn(testTopicDTO);

        TopicDTO result = topicService.update(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(topicMapper).update(updateDTO, testTopic);
        verify(topicRepository).save(testTopic);
    }

    @Test
    @DisplayName("update: бросает ResourceNotFoundException, если темы нет")
    public void testUpdateNotFound() {
        var updateDTO = new TopicUpdateDTO();
        when(topicRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.update(999L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete: вызывает deleteById у репозитория")
    public void testDelete() {
        topicService.delete(1L);
        verify(topicRepository).deleteById(1L);
    }
}