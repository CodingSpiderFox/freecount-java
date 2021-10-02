package org.codingspiderfox.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FinanceAccountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FinanceAccount.class);
        FinanceAccount financeAccount1 = new FinanceAccount();
        financeAccount1.setId("id1");
        FinanceAccount financeAccount2 = new FinanceAccount();
        financeAccount2.setId(financeAccount1.getId());
        assertThat(financeAccount1).isEqualTo(financeAccount2);
        financeAccount2.setId("id2");
        assertThat(financeAccount1).isNotEqualTo(financeAccount2);
        financeAccount1.setId(null);
        assertThat(financeAccount1).isNotEqualTo(financeAccount2);
    }
}
