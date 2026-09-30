package io.hexlet.forum.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import io.hexlet.forum.model.Topic;
import io.hexlet.forum.model.User;

@DataJpaTest
@ActiveProfiles("test")
public class TopicRepositoryTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Topic testTopic;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setUsername("alice");
        testUser.setEmail("alice@example.com");
        userRepository.save(testUser);

        testTopic = new Topic();
        testTopic.setAuthor(testUser);
        testTopic.setSlug("test-slug");
        testTopic.setTitle("Test title");
        testTopic.setBody("Test body");
    }

    @Test
    @DisplayName("Сохранение темы: тема получает id и сохраняется в БД")
    public void testSaveTopic() {
        var saved = topicRepository.save(testTopic);
        assertThat(saved.getId()).isNotNull().isGreaterThan(0);
    }

    @Test
    @DisplayName("Поиск темы по id: возвращает сохранённую тему")
    public void testFindById() {
        var saved = topicRepository.save(testTopic);
        Optional<Topic> found = topicRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test title");
    }

    @Test
    @DisplayName("Поиск темы по slug: findBySlug возвращает тему")
    public void testFindBySlug() {
        topicRepository.save(testTopic);
        Optional<Topic> found = topicRepository.findBySlug("test-slug");
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test title");
    }

    @Test
    @DisplayName("Обновление темы: изменения сохраняются")
    public void testUpdateTopic() {
        var saved = topicRepository.save(testTopic);
        saved.setTitle("Updated title");
        topicRepository.save(saved);

        var updated = topicRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated title");
    }

    @Test
    @DisplayName("Удаление темы: после delete тема не находится")
    public void testDeleteTopic() {
        var saved = topicRepository.save(testTopic);
        topicRepository.deleteById(saved.getId());
        assertThat(topicRepository.findById(saved.getId())).isEmpty();
    }
}