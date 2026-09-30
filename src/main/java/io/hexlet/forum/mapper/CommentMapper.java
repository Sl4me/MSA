package io.hexlet.forum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import io.hexlet.forum.dto.CommentCreateDTO;
import io.hexlet.forum.dto.CommentDTO;
import io.hexlet.forum.dto.CommentUpdateDTO;
import io.hexlet.forum.model.Comment;

@Mapper(
        uses = { ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class CommentMapper {

    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "topic", source = "topicId")
    public abstract Comment map(CommentCreateDTO dto);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "topic.id", target = "topicId")
    public abstract CommentDTO map(Comment model);

    public abstract void update(CommentUpdateDTO dto, @MappingTarget Comment model);
}