package org.codingspiderfox.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectSettingsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectSettings.class);
        ProjectSettings projectSettings1 = new ProjectSettings();
        projectSettings1.setId(1L);
        ProjectSettings projectSettings2 = new ProjectSettings();
        projectSettings2.setId(projectSettings1.getId());
        assertThat(projectSettings1).isEqualTo(projectSettings2);
        projectSettings2.setId(2L);
        assertThat(projectSettings1).isNotEqualTo(projectSettings2);
        projectSettings1.setId(null);
        assertThat(projectSettings1).isNotEqualTo(projectSettings2);
    }
}
