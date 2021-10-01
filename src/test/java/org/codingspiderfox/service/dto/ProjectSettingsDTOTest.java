package org.codingspiderfox.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectSettingsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectSettingsDTO.class);
        ProjectSettingsDTO projectSettingsDTO1 = new ProjectSettingsDTO();
        projectSettingsDTO1.setId(1L);
        ProjectSettingsDTO projectSettingsDTO2 = new ProjectSettingsDTO();
        assertThat(projectSettingsDTO1).isNotEqualTo(projectSettingsDTO2);
        projectSettingsDTO2.setId(projectSettingsDTO1.getId());
        assertThat(projectSettingsDTO1).isEqualTo(projectSettingsDTO2);
        projectSettingsDTO2.setId(2L);
        assertThat(projectSettingsDTO1).isNotEqualTo(projectSettingsDTO2);
        projectSettingsDTO1.setId(null);
        assertThat(projectSettingsDTO1).isNotEqualTo(projectSettingsDTO2);
    }
}
