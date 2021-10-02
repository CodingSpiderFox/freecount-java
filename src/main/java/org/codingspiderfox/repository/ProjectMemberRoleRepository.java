package org.codingspiderfox.repository;

import org.codingspiderfox.domain.ProjectMemberRole;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProjectMemberRole entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectMemberRoleRepository extends JpaRepository<ProjectMemberRole, Long>, JpaSpecificationExecutor<ProjectMemberRole> {}
