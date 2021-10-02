package org.codingspiderfox.repository;

import org.codingspiderfox.domain.ProjectMemberPermission;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProjectMemberPermission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectMemberPermissionRepository
    extends JpaRepository<ProjectMemberPermission, Long>, JpaSpecificationExecutor<ProjectMemberPermission> {}
