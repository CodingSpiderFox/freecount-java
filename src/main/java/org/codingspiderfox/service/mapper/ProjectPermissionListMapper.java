package org.codingspiderfox.service.mapper;

import org.codingspiderfox.domain.*;
import org.codingspiderfox.domain.enumeration.ProjectPermission;
import org.codingspiderfox.service.dto.ProjectDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProjectPermissionListMapper extends EntityMapper<ProjectPermission, List<ProjectPermission>> {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "")
    List<ProjectPermission> toPermissionList(ProjectPermission project);
}
