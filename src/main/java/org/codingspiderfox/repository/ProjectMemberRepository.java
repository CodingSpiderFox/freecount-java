package org.codingspiderfox.repository;

import java.util.List;
import org.codingspiderfox.domain.ProjectMember;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProjectMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long>, JpaSpecificationExecutor<ProjectMember> {
    @Query("select projectMember from ProjectMember projectMember where projectMember.user.login = ?#{principal.preferredUsername}")
    List<ProjectMember> findByUserIsCurrentUser();
}
