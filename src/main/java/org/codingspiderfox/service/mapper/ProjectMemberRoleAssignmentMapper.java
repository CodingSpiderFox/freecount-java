package org.codingspiderfox.service.mapper;

import org.codingspiderfox.domain.*;
import org.codingspiderfox.service.dto.ProjectMemberRoleAssignmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectMemberRoleAssignment} and its DTO {@link ProjectMemberRoleAssignmentDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProjectMemberMapper.class, ProjectMemberRoleMapper.class })
public interface ProjectMemberRoleAssignmentMapper extends EntityMapper<ProjectMemberRoleAssignmentDTO, ProjectMemberRoleAssignment> {
    @Mapping(target = "projectMember", source = "projectMember", qualifiedByName = "id")
    @Mapping(target = "projectMemberRoles", source = "projectMemberRoles", qualifiedByName = "idSet")
    ProjectMemberRoleAssignmentDTO toDto(ProjectMemberRoleAssignment s);

    @Mapping(target = "removeProjectMemberRole", ignore = true)
    ProjectMemberRoleAssignment toEntity(ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO);
}
