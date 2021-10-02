package org.codingspiderfox.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FinanceTransactionsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FinanceTransactionsDTO.class);
        FinanceTransactionsDTO financeTransactionsDTO1 = new FinanceTransactionsDTO();
        financeTransactionsDTO1.setId("id1");
        FinanceTransactionsDTO financeTransactionsDTO2 = new FinanceTransactionsDTO();
        assertThat(financeTransactionsDTO1).isNotEqualTo(financeTransactionsDTO2);
        financeTransactionsDTO2.setId(financeTransactionsDTO1.getId());
        assertThat(financeTransactionsDTO1).isEqualTo(financeTransactionsDTO2);
        financeTransactionsDTO2.setId("id2");
        assertThat(financeTransactionsDTO1).isNotEqualTo(financeTransactionsDTO2);
        financeTransactionsDTO1.setId(null);
        assertThat(financeTransactionsDTO1).isNotEqualTo(financeTransactionsDTO2);
    }
}
