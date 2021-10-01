package org.codingspiderfox.service;

import java.util.Optional;
import org.codingspiderfox.service.dto.ProjectMemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.codingspiderfox.domain.ProjectMember}.
 */
public interface ProjectMemberService {
    /**
     * Save a projectMember.
     *
     * @param projectMemberDTO the entity to save.
     * @return the persisted entity.
     */
    ProjectMemberDTO save(ProjectMemberDTO projectMemberDTO);

    /**
     * Partially updates a projectMember.
     *
     * @param projectMemberDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProjectMemberDTO> partialUpdate(ProjectMemberDTO projectMemberDTO);

    /**
     * Get all the projectMembers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberDTO> findAll(Pageable pageable);

    /**
     * Get the "id" projectMember.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProjectMemberDTO> findOne(Long id);

    /**
     * Delete the "id" projectMember.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the projectMember corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectMemberDTO> search(String query, Pageable pageable);
}
