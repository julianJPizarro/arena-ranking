package com.curso.arenaranking.team;

import com.curso.arenaranking.team.dto.TeamRequest;
import com.curso.arenaranking.team.dto.TeamResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Team toEntity(TeamRequest request);

    TeamResponse toResponse(Team team);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(TeamRequest request, @MappingTarget Team team);
}
