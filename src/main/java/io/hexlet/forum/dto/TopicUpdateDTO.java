package io.hexlet.forum.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TopicUpdateDTO {
    private Long authorId;
    private String slug;
    private String title;
    private String body;
}