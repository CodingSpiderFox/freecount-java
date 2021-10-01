package org.codingspiderfox.service.mapper;

import org.codingspiderfox.domain.*;
import org.codingspiderfox.service.dto.ProjectSettingsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectSettings} and its DTO {@link ProjectSettingsDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProjectMapper.class })
public interface ProjectSettingsMapper extends EntityMapper<ProjectSettingsDTO, ProjectSettings> {
    @Mapping(target = "project", source = "project", qualifiedByName = "id")
    ProjectSettingsDTO toDto(ProjectSettings s);
}
