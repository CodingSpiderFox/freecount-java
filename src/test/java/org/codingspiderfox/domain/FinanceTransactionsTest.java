package org.codingspiderfox.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FinanceTransactionsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FinanceTransactions.class);
        FinanceTransactions financeTransactions1 = new FinanceTransactions();
        financeTransactions1.setId("id1");
        FinanceTransactions financeTransactions2 = new FinanceTransactions();
        financeTransactions2.setId(financeTransactions1.getId());
        assertThat(financeTransactions1).isEqualTo(financeTransactions2);
        financeTransactions2.setId("id2");
        assertThat(financeTransactions1).isNotEqualTo(financeTransactions2);
        financeTransactions1.setId(null);
        assertThat(financeTransactions1).isNotEqualTo(financeTransactions2);
    }
}
