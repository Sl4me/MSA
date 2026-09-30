package io.hexlet.forum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TopicCreateDTO {

    @NotNull
    private Long authorId;

    @NotNull
    private String slug;

    @NotNull
    private String title;

    @NotNull
    private String body;
}