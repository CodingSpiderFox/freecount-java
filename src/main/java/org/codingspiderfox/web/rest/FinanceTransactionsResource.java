package org.codingspiderfox.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.codingspiderfox.repository.FinanceTransactionsRepository;
import org.codingspiderfox.service.FinanceTransactionsQueryService;
import org.codingspiderfox.service.FinanceTransactionsService;
import org.codingspiderfox.service.criteria.FinanceTransactionsCriteria;
import org.codingspiderfox.service.dto.FinanceTransactionsDTO;
import org.codingspiderfox.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.codingspiderfox.domain.FinanceTransactions}.
 */
@RestController
@RequestMapping("/api")
public class FinanceTransactionsResource {

    private final Logger log = LoggerFactory.getLogger(FinanceTransactionsResource.class);

    private static final String ENTITY_NAME = "financeTransactions";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FinanceTransactionsService financeTransactionsService;

    private final FinanceTransactionsRepository financeTransactionsRepository;

    private final FinanceTransactionsQueryService financeTransactionsQueryService;

    public FinanceTransactionsResource(
        FinanceTransactionsService financeTransactionsService,
        FinanceTransactionsRepository financeTransactionsRepository,
        FinanceTransactionsQueryService financeTransactionsQueryService
    ) {
        this.financeTransactionsService = financeTransactionsService;
        this.financeTransactionsRepository = financeTransactionsRepository;
        this.financeTransactionsQueryService = financeTransactionsQueryService;
    }

    /**
     * {@code POST  /finance-transactions} : Create a new financeTransactions.
     *
     * @param financeTransactionsDTO the financeTransactionsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new financeTransactionsDTO, or with status {@code 400 (Bad Request)} if the financeTransactions has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/finance-transactions")
    public ResponseEntity<FinanceTransactionsDTO> createFinanceTransactions(
        @Valid @RequestBody FinanceTransactionsDTO financeTransactionsDTO
    ) throws URISyntaxException {
        log.debug("REST request to save FinanceTransactions : {}", financeTransactionsDTO);
        if (financeTransactionsDTO.getId() != null) {
            throw new BadRequestAlertException("A new financeTransactions cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(financeTransactionsDTO.getReferenceAccount())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        FinanceTransactionsDTO result = financeTransactionsService.save(financeTransactionsDTO);
        return ResponseEntity
            .created(new URI("/api/finance-transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /finance-transactions/:id} : Updates an existing financeTransactions.
     *
     * @param id the id of the financeTransactionsDTO to save.
     * @param financeTransactionsDTO the financeTransactionsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated financeTransactionsDTO,
     * or with status {@code 400 (Bad Request)} if the financeTransactionsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the financeTransactionsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/finance-transactions/{id}")
    public ResponseEntity<FinanceTransactionsDTO> updateFinanceTransactions(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody FinanceTransactionsDTO financeTransactionsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update FinanceTransactions : {}, {}", id, financeTransactionsDTO);
        if (financeTransactionsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, financeTransactionsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!financeTransactionsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FinanceTransactionsDTO result = financeTransactionsService.save(financeTransactionsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, financeTransactionsDTO.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /finance-transactions/:id} : Partial updates given fields of an existing financeTransactions, field will ignore if it is null
     *
     * @param id the id of the financeTransactionsDTO to save.
     * @param financeTransactionsDTO the financeTransactionsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated financeTransactionsDTO,
     * or with status {@code 400 (Bad Request)} if the financeTransactionsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the financeTransactionsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the financeTransactionsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/finance-transactions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FinanceTransactionsDTO> partialUpdateFinanceTransactions(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody FinanceTransactionsDTO financeTransactionsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update FinanceTransactions partially : {}, {}", id, financeTransactionsDTO);
        if (financeTransactionsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, financeTransactionsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!financeTransactionsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FinanceTransactionsDTO> result = financeTransactionsService.partialUpdate(financeTransactionsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, financeTransactionsDTO.getId())
        );
    }

    /**
     * {@code GET  /finance-transactions} : get all the financeTransactions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of financeTransactions in body.
     */
    @GetMapping("/finance-transactions")
    public ResponseEntity<List<FinanceTransactionsDTO>> getAllFinanceTransactions(FinanceTransactionsCriteria criteria, Pageable pageable) {
        log.debug("REST request to get FinanceTransactions by criteria: {}", criteria);
        Page<FinanceTransactionsDTO> page = financeTransactionsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /finance-transactions/count} : count all the financeTransactions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/finance-transactions/count")
    public ResponseEntity<Long> countFinanceTransactions(FinanceTransactionsCriteria criteria) {
        log.debug("REST request to count FinanceTransactions by criteria: {}", criteria);
        return ResponseEntity.ok().body(financeTransactionsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /finance-transactions/:id} : get the "id" financeTransactions.
     *
     * @param id the id of the financeTransactionsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the financeTransactionsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/finance-transactions/{id}")
    public ResponseEntity<FinanceTransactionsDTO> getFinanceTransactions(@PathVariable String id) {
        log.debug("REST request to get FinanceTransactions : {}", id);
        Optional<FinanceTransactionsDTO> financeTransactionsDTO = financeTransactionsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(financeTransactionsDTO);
    }

    /**
     * {@code DELETE  /finance-transactions/:id} : delete the "id" financeTransactions.
     *
     * @param id the id of the financeTransactionsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/finance-transactions/{id}")
    public ResponseEntity<Void> deleteFinanceTransactions(@PathVariable String id) {
        log.debug("REST request to delete FinanceTransactions : {}", id);
        financeTransactionsService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/finance-transactions?query=:query} : search for the financeTransactions corresponding
     * to the query.
     *
     * @param query the query of the financeTransactions search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/finance-transactions")
    public ResponseEntity<List<FinanceTransactionsDTO>> searchFinanceTransactions(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of FinanceTransactions for query {}", query);
        Page<FinanceTransactionsDTO> page = financeTransactionsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
