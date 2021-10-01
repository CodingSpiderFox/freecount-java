package org.codingspiderfox.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.codingspiderfox.domain.BillPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link BillPosition} entity.
 */
public interface BillPositionSearchRepository extends ElasticsearchRepository<BillPosition, Long>, BillPositionSearchRepositoryInternal {}

interface BillPositionSearchRepositoryInternal {
    Page<BillPosition> search(String query, Pageable pageable);
}

class BillPositionSearchRepositoryInternalImpl implements BillPositionSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    BillPositionSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<BillPosition> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<BillPosition> hits = elasticsearchTemplate
            .search(nativeSearchQuery, BillPosition.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
