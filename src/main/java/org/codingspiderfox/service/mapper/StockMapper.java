package org.codingspiderfox.service.mapper;

import org.codingspiderfox.domain.*;
import org.codingspiderfox.service.dto.StockDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Stock} and its DTO {@link StockDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface StockMapper extends EntityMapper<StockDTO, Stock> {
    @Mapping(target = "product", source = "product", qualifiedByName = "id")
    StockDTO toDto(Stock s);
}
