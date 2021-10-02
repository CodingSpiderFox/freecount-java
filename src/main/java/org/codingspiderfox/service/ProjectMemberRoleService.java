package org.codingspiderfox.service;

import java.util.Optional;
import org.codingspiderfox.service.dto.ProjectMemberRoleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.codingspiderfox.domain.ProjectMemberRole}.
 */
public interface ProjectMemberRoleService {
    /**
     * Save a projectMemberRole.
     *
     * @param projectMemberRoleDTO the entity to save.
     * @return the persisted entity.
     */
    ProjectMemberRoleDTO save(ProjectMemberRoleDTO projectMemberRoleDTO);

    /**
     * Partially updates a projectMemberRole.
     *
     * @param projectMemberRoleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProjectMemberRoleDTO> partialUpdate(ProjectMemberRoleDTO projectMemberRoleDTO);

    /**
     * Get all the projectMemberRoles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberRoleDTO> findAll(Pageable pageable);

    /**
     * Get the "id" projectMemberRole.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProjectMemberRoleDTO> findOne(Long id);

    /**
     * Delete the "id" projectMemberRole.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the projectMemberRole corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberRoleDTO> search(String query, Pageable pageable);
}
