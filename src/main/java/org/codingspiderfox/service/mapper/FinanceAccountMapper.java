package org.codingspiderfox.service.mapper;

import org.codingspiderfox.domain.*;
import org.codingspiderfox.service.dto.FinanceAccountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FinanceAccount} and its DTO {@link FinanceAccountDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface FinanceAccountMapper extends EntityMapper<FinanceAccountDTO, FinanceAccount> {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "login")
    FinanceAccountDTO toDto(FinanceAccount s);
}
