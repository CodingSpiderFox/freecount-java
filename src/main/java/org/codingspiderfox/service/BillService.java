package org.codingspiderfox.service;

import java.util.Optional;
import org.codingspiderfox.service.dto.BillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link org.codingspiderfox.domain.Bill}.
 */
public interface BillService {
    /**
     * Save a bill.
     *
     * @param billDTO the entity to save.
     * @return the persisted entity.
     */
    BillDTO save(BillDTO billDTO);

    /**
     * Partially updates a bill.
     *
     * @param billDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BillDTO> partialUpdate(BillDTO billDTO);

    /**
     * Get all the bills.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BillDTO> findAll(Pageable pageable);

    /**
     * Get the "id" bill.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BillDTO> findOne(Long id);

    /**
     * Delete the "id" bill.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the bill corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BillDTO> search(String query, Pageable pageable);
}
