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
import org.codingspiderfox.repository.BillPositionRepository;
import org.codingspiderfox.service.BillPositionService;
import org.codingspiderfox.service.dto.BillPositionDTO;
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
 * REST controller for managing {@link org.codingspiderfox.domain.BillPosition}.
 */
@RestController
@RequestMapping("/api")
public class BillPositionResource {

    private final Logger log = LoggerFactory.getLogger(BillPositionResource.class);

    private static final String ENTITY_NAME = "billPosition";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BillPositionService billPositionService;

    private final BillPositionRepository billPositionRepository;

    public BillPositionResource(BillPositionService billPositionService, BillPositionRepository billPositionRepository) {
        this.billPositionService = billPositionService;
        this.billPositionRepository = billPositionRepository;
    }

    /**
     * {@code POST  /bill-positions} : Create a new billPosition.
     *
     * @param billPositionDTO the billPositionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new billPositionDTO, or with status {@code 400 (Bad Request)} if the billPosition has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bill-positions")
    public ResponseEntity<BillPositionDTO> createBillPosition(@Valid @RequestBody BillPositionDTO billPositionDTO)
        throws URISyntaxException {
        log.debug("REST request to save BillPosition : {}", billPositionDTO);
        if (billPositionDTO.getId() != null) {
            throw new BadRequestAlertException("A new billPosition cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(billPositionDTO.getProduct())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        BillPositionDTO result = billPositionService.save(billPositionDTO);
        return ResponseEntity
            .created(new URI("/api/bill-positions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bill-positions/:id} : Updates an existing billPosition.
     *
     * @param id the id of the billPositionDTO to save.
     * @param billPositionDTO the billPositionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated billPositionDTO,
     * or with status {@code 400 (Bad Request)} if the billPositionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the billPositionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bill-positions/{id}")
    public ResponseEntity<BillPositionDTO> updateBillPosition(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BillPositionDTO billPositionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BillPosition : {}, {}", id, billPositionDTO);
        if (billPositionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, billPositionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!billPositionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BillPositionDTO result = billPositionService.save(billPositionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, billPositionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bill-positions/:id} : Partial updates given fields of an existing billPosition, field will ignore if it is null
     *
     * @param id the id of the billPositionDTO to save.
     * @param billPositionDTO the billPositionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated billPositionDTO,
     * or with status {@code 400 (Bad Request)} if the billPositionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the billPositionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the billPositionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/bill-positions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BillPositionDTO> partialUpdateBillPosition(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BillPositionDTO billPositionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BillPosition partially : {}, {}", id, billPositionDTO);
        if (billPositionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, billPositionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!billPositionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BillPositionDTO> result = billPositionService.partialUpdate(billPositionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, billPositionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /bill-positions} : get all the billPositions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of billPositions in body.
     */
    @GetMapping("/bill-positions")
    public ResponseEntity<List<BillPositionDTO>> getAllBillPositions(Pageable pageable) {
        log.debug("REST request to get a page of BillPositions");
        Page<BillPositionDTO> page = billPositionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bill-positions/:id} : get the "id" billPosition.
     *
     * @param id the id of the billPositionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the billPositionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bill-positions/{id}")
    public ResponseEntity<BillPositionDTO> getBillPosition(@PathVariable Long id) {
        log.debug("REST request to get BillPosition : {}", id);
        Optional<BillPositionDTO> billPositionDTO = billPositionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(billPositionDTO);
    }

    /**
     * {@code DELETE  /bill-positions/:id} : delete the "id" billPosition.
     *
     * @param id the id of the billPositionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bill-positions/{id}")
    public ResponseEntity<Void> deleteBillPosition(@PathVariable Long id) {
        log.debug("REST request to delete BillPosition : {}", id);
        billPositionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/bill-positions?query=:query} : search for the billPosition corresponding
     * to the query.
     *
     * @param query the query of the billPosition search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/bill-positions")
    public ResponseEntity<List<BillPositionDTO>> searchBillPositions(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of BillPositions for query {}", query);
        Page<BillPositionDTO> page = billPositionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
