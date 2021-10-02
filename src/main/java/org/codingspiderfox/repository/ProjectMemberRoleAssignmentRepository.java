package org.codingspiderfox.repository;

import java.util.List;
import java.util.Optional;
import org.codingspiderfox.domain.ProjectMemberRoleAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProjectMemberRoleAssignment entity.
 */
@Repository
public interface ProjectMemberRoleAssignmentRepository
    extends JpaRepository<ProjectMemberRoleAssignment, Long>, JpaSpecificationExecutor<ProjectMemberRoleAssignment> {
    @Query(
        value = "select distinct projectMemberRoleAssignment from ProjectMemberRoleAssignment projectMemberRoleAssignment left join fetch projectMemberRoleAssignment.projectMemberRoles",
        countQuery = "select count(distinct projectMemberRoleAssignment) from ProjectMemberRoleAssignment projectMemberRoleAssignment"
    )
    Page<ProjectMemberRoleAssignment> findAllWithEagerRelationships(Pageable pageable);

    @Query(
        "select distinct projectMemberRoleAssignment from ProjectMemberRoleAssignment projectMemberRoleAssignment left join fetch projectMemberRoleAssignment.projectMemberRoles"
    )
    List<ProjectMemberRoleAssignment> findAllWithEagerRelationships();

    @Query(
        "select projectMemberRoleAssignment from ProjectMemberRoleAssignment projectMemberRoleAssignment left join fetch projectMemberRoleAssignment.projectMemberRoles where projectMemberRoleAssignment.id =:id"
    )
    Optional<ProjectMemberRoleAssignment> findOneWithEagerRelationships(@Param("id") Long id);
}
