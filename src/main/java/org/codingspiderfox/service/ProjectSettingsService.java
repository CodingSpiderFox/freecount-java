package org.codingspiderfox.service;

import java.util.Optional;
import org.codingspiderfox.service.dto.ProjectSettingsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.codingspiderfox.domain.ProjectSettings}.
 */
public interface ProjectSettingsService {
    /**
     * Save a projectSettings.
     *
     * @param projectSettingsDTO the entity to save.
     * @return the persisted entity.
     */
    ProjectSettingsDTO save(ProjectSettingsDTO projectSettingsDTO);

    /**
     * Partially updates a projectSettings.
     *
     * @param projectSettingsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProjectSettingsDTO> partialUpdate(ProjectSettingsDTO projectSettingsDTO);

    /**
     * Get all the projectSettings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectSettingsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" projectSettings.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProjectSettingsDTO> findOne(Long id);

    /**
     * Delete the "id" projectSettings.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the projectSettings corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectSettingsDTO> search(String query, Pageable pageable);
}
