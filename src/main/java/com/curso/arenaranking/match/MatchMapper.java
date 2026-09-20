package com.curso.arenaranking.match;

import com.curso.arenaranking.match.dto.MatchRequest;
import com.curso.arenaranking.match.dto.MatchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "result", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Match toEntity(MatchRequest request);

    @Mapping(target = "teamId", source = "team.id")
    MatchResponse toResponse(Match match);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "result", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(MatchRequest request, @MappingTarget Match match);
}
