package org.codingspiderfox.service;

import java.util.Optional;
import org.codingspiderfox.service.dto.BillPositionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.codingspiderfox.domain.BillPosition}.
 */
public interface BillPositionService {
    /**
     * Save a billPosition.
     *
     * @param billPositionDTO the entity to save.
     * @return the persisted entity.
     */
    BillPositionDTO save(BillPositionDTO billPositionDTO);

    /**
     * Partially updates a billPosition.
     *
     * @param billPositionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BillPositionDTO> partialUpdate(BillPositionDTO billPositionDTO);

    /**
     * Get all the billPositions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BillPositionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" billPosition.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BillPositionDTO> findOne(Long id);

    /**
     * Delete the "id" billPosition.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the billPosition corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BillPositionDTO> search(String query, Pageable pageable);
}
