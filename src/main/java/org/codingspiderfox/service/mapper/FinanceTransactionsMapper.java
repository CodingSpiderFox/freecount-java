package org.codingspiderfox.service.mapper;

import org.codingspiderfox.domain.*;
import org.codingspiderfox.service.dto.FinanceTransactionsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FinanceTransactions} and its DTO {@link FinanceTransactionsDTO}.
 */
@Mapper(componentModel = "spring", uses = { FinanceAccountMapper.class })
public interface FinanceTransactionsMapper extends EntityMapper<FinanceTransactionsDTO, FinanceTransactions> {
    @Mapping(target = "destinationAccount", source = "destinationAccount", qualifiedByName = "id")
    @Mapping(target = "referenceAccount", source = "referenceAccount", qualifiedByName = "id")
    FinanceTransactionsDTO toDto(FinanceTransactions s);
}
