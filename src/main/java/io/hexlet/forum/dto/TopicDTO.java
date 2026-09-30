package io.hexlet.forum.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TopicDTO {

    private Long id;
    private Long authorId;
    private String slug;
    private String title;
    private String body;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}