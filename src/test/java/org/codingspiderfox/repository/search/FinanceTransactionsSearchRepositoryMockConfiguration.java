package org.codingspiderfox.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link FinanceTransactionsSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class FinanceTransactionsSearchRepositoryMockConfiguration {

    @MockBean
    private FinanceTransactionsSearchRepository mockFinanceTransactionsSearchRepository;
}
