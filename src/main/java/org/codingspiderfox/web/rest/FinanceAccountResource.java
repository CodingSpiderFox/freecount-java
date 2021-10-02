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
import org.codingspiderfox.repository.FinanceAccountRepository;
import org.codingspiderfox.service.FinanceAccountQueryService;
import org.codingspiderfox.service.FinanceAccountService;
import org.codingspiderfox.service.criteria.FinanceAccountCriteria;
import org.codingspiderfox.service.dto.FinanceAccountDTO;
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
 * REST controller for managing {@link org.codingspiderfox.domain.FinanceAccount}.
 */
@RestController
@RequestMapping("/api")
public class FinanceAccountResource {

    private final Logger log = LoggerFactory.getLogger(FinanceAccountResource.class);

    private static final String ENTITY_NAME = "financeAccount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FinanceAccountService financeAccountService;

    private final FinanceAccountRepository financeAccountRepository;

    private final FinanceAccountQueryService financeAccountQueryService;

    public FinanceAccountResource(
        FinanceAccountService financeAccountService,
        FinanceAccountRepository financeAccountRepository,
        FinanceAccountQueryService financeAccountQueryService
    ) {
        this.financeAccountService = financeAccountService;
        this.financeAccountRepository = financeAccountRepository;
        this.financeAccountQueryService = financeAccountQueryService;
    }

    /**
     * {@code POST  /finance-accounts} : Create a new financeAccount.
     *
     * @param financeAccountDTO the financeAccountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new financeAccountDTO, or with status {@code 400 (Bad Request)} if the financeAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/finance-accounts")
    public ResponseEntity<FinanceAccountDTO> createFinanceAccount(@Valid @RequestBody FinanceAccountDTO financeAccountDTO)
        throws URISyntaxException {
        log.debug("REST request to save FinanceAccount : {}", financeAccountDTO);
        if (financeAccountDTO.getId() != null) {
            throw new BadRequestAlertException("A new financeAccount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(financeAccountDTO.getOwner())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        FinanceAccountDTO result = financeAccountService.save(financeAccountDTO);
        return ResponseEntity
            .created(new URI("/api/finance-accounts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /finance-accounts/:id} : Updates an existing financeAccount.
     *
     * @param id the id of the financeAccountDTO to save.
     * @param financeAccountDTO the financeAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated financeAccountDTO,
     * or with status {@code 400 (Bad Request)} if the financeAccountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the financeAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/finance-accounts/{id}")
    public ResponseEntity<FinanceAccountDTO> updateFinanceAccount(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody FinanceAccountDTO financeAccountDTO
    ) throws URISyntaxException {
        log.debug("REST request to update FinanceAccount : {}, {}", id, financeAccountDTO);
        if (financeAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, financeAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!financeAccountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FinanceAccountDTO result = financeAccountService.save(financeAccountDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, financeAccountDTO.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /finance-accounts/:id} : Partial updates given fields of an existing financeAccount, field will ignore if it is null
     *
     * @param id the id of the financeAccountDTO to save.
     * @param financeAccountDTO the financeAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated financeAccountDTO,
     * or with status {@code 400 (Bad Request)} if the financeAccountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the financeAccountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the financeAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/finance-accounts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FinanceAccountDTO> partialUpdateFinanceAccount(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody FinanceAccountDTO financeAccountDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update FinanceAccount partially : {}, {}", id, financeAccountDTO);
        if (financeAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, financeAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!financeAccountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FinanceAccountDTO> result = financeAccountService.partialUpdate(financeAccountDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, financeAccountDTO.getId())
        );
    }

    /**
     * {@code GET  /finance-accounts} : get all the financeAccounts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of financeAccounts in body.
     */
    @GetMapping("/finance-accounts")
    public ResponseEntity<List<FinanceAccountDTO>> getAllFinanceAccounts(FinanceAccountCriteria criteria, Pageable pageable) {
        log.debug("REST request to get FinanceAccounts by criteria: {}", criteria);
        Page<FinanceAccountDTO> page = financeAccountQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /finance-accounts/count} : count all the financeAccounts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/finance-accounts/count")
    public ResponseEntity<Long> countFinanceAccounts(FinanceAccountCriteria criteria) {
        log.debug("REST request to count FinanceAccounts by criteria: {}", criteria);
        return ResponseEntity.ok().body(financeAccountQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /finance-accounts/:id} : get the "id" financeAccount.
     *
     * @param id the id of the financeAccountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the financeAccountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/finance-accounts/{id}")
    public ResponseEntity<FinanceAccountDTO> getFinanceAccount(@PathVariable String id) {
        log.debug("REST request to get FinanceAccount : {}", id);
        Optional<FinanceAccountDTO> financeAccountDTO = financeAccountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(financeAccountDTO);
    }

    /**
     * {@code DELETE  /finance-accounts/:id} : delete the "id" financeAccount.
     *
     * @param id the id of the financeAccountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/finance-accounts/{id}")
    public ResponseEntity<Void> deleteFinanceAccount(@PathVariable String id) {
        log.debug("REST request to delete FinanceAccount : {}", id);
        financeAccountService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/finance-accounts?query=:query} : search for the financeAccount corresponding
     * to the query.
     *
     * @param query the query of the financeAccount search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/finance-accounts")
    public ResponseEntity<List<FinanceAccountDTO>> searchFinanceAccounts(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of FinanceAccounts for query {}", query);
        Page<FinanceAccountDTO> page = financeAccountService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
