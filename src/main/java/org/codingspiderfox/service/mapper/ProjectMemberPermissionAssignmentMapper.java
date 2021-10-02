package org.codingspiderfox.service.mapper;

import org.codingspiderfox.domain.*;
import org.codingspiderfox.service.dto.ProjectMemberPermissionAssignmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectMemberPermissionAssignment} and its DTO {@link ProjectMemberPermissionAssignmentDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProjectMemberMapper.class, ProjectMemberPermissionMapper.class })
public interface ProjectMemberPermissionAssignmentMapper
    extends EntityMapper<ProjectMemberPermissionAssignmentDTO, ProjectMemberPermissionAssignment> {
    @Mapping(target = "projectMember", source = "projectMember", qualifiedByName = "id")
    @Mapping(target = "projectMemberPermissions", source = "projectMemberPermissions", qualifiedByName = "idSet")
    ProjectMemberPermissionAssignmentDTO toDto(ProjectMemberPermissionAssignment s);

    @Mapping(target = "removeProjectMemberPermission", ignore = true)
    ProjectMemberPermissionAssignment toEntity(ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO);
}
