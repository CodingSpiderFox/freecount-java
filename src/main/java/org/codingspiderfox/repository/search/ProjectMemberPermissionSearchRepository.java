package org.codingspiderfox.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.codingspiderfox.domain.ProjectMemberPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ProjectMemberPermission} entity.
 */
public interface ProjectMemberPermissionSearchRepository
    extends ElasticsearchRepository<ProjectMemberPermission, Long>, ProjectMemberPermissionSearchRepositoryInternal {}

interface ProjectMemberPermissionSearchRepositoryInternal {
    Page<ProjectMemberPermission> search(String query, Pageable pageable);
}

class ProjectMemberPermissionSearchRepositoryInternalImpl implements ProjectMemberPermissionSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ProjectMemberPermissionSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<ProjectMemberPermission> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<ProjectMemberPermission> hits = elasticsearchTemplate
            .search(nativeSearchQuery, ProjectMemberPermission.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
