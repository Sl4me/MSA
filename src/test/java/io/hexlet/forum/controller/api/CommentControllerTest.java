package io.hexlet.forum.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
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

import io.hexlet.forum.dto.CommentCreateDTO;
import io.hexlet.forum.dto.CommentUpdateDTO;
import io.hexlet.forum.model.Comment;
import io.hexlet.forum.model.Topic;
import io.hexlet.forum.model.User;
import io.hexlet.forum.repository.CommentRepository;
import io.hexlet.forum.repository.TopicRepository;
import io.hexlet.forum.repository.UserRepository;
import io.hexlet.forum.util.ModelGenerator;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;
    private Topic testTopic;
    private Comment testComment;

    @BeforeEach
    public void setUp() {
        testUser = modelGenerator.generateUser();
        userRepository.save(testUser);

        testTopic = modelGenerator.generateTopic(testUser);
        topicRepository.save(testTopic);

        testComment = modelGenerator.generateComment(testUser, testTopic);
    }

    @Test
    public void testIndexByTopic() throws Exception {
        commentRepository.save(testComment);

        var result = mockMvc.perform(get("/api/topics/" + testTopic.getId() + "/comments"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var dto = new CommentCreateDTO();
        dto.setAuthorId(testUser.getId());
        dto.setTopicId(testTopic.getId());
        dto.setBody("Test comment body");

        var request = post("/api/topics/" + testTopic.getId() + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var comments = commentRepository.findByTopicId(testTopic.getId());
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getBody()).isEqualTo("Test comment body");
    }

    @Test
    public void testShow() throws Exception {
        commentRepository.save(testComment);

        var request = get("/api/comments/" + testComment.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("body").isEqualTo(testComment.getBody()),
                v -> v.node("authorId").isEqualTo(testUser.getId()),
                v -> v.node("topicId").isEqualTo(testTopic.getId())
        );
    }

    @Test
    public void testUpdate() throws Exception {
        commentRepository.save(testComment);

        var dto = new CommentUpdateDTO();
        dto.setBody("updated body");

        var request = put("/api/comments/" + testComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var comment = commentRepository.findById(testComment.getId()).orElseThrow();
        assertThat(comment.getBody()).isEqualTo("updated body");
    }

    @Test
    public void testDestroy() throws Exception {
        commentRepository.save(testComment);

        var request = delete("/api/comments/" + testComment.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(commentRepository.existsById(testComment.getId())).isFalse();
    }
}