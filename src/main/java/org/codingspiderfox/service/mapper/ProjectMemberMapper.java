package org.codingspiderfox.service.mapper;

import org.codingspiderfox.domain.*;
import org.codingspiderfox.service.dto.ProjectMemberDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectMember} and its DTO {@link ProjectMemberDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, ProjectMapper.class })
public interface ProjectMemberMapper extends EntityMapper<ProjectMemberDTO, ProjectMember> {
    @Mapping(target = "user", source = "user", qualifiedByName = "login")
    @Mapping(target = "project", source = "project", qualifiedByName = "id")
    ProjectMemberDTO toDto(ProjectMember s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "project", source = "project")
    ProjectMemberDTO toDtoId(ProjectMember projectMember);
}
