package org.codingspiderfox.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.codingspiderfox.IntegrationTest;
import org.codingspiderfox.domain.Product;
import org.codingspiderfox.repository.ProductRepository;
import org.codingspiderfox.repository.search.ProductSearchRepository;
import org.codingspiderfox.service.criteria.ProductCriteria;
import org.codingspiderfox.service.dto.ProductDTO;
import org.codingspiderfox.service.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SCANNER_ID = "AAAAAAAAAA";
    private static final String UPDATED_SCANNER_ID = "BBBBBBBBBB";

    private static final Duration DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE = Duration.ofHours(6);
    private static final Duration UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE = Duration.ofHours(12);
    private static final Duration SMALLER_USUAL_DURATION_FROM_BUY_TILL_EXPIRE = Duration.ofHours(5);

    private static final Boolean DEFAULT_EXPIRE_MEANS_BAD = false;
    private static final Boolean UPDATED_EXPIRE_MEANS_BAD = true;

    private static final Double DEFAULT_DEFAULT_PRICE = 1D;
    private static final Double UPDATED_DEFAULT_PRICE = 2D;
    private static final Double SMALLER_DEFAULT_PRICE = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/products";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    /**
     * This repository is mocked in the org.codingspiderfox.repository.search test package.
     *
     * @see org.codingspiderfox.repository.search.ProductSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProductSearchRepository mockProductSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .title(DEFAULT_TITLE)
            .scannerId(DEFAULT_SCANNER_ID)
            .usualDurationFromBuyTillExpire(DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE)
            .expireMeansBad(DEFAULT_EXPIRE_MEANS_BAD)
            .defaultPrice(DEFAULT_DEFAULT_PRICE);
        return product;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .title(UPDATED_TITLE)
            .scannerId(UPDATED_SCANNER_ID)
            .usualDurationFromBuyTillExpire(UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE)
            .expireMeansBad(UPDATED_EXPIRE_MEANS_BAD)
            .defaultPrice(UPDATED_DEFAULT_PRICE);
        return product;
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();
        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        restProductMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProduct.getScannerId()).isEqualTo(DEFAULT_SCANNER_ID);
        assertThat(testProduct.getUsualDurationFromBuyTillExpire()).isEqualTo(DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);
        assertThat(testProduct.getExpireMeansBad()).isEqualTo(DEFAULT_EXPIRE_MEANS_BAD);
        assertThat(testProduct.getDefaultPrice()).isEqualTo(DEFAULT_DEFAULT_PRICE);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(1)).save(testProduct);
    }

    @Test
    @Transactional
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        product.setId(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setTitle(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkScannerIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setScannerId(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUsualDurationFromBuyTillExpireIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setUsualDurationFromBuyTillExpire(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDefaultPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setDefaultPrice(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].scannerId").value(hasItem(DEFAULT_SCANNER_ID)))
            .andExpect(
                jsonPath("$.[*].usualDurationFromBuyTillExpire").value(hasItem(DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE.toString()))
            )
            .andExpect(jsonPath("$.[*].expireMeansBad").value(hasItem(DEFAULT_EXPIRE_MEANS_BAD.booleanValue())))
            .andExpect(jsonPath("$.[*].defaultPrice").value(hasItem(DEFAULT_DEFAULT_PRICE.doubleValue())));
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.scannerId").value(DEFAULT_SCANNER_ID))
            .andExpect(jsonPath("$.usualDurationFromBuyTillExpire").value(DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE.toString()))
            .andExpect(jsonPath("$.expireMeansBad").value(DEFAULT_EXPIRE_MEANS_BAD.booleanValue()))
            .andExpect(jsonPath("$.defaultPrice").value(DEFAULT_DEFAULT_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        Long id = product.getId();

        defaultProductShouldBeFound("id.equals=" + id);
        defaultProductShouldNotBeFound("id.notEquals=" + id);

        defaultProductShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.greaterThan=" + id);

        defaultProductShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title equals to DEFAULT_TITLE
        defaultProductShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the productList where title equals to UPDATED_TITLE
        defaultProductShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title not equals to DEFAULT_TITLE
        defaultProductShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the productList where title not equals to UPDATED_TITLE
        defaultProductShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultProductShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the productList where title equals to UPDATED_TITLE
        defaultProductShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title is not null
        defaultProductShouldBeFound("title.specified=true");

        // Get all the productList where title is null
        defaultProductShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByTitleContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title contains DEFAULT_TITLE
        defaultProductShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the productList where title contains UPDATED_TITLE
        defaultProductShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where title does not contain DEFAULT_TITLE
        defaultProductShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the productList where title does not contain UPDATED_TITLE
        defaultProductShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllProductsByScannerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where scannerId equals to DEFAULT_SCANNER_ID
        defaultProductShouldBeFound("scannerId.equals=" + DEFAULT_SCANNER_ID);

        // Get all the productList where scannerId equals to UPDATED_SCANNER_ID
        defaultProductShouldNotBeFound("scannerId.equals=" + UPDATED_SCANNER_ID);
    }

    @Test
    @Transactional
    void getAllProductsByScannerIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where scannerId not equals to DEFAULT_SCANNER_ID
        defaultProductShouldNotBeFound("scannerId.notEquals=" + DEFAULT_SCANNER_ID);

        // Get all the productList where scannerId not equals to UPDATED_SCANNER_ID
        defaultProductShouldBeFound("scannerId.notEquals=" + UPDATED_SCANNER_ID);
    }

    @Test
    @Transactional
    void getAllProductsByScannerIdIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where scannerId in DEFAULT_SCANNER_ID or UPDATED_SCANNER_ID
        defaultProductShouldBeFound("scannerId.in=" + DEFAULT_SCANNER_ID + "," + UPDATED_SCANNER_ID);

        // Get all the productList where scannerId equals to UPDATED_SCANNER_ID
        defaultProductShouldNotBeFound("scannerId.in=" + UPDATED_SCANNER_ID);
    }

    @Test
    @Transactional
    void getAllProductsByScannerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where scannerId is not null
        defaultProductShouldBeFound("scannerId.specified=true");

        // Get all the productList where scannerId is null
        defaultProductShouldNotBeFound("scannerId.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByScannerIdContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where scannerId contains DEFAULT_SCANNER_ID
        defaultProductShouldBeFound("scannerId.contains=" + DEFAULT_SCANNER_ID);

        // Get all the productList where scannerId contains UPDATED_SCANNER_ID
        defaultProductShouldNotBeFound("scannerId.contains=" + UPDATED_SCANNER_ID);
    }

    @Test
    @Transactional
    void getAllProductsByScannerIdNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where scannerId does not contain DEFAULT_SCANNER_ID
        defaultProductShouldNotBeFound("scannerId.doesNotContain=" + DEFAULT_SCANNER_ID);

        // Get all the productList where scannerId does not contain UPDATED_SCANNER_ID
        defaultProductShouldBeFound("scannerId.doesNotContain=" + UPDATED_SCANNER_ID);
    }

    @Test
    @Transactional
    void getAllProductsByUsualDurationFromBuyTillExpireIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where usualDurationFromBuyTillExpire equals to DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldBeFound("usualDurationFromBuyTillExpire.equals=" + DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);

        // Get all the productList where usualDurationFromBuyTillExpire equals to UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldNotBeFound("usualDurationFromBuyTillExpire.equals=" + UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);
    }

    @Test
    @Transactional
    void getAllProductsByUsualDurationFromBuyTillExpireIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where usualDurationFromBuyTillExpire not equals to DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldNotBeFound("usualDurationFromBuyTillExpire.notEquals=" + DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);

        // Get all the productList where usualDurationFromBuyTillExpire not equals to UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldBeFound("usualDurationFromBuyTillExpire.notEquals=" + UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);
    }

    @Test
    @Transactional
    void getAllProductsByUsualDurationFromBuyTillExpireIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where usualDurationFromBuyTillExpire in DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE or UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldBeFound(
            "usualDurationFromBuyTillExpire.in=" +
            DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE +
            "," +
            UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        );

        // Get all the productList where usualDurationFromBuyTillExpire equals to UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldNotBeFound("usualDurationFromBuyTillExpire.in=" + UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);
    }

    @Test
    @Transactional
    void getAllProductsByUsualDurationFromBuyTillExpireIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where usualDurationFromBuyTillExpire is not null
        defaultProductShouldBeFound("usualDurationFromBuyTillExpire.specified=true");

        // Get all the productList where usualDurationFromBuyTillExpire is null
        defaultProductShouldNotBeFound("usualDurationFromBuyTillExpire.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByUsualDurationFromBuyTillExpireIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where usualDurationFromBuyTillExpire is greater than or equal to DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldBeFound("usualDurationFromBuyTillExpire.greaterThanOrEqual=" + DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);

        // Get all the productList where usualDurationFromBuyTillExpire is greater than or equal to UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldNotBeFound("usualDurationFromBuyTillExpire.greaterThanOrEqual=" + UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);
    }

    @Test
    @Transactional
    void getAllProductsByUsualDurationFromBuyTillExpireIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where usualDurationFromBuyTillExpire is less than or equal to DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldBeFound("usualDurationFromBuyTillExpire.lessThanOrEqual=" + DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);

        // Get all the productList where usualDurationFromBuyTillExpire is less than or equal to SMALLER_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldNotBeFound("usualDurationFromBuyTillExpire.lessThanOrEqual=" + SMALLER_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);
    }

    @Test
    @Transactional
    void getAllProductsByUsualDurationFromBuyTillExpireIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where usualDurationFromBuyTillExpire is less than DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldNotBeFound("usualDurationFromBuyTillExpire.lessThan=" + DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);

        // Get all the productList where usualDurationFromBuyTillExpire is less than UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldBeFound("usualDurationFromBuyTillExpire.lessThan=" + UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);
    }

    @Test
    @Transactional
    void getAllProductsByUsualDurationFromBuyTillExpireIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where usualDurationFromBuyTillExpire is greater than DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldNotBeFound("usualDurationFromBuyTillExpire.greaterThan=" + DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);

        // Get all the productList where usualDurationFromBuyTillExpire is greater than SMALLER_USUAL_DURATION_FROM_BUY_TILL_EXPIRE
        defaultProductShouldBeFound("usualDurationFromBuyTillExpire.greaterThan=" + SMALLER_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);
    }

    @Test
    @Transactional
    void getAllProductsByExpireMeansBadIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where expireMeansBad equals to DEFAULT_EXPIRE_MEANS_BAD
        defaultProductShouldBeFound("expireMeansBad.equals=" + DEFAULT_EXPIRE_MEANS_BAD);

        // Get all the productList where expireMeansBad equals to UPDATED_EXPIRE_MEANS_BAD
        defaultProductShouldNotBeFound("expireMeansBad.equals=" + UPDATED_EXPIRE_MEANS_BAD);
    }

    @Test
    @Transactional
    void getAllProductsByExpireMeansBadIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where expireMeansBad not equals to DEFAULT_EXPIRE_MEANS_BAD
        defaultProductShouldNotBeFound("expireMeansBad.notEquals=" + DEFAULT_EXPIRE_MEANS_BAD);

        // Get all the productList where expireMeansBad not equals to UPDATED_EXPIRE_MEANS_BAD
        defaultProductShouldBeFound("expireMeansBad.notEquals=" + UPDATED_EXPIRE_MEANS_BAD);
    }

    @Test
    @Transactional
    void getAllProductsByExpireMeansBadIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where expireMeansBad in DEFAULT_EXPIRE_MEANS_BAD or UPDATED_EXPIRE_MEANS_BAD
        defaultProductShouldBeFound("expireMeansBad.in=" + DEFAULT_EXPIRE_MEANS_BAD + "," + UPDATED_EXPIRE_MEANS_BAD);

        // Get all the productList where expireMeansBad equals to UPDATED_EXPIRE_MEANS_BAD
        defaultProductShouldNotBeFound("expireMeansBad.in=" + UPDATED_EXPIRE_MEANS_BAD);
    }

    @Test
    @Transactional
    void getAllProductsByExpireMeansBadIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where expireMeansBad is not null
        defaultProductShouldBeFound("expireMeansBad.specified=true");

        // Get all the productList where expireMeansBad is null
        defaultProductShouldNotBeFound("expireMeansBad.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDefaultPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where defaultPrice equals to DEFAULT_DEFAULT_PRICE
        defaultProductShouldBeFound("defaultPrice.equals=" + DEFAULT_DEFAULT_PRICE);

        // Get all the productList where defaultPrice equals to UPDATED_DEFAULT_PRICE
        defaultProductShouldNotBeFound("defaultPrice.equals=" + UPDATED_DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByDefaultPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where defaultPrice not equals to DEFAULT_DEFAULT_PRICE
        defaultProductShouldNotBeFound("defaultPrice.notEquals=" + DEFAULT_DEFAULT_PRICE);

        // Get all the productList where defaultPrice not equals to UPDATED_DEFAULT_PRICE
        defaultProductShouldBeFound("defaultPrice.notEquals=" + UPDATED_DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByDefaultPriceIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where defaultPrice in DEFAULT_DEFAULT_PRICE or UPDATED_DEFAULT_PRICE
        defaultProductShouldBeFound("defaultPrice.in=" + DEFAULT_DEFAULT_PRICE + "," + UPDATED_DEFAULT_PRICE);

        // Get all the productList where defaultPrice equals to UPDATED_DEFAULT_PRICE
        defaultProductShouldNotBeFound("defaultPrice.in=" + UPDATED_DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByDefaultPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where defaultPrice is not null
        defaultProductShouldBeFound("defaultPrice.specified=true");

        // Get all the productList where defaultPrice is null
        defaultProductShouldNotBeFound("defaultPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDefaultPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where defaultPrice is greater than or equal to DEFAULT_DEFAULT_PRICE
        defaultProductShouldBeFound("defaultPrice.greaterThanOrEqual=" + DEFAULT_DEFAULT_PRICE);

        // Get all the productList where defaultPrice is greater than or equal to UPDATED_DEFAULT_PRICE
        defaultProductShouldNotBeFound("defaultPrice.greaterThanOrEqual=" + UPDATED_DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByDefaultPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where defaultPrice is less than or equal to DEFAULT_DEFAULT_PRICE
        defaultProductShouldBeFound("defaultPrice.lessThanOrEqual=" + DEFAULT_DEFAULT_PRICE);

        // Get all the productList where defaultPrice is less than or equal to SMALLER_DEFAULT_PRICE
        defaultProductShouldNotBeFound("defaultPrice.lessThanOrEqual=" + SMALLER_DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByDefaultPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where defaultPrice is less than DEFAULT_DEFAULT_PRICE
        defaultProductShouldNotBeFound("defaultPrice.lessThan=" + DEFAULT_DEFAULT_PRICE);

        // Get all the productList where defaultPrice is less than UPDATED_DEFAULT_PRICE
        defaultProductShouldBeFound("defaultPrice.lessThan=" + UPDATED_DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByDefaultPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where defaultPrice is greater than DEFAULT_DEFAULT_PRICE
        defaultProductShouldNotBeFound("defaultPrice.greaterThan=" + DEFAULT_DEFAULT_PRICE);

        // Get all the productList where defaultPrice is greater than SMALLER_DEFAULT_PRICE
        defaultProductShouldBeFound("defaultPrice.greaterThan=" + SMALLER_DEFAULT_PRICE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].scannerId").value(hasItem(DEFAULT_SCANNER_ID)))
            .andExpect(
                jsonPath("$.[*].usualDurationFromBuyTillExpire").value(hasItem(DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE.toString()))
            )
            .andExpect(jsonPath("$.[*].expireMeansBad").value(hasItem(DEFAULT_EXPIRE_MEANS_BAD.booleanValue())))
            .andExpect(jsonPath("$.[*].defaultPrice").value(hasItem(DEFAULT_DEFAULT_PRICE.doubleValue())));

        // Check, that the count call also returns 1
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .title(UPDATED_TITLE)
            .scannerId(UPDATED_SCANNER_ID)
            .usualDurationFromBuyTillExpire(UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE)
            .expireMeansBad(UPDATED_EXPIRE_MEANS_BAD)
            .defaultPrice(UPDATED_DEFAULT_PRICE);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProduct.getScannerId()).isEqualTo(UPDATED_SCANNER_ID);
        assertThat(testProduct.getUsualDurationFromBuyTillExpire()).isEqualTo(UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);
        assertThat(testProduct.getExpireMeansBad()).isEqualTo(UPDATED_EXPIRE_MEANS_BAD);
        assertThat(testProduct.getDefaultPrice()).isEqualTo(UPDATED_DEFAULT_PRICE);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository).save(testProduct);
    }

    @Test
    @Transactional
    void putNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct.defaultPrice(UPDATED_DEFAULT_PRICE);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProduct.getScannerId()).isEqualTo(DEFAULT_SCANNER_ID);
        assertThat(testProduct.getUsualDurationFromBuyTillExpire()).isEqualTo(DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);
        assertThat(testProduct.getExpireMeansBad()).isEqualTo(DEFAULT_EXPIRE_MEANS_BAD);
        assertThat(testProduct.getDefaultPrice()).isEqualTo(UPDATED_DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .title(UPDATED_TITLE)
            .scannerId(UPDATED_SCANNER_ID)
            .usualDurationFromBuyTillExpire(UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE)
            .expireMeansBad(UPDATED_EXPIRE_MEANS_BAD)
            .defaultPrice(UPDATED_DEFAULT_PRICE);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProduct.getScannerId()).isEqualTo(UPDATED_SCANNER_ID);
        assertThat(testProduct.getUsualDurationFromBuyTillExpire()).isEqualTo(UPDATED_USUAL_DURATION_FROM_BUY_TILL_EXPIRE);
        assertThat(testProduct.getExpireMeansBad()).isEqualTo(UPDATED_EXPIRE_MEANS_BAD);
        assertThat(testProduct.getDefaultPrice()).isEqualTo(UPDATED_DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(product);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Delete the product
        restProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, product.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(1)).deleteById(product.getId());
    }

    @Test
    @Transactional
    void searchProduct() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        productRepository.saveAndFlush(product);
        when(mockProductSearchRepository.search("id:" + product.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(product), PageRequest.of(0, 1), 1));

        // Search the product
        restProductMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].scannerId").value(hasItem(DEFAULT_SCANNER_ID)))
            .andExpect(
                jsonPath("$.[*].usualDurationFromBuyTillExpire").value(hasItem(DEFAULT_USUAL_DURATION_FROM_BUY_TILL_EXPIRE.toString()))
            )
            .andExpect(jsonPath("$.[*].expireMeansBad").value(hasItem(DEFAULT_EXPIRE_MEANS_BAD.booleanValue())))
            .andExpect(jsonPath("$.[*].defaultPrice").value(hasItem(DEFAULT_DEFAULT_PRICE.doubleValue())));
    }
}
