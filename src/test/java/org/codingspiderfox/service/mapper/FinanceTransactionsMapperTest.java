package org.codingspiderfox.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FinanceTransactionsMapperTest {

    private FinanceTransactionsMapper financeTransactionsMapper;

    @BeforeEach
    public void setUp() {
        financeTransactionsMapper = new FinanceTransactionsMapperImpl();
    }
}
