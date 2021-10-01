package org.codingspiderfox.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.codingspiderfox.domain.ProjectSettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ProjectSettings} entity.
 */
public interface ProjectSettingsSearchRepository
    extends ElasticsearchRepository<ProjectSettings, Long>, ProjectSettingsSearchRepositoryInternal {}

interface ProjectSettingsSearchRepositoryInternal {
    Page<ProjectSettings> search(String query, Pageable pageable);
}

class ProjectSettingsSearchRepositoryInternalImpl implements ProjectSettingsSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ProjectSettingsSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<ProjectSettings> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<ProjectSettings> hits = elasticsearchTemplate
            .search(nativeSearchQuery, ProjectSettings.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
