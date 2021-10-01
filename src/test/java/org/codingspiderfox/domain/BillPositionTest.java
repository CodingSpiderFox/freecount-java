package org.codingspiderfox.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BillPositionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BillPosition.class);
        BillPosition billPosition1 = new BillPosition();
        billPosition1.setId(1L);
        BillPosition billPosition2 = new BillPosition();
        billPosition2.setId(billPosition1.getId());
        assertThat(billPosition1).isEqualTo(billPosition2);
        billPosition2.setId(2L);
        assertThat(billPosition1).isNotEqualTo(billPosition2);
        billPosition1.setId(null);
        assertThat(billPosition1).isNotEqualTo(billPosition2);
    }
}
