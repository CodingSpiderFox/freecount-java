package org.codingspiderfox.service.mapper;

import java.util.Set;
import org.codingspiderfox.domain.*;
import org.codingspiderfox.service.dto.ProjectMemberPermissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectMemberPermission} and its DTO {@link ProjectMemberPermissionDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProjectMemberPermissionMapper extends EntityMapper<ProjectMemberPermissionDTO, ProjectMemberPermission> {
    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<ProjectMemberPermissionDTO> toDtoIdSet(Set<ProjectMemberPermission> projectMemberPermission);
}
