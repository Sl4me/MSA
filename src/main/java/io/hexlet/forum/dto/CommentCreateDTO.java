package io.hexlet.forum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentCreateDTO {

    @NotNull
    private Long authorId;

    @NotNull
    private Long topicId;

    @NotNull
    private String body;
}