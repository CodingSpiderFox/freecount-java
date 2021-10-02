package org.codingspiderfox.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FinanceAccountMapperTest {

    private FinanceAccountMapper financeAccountMapper;

    @BeforeEach
    public void setUp() {
        financeAccountMapper = new FinanceAccountMapperImpl();
    }
}
