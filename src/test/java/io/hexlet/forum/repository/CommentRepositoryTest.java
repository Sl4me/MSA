package io.hexlet.forum.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import io.hexlet.forum.model.Comment;
import io.hexlet.forum.model.Topic;
import io.hexlet.forum.model.User;

@DataJpaTest
@ActiveProfiles("test")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Topic testTopic;
    private Comment testComment;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setUsername("bob");
        testUser.setEmail("bob@example.com");
        userRepository.save(testUser);

        testTopic = new Topic();
        testTopic.setAuthor(testUser);
        testTopic.setSlug("topic-slug");
        testTopic.setTitle("Topic title");
        testTopic.setBody("Topic body");
        topicRepository.save(testTopic);

        testComment = new Comment();
        testComment.setAuthor(testUser);
        testComment.setTopic(testTopic);
        testComment.setBody("Comment body");
    }

    @Test
    @DisplayName("Сохранение комментария: id присваивается")
    public void testSaveComment() {
        var saved = commentRepository.save(testComment);
        assertThat(saved.getId()).isNotNull().isGreaterThan(0);
    }

    @Test
    @DisplayName("Поиск комментариев по topicId: возвращает список")
    public void testFindByTopicId() {
        commentRepository.save(testComment);
        List<Comment> comments = commentRepository.findByTopicId(testTopic.getId());
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getBody()).isEqualTo("Comment body");
    }

    @Test
    @DisplayName("Обновление комментария: изменения сохраняются")
    public void testUpdateComment() {
        var saved = commentRepository.save(testComment);
        saved.setBody("Updated body");
        commentRepository.save(saved);

        var updated = commentRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getBody()).isEqualTo("Updated body");
    }

    @Test
    @DisplayName("Удаление комментария: не находится после delete")
    public void testDeleteComment() {
        var saved = commentRepository.save(testComment);
        commentRepository.deleteById(saved.getId());
        assertThat(commentRepository.findById(saved.getId())).isEmpty();
    }
}