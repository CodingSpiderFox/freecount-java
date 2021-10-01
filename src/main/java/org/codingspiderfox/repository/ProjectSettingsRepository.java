package org.codingspiderfox.repository;

import org.codingspiderfox.domain.ProjectSettings;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProjectSettings entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectSettingsRepository extends JpaRepository<ProjectSettings, Long>, JpaSpecificationExecutor<ProjectSettings> {}
