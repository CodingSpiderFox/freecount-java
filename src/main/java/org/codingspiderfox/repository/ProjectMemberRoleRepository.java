package org.codingspiderfox.repository;

import java.util.List;
import org.codingspiderfox.domain.ProjectMemberRole;
import org.codingspiderfox.domain.enumeration.ProjectMemberRoleEnum;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProjectMemberRole entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectMemberRoleRepository extends JpaRepository<ProjectMemberRole, Long>, JpaSpecificationExecutor<ProjectMemberRole> {
    List<ProjectMemberRole> findByProjectMemberRole(ProjectMemberRoleEnum projectMemberRoleEnum);
}
