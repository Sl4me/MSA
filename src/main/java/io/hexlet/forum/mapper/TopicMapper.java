package io.hexlet.forum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import io.hexlet.forum.dto.TopicCreateDTO;
import io.hexlet.forum.dto.TopicDTO;
import io.hexlet.forum.dto.TopicUpdateDTO;
import io.hexlet.forum.model.Topic;

@Mapper(
        uses = { ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TopicMapper {

    @Mapping(target = "author", source = "authorId")
    public abstract Topic map(TopicCreateDTO dto);

    @Mapping(source = "author.id", target = "authorId")
    public abstract TopicDTO map(Topic model);

    @Mapping(target = "author", source = "authorId")
    public abstract void update(TopicUpdateDTO dto, @MappingTarget Topic model);
}