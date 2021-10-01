package org.codingspiderfox.service.mapper;

import org.codingspiderfox.domain.*;
import org.codingspiderfox.service.dto.BillPositionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BillPosition} and its DTO {@link BillPositionDTO}.
 */
@Mapper(componentModel = "spring", uses = { BillMapper.class })
public interface BillPositionMapper extends EntityMapper<BillPositionDTO, BillPosition> {
    @Mapping(target = "bill", source = "bill", qualifiedByName = "id")
    BillPositionDTO toDto(BillPosition s);
}
