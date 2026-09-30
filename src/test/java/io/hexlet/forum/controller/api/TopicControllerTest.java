package io.hexlet.forum.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import io.hexlet.forum.dto.TopicCreateDTO;
import io.hexlet.forum.dto.TopicUpdateDTO;
import io.hexlet.forum.model.Topic;
import io.hexlet.forum.model.User;
import io.hexlet.forum.repository.TopicRepository;
import io.hexlet.forum.repository.UserRepository;
import io.hexlet.forum.util.ModelGenerator;

@SpringBootTest
@AutoConfigureMockMvc
public class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;
    private Topic testTopic;

    @BeforeEach
    public void setUp() {
        testUser = modelGenerator.generateUser();
        userRepository.save(testUser);

        testTopic = modelGenerator.generateTopic(testUser);
    }

    @Test
    public void testIndex() throws Exception {
        topicRepository.save(testTopic);

        var result = mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var dto = new TopicCreateDTO();
        dto.setAuthorId(testUser.getId());
        dto.setSlug("test-slug-" + System.currentTimeMillis());
        dto.setTitle("Test title");
        dto.setBody("Test body");

        var request = post("/api/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var topic = topicRepository.findBySlug(dto.getSlug()).orElse(null);
        assertNotNull(topic);
        assertThat(topic.getTitle()).isEqualTo("Test title");
    }

    @Test
    public void testShow() throws Exception {
        topicRepository.save(testTopic);

        var request = get("/api/topics/" + testTopic.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("slug").isEqualTo(testTopic.getSlug()),
                v -> v.node("title").isEqualTo(testTopic.getTitle()),
                v -> v.node("body").isEqualTo(testTopic.getBody())
        );
    }

    @Test
    public void testUpdate() throws Exception {
        topicRepository.save(testTopic);

        var dto = new TopicUpdateDTO();
        dto.setTitle("new title");

        var request = put("/api/topics/" + testTopic.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var topic = topicRepository.findById(testTopic.getId()).orElseThrow();
        assertThat(topic.getTitle()).isEqualTo("new title");
    }

    @Test
    public void testDestroy() throws Exception {
        topicRepository.save(testTopic);

        var request = delete("/api/topics/" + testTopic.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(topicRepository.existsById(testTopic.getId())).isFalse();
    }
}