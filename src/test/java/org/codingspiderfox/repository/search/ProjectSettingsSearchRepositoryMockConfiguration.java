package org.codingspiderfox.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ProjectSettingsSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ProjectSettingsSearchRepositoryMockConfiguration {

    @MockBean
    private ProjectSettingsSearchRepository mockProjectSettingsSearchRepository;
}
