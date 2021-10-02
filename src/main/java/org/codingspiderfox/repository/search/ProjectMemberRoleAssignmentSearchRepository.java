package org.codingspiderfox.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.codingspiderfox.domain.ProjectMemberRoleAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ProjectMemberRoleAssignment} entity.
 */
public interface ProjectMemberRoleAssignmentSearchRepository
    extends ElasticsearchRepository<ProjectMemberRoleAssignment, Long>, ProjectMemberRoleAssignmentSearchRepositoryInternal {}

interface ProjectMemberRoleAssignmentSearchRepositoryInternal {
    Page<ProjectMemberRoleAssignment> search(String query, Pageable pageable);
}

class ProjectMemberRoleAssignmentSearchRepositoryInternalImpl implements ProjectMemberRoleAssignmentSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ProjectMemberRoleAssignmentSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<ProjectMemberRoleAssignment> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<ProjectMemberRoleAssignment> hits = elasticsearchTemplate
            .search(nativeSearchQuery, ProjectMemberRoleAssignment.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
