package org.codingspiderfox.service;

import java.util.Optional;
import org.codingspiderfox.service.dto.ProjectMemberRoleAssignmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.codingspiderfox.domain.ProjectMemberRoleAssignment}.
 */
public interface ProjectMemberRoleAssignmentService {
    /**
     * Save a projectMemberRoleAssignment.
     *
     * @param projectMemberRoleAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    ProjectMemberRoleAssignmentDTO save(ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO);

    /**
     * Partially updates a projectMemberRoleAssignment.
     *
     * @param projectMemberRoleAssignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProjectMemberRoleAssignmentDTO> partialUpdate(ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO);

    /**
     * Get all the projectMemberRoleAssignments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberRoleAssignmentDTO> findAll(Pageable pageable);

    /**
     * Get all the projectMemberRoleAssignments with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberRoleAssignmentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" projectMemberRoleAssignment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProjectMemberRoleAssignmentDTO> findOne(Long id);

    /**
     * Delete the "id" projectMemberRoleAssignment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the projectMemberRoleAssignment corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberRoleAssignmentDTO> search(String query, Pageable pageable);
}
