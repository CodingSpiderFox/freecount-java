package org.codingspiderfox.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FinanceAccountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FinanceAccountDTO.class);
        FinanceAccountDTO financeAccountDTO1 = new FinanceAccountDTO();
        financeAccountDTO1.setId("id1");
        FinanceAccountDTO financeAccountDTO2 = new FinanceAccountDTO();
        assertThat(financeAccountDTO1).isNotEqualTo(financeAccountDTO2);
        financeAccountDTO2.setId(financeAccountDTO1.getId());
        assertThat(financeAccountDTO1).isEqualTo(financeAccountDTO2);
        financeAccountDTO2.setId("id2");
        assertThat(financeAccountDTO1).isNotEqualTo(financeAccountDTO2);
        financeAccountDTO1.setId(null);
        assertThat(financeAccountDTO1).isNotEqualTo(financeAccountDTO2);
    }
}
