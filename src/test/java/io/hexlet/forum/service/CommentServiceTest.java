package io.hexlet.forum.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import io.hexlet.forum.dto.CommentCreateDTO;
import io.hexlet.forum.dto.CommentDTO;
import io.hexlet.forum.dto.CommentUpdateDTO;
import io.hexlet.forum.exception.ResourceNotFoundException;
import io.hexlet.forum.mapper.CommentMapper;
import io.hexlet.forum.model.Comment;
import io.hexlet.forum.model.Topic;
import io.hexlet.forum.model.User;
import io.hexlet.forum.repository.CommentRepository;
import io.hexlet.forum.repository.TopicRepository;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Topic testTopic;
    private Comment testComment;
    private CommentDTO testCommentDTO;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("bob");
        testUser.setEmail("bob@example.com");

        testTopic = new Topic();
        testTopic.setId(1L);
        testTopic.setAuthor(testUser);
        testTopic.setSlug("topic-slug");
        testTopic.setTitle("Topic");
        testTopic.setBody("Body");

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setAuthor(testUser);
        testComment.setTopic(testTopic);
        testComment.setBody("Comment body");

        testCommentDTO = new CommentDTO();
        testCommentDTO.setId(1L);
        testCommentDTO.setAuthorId(1L);
        testCommentDTO.setTopicId(1L);
        testCommentDTO.setBody("Comment body");
    }

    @Test
    @DisplayName("getByTopicId: возвращает комментарии темы")
    public void testGetByTopicId() {
        when(topicRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findByTopicId(1L)).thenReturn(List.of(testComment));
        when(commentMapper.map(testComment)).thenReturn(testCommentDTO);

        List<CommentDTO> result = commentService.getByTopicId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBody()).isEqualTo("Comment body");
    }

    @Test
    @DisplayName("getByTopicId: бросает исключение, если темы нет")
    public void testGetByTopicIdTopicNotFound() {
        when(topicRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> commentService.getByTopicId(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getById: возвращает комментарий")
    public void testGetById() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentMapper.map(testComment)).thenReturn(testCommentDTO);

        CommentDTO result = commentService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getById: бросает исключение, если комментария нет")
    public void testGetByIdNotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Comment not found: 999");
    }

    @Test
    @DisplayName("create: создаёт комментарий и привязывает к теме")
    public void testCreate() {
        var createDTO = new CommentCreateDTO();
        createDTO.setAuthorId(1L);
        createDTO.setTopicId(1L);
        createDTO.setBody("New comment");

        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(commentMapper.map(createDTO)).thenReturn(testComment);
        when(commentRepository.save(testComment)).thenReturn(testComment);
        when(commentMapper.map(testComment)).thenReturn(testCommentDTO);

        CommentDTO result = commentService.create(1L, createDTO);

        assertThat(result).isNotNull();
        verify(commentRepository).save(testComment);
    }

    @Test
    @DisplayName("create: бросает исключение, если темы нет")
    public void testCreateTopicNotFound() {
        var createDTO = new CommentCreateDTO();
        when(topicRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(999L, createDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update: обновляет комментарий")
    public void testUpdate() {
        var updateDTO = new CommentUpdateDTO();
        updateDTO.setBody("Updated");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(testComment)).thenReturn(testComment);
        when(commentMapper.map(testComment)).thenReturn(testCommentDTO);

        CommentDTO result = commentService.update(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(commentMapper).update(updateDTO, testComment);
    }

    @Test
    @DisplayName("delete: вызывает deleteById у репозитория")
    public void testDelete() {
        commentService.delete(1L);
        verify(commentRepository).deleteById(1L);
    }
}