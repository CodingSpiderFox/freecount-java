package org.codingspiderfox.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.codingspiderfox.domain.ProjectMemberPermissionAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ProjectMemberPermissionAssignment} entity.
 */
public interface ProjectMemberPermissionAssignmentSearchRepository
    extends ElasticsearchRepository<ProjectMemberPermissionAssignment, Long>, ProjectMemberPermissionAssignmentSearchRepositoryInternal {}

interface ProjectMemberPermissionAssignmentSearchRepositoryInternal {
    Page<ProjectMemberPermissionAssignment> search(String query, Pageable pageable);
}

class ProjectMemberPermissionAssignmentSearchRepositoryInternalImpl implements ProjectMemberPermissionAssignmentSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ProjectMemberPermissionAssignmentSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<ProjectMemberPermissionAssignment> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<ProjectMemberPermissionAssignment> hits = elasticsearchTemplate
            .search(nativeSearchQuery, ProjectMemberPermissionAssignment.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
