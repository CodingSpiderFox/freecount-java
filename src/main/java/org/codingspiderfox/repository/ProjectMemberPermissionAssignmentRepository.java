package org.codingspiderfox.repository;

import java.util.List;
import java.util.Optional;
import org.codingspiderfox.domain.ProjectMemberPermissionAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProjectMemberPermissionAssignment entity.
 */
@Repository
public interface ProjectMemberPermissionAssignmentRepository
    extends JpaRepository<ProjectMemberPermissionAssignment, Long>, JpaSpecificationExecutor<ProjectMemberPermissionAssignment> {
    @Query(
        value = "select distinct projectMemberPermissionAssignment from ProjectMemberPermissionAssignment projectMemberPermissionAssignment left join fetch projectMemberPermissionAssignment.projectMemberPermissions",
        countQuery = "select count(distinct projectMemberPermissionAssignment) from ProjectMemberPermissionAssignment projectMemberPermissionAssignment"
    )
    Page<ProjectMemberPermissionAssignment> findAllWithEagerRelationships(Pageable pageable);

    @Query(
        "select distinct projectMemberPermissionAssignment from ProjectMemberPermissionAssignment projectMemberPermissionAssignment left join fetch projectMemberPermissionAssignment.projectMemberPermissions"
    )
    List<ProjectMemberPermissionAssignment> findAllWithEagerRelationships();

    @Query(
        "select projectMemberPermissionAssignment from ProjectMemberPermissionAssignment projectMemberPermissionAssignment left join fetch projectMemberPermissionAssignment.projectMemberPermissions where projectMemberPermissionAssignment.id =:id"
    )
    Optional<ProjectMemberPermissionAssignment> findOneWithEagerRelationships(@Param("id") Long id);
}
