package org.codingspiderfox.service;

import java.util.Optional;
import org.codingspiderfox.service.dto.ProjectMemberPermissionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.codingspiderfox.domain.ProjectMemberPermission}.
 */
public interface ProjectMemberPermissionService {
    /**
     * Save a projectMemberPermission.
     *
     * @param projectMemberPermissionDTO the entity to save.
     * @return the persisted entity.
     */
    ProjectMemberPermissionDTO save(ProjectMemberPermissionDTO projectMemberPermissionDTO);

    /**
     * Partially updates a projectMemberPermission.
     *
     * @param projectMemberPermissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProjectMemberPermissionDTO> partialUpdate(ProjectMemberPermissionDTO projectMemberPermissionDTO);

    /**
     * Get all the projectMemberPermissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberPermissionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" projectMemberPermission.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProjectMemberPermissionDTO> findOne(Long id);

    /**
     * Delete the "id" projectMemberPermission.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the projectMemberPermission corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberPermissionDTO> search(String query, Pageable pageable);
}
