package org.codingspiderfox.service.mapper;

import java.util.Set;
import org.codingspiderfox.domain.*;
import org.codingspiderfox.service.dto.ProjectMemberRoleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectMemberRole} and its DTO {@link ProjectMemberRoleDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProjectMemberRoleMapper extends EntityMapper<ProjectMemberRoleDTO, ProjectMemberRole> {
    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<ProjectMemberRoleDTO> toDtoIdSet(Set<ProjectMemberRole> projectMemberRole);
}
