package io.hexlet.forum.util;

import org.springframework.stereotype.Component;

import io.hexlet.forum.model.Comment;
import io.hexlet.forum.model.Topic;
import io.hexlet.forum.model.User;
import net.datafaker.Faker;

@Component
public class ModelGenerator {

    private final Faker faker;

    public ModelGenerator(Faker faker) {
        this.faker = faker;
    }

    public User generateUser() {
        var user = new User();
        user.setUsername(faker.name().username());
        user.setEmail(faker.internet().emailAddress());
        return user;
    }

    public Topic generateTopic(User author) {
        var topic = new Topic();
        topic.setAuthor(author);
        topic.setSlug(faker.lorem().word() + "-" + System.currentTimeMillis());
        topic.setTitle(faker.lorem().sentence());
        topic.setBody(faker.lorem().paragraph());
        return topic;
    }

    public Comment generateComment(User author, Topic topic) {
        var comment = new Comment();
        comment.setAuthor(author);
        comment.setTopic(topic);
        comment.setBody(faker.lorem().paragraph());
        return comment;
    }
}