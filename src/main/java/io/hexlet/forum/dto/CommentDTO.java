package io.hexlet.forum.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentDTO {

    private Long id;
    private Long authorId;
    private Long topicId;
    private String body;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}