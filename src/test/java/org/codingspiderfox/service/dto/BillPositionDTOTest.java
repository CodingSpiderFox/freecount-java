package org.codingspiderfox.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BillPositionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BillPositionDTO.class);
        BillPositionDTO billPositionDTO1 = new BillPositionDTO();
        billPositionDTO1.setId(1L);
        BillPositionDTO billPositionDTO2 = new BillPositionDTO();
        assertThat(billPositionDTO1).isNotEqualTo(billPositionDTO2);
        billPositionDTO2.setId(billPositionDTO1.getId());
        assertThat(billPositionDTO1).isEqualTo(billPositionDTO2);
        billPositionDTO2.setId(2L);
        assertThat(billPositionDTO1).isNotEqualTo(billPositionDTO2);
        billPositionDTO1.setId(null);
        assertThat(billPositionDTO1).isNotEqualTo(billPositionDTO2);
    }
}
