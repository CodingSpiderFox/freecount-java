package org.codingspiderfox.service;

import java.util.Optional;
import org.codingspiderfox.service.dto.ProjectMemberPermissionAssignmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.codingspiderfox.domain.ProjectMemberPermissionAssignment}.
 */
public interface ProjectMemberPermissionAssignmentService {
    /**
     * Save a projectMemberPermissionAssignment.
     *
     * @param projectMemberPermissionAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    ProjectMemberPermissionAssignmentDTO save(ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO);

    /**
     * Partially updates a projectMemberPermissionAssignment.
     *
     * @param projectMemberPermissionAssignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProjectMemberPermissionAssignmentDTO> partialUpdate(ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO);

    /**
     * Get all the projectMemberPermissionAssignments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberPermissionAssignmentDTO> findAll(Pageable pageable);

    /**
     * Get all the projectMemberPermissionAssignments with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberPermissionAssignmentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" projectMemberPermissionAssignment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProjectMemberPermissionAssignmentDTO> findOne(Long id);

    /**
     * Delete the "id" projectMemberPermissionAssignment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the projectMemberPermissionAssignment corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberPermissionAssignmentDTO> search(String query, Pageable pageable);
}
