package org.codingspiderfox.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectMemberPermissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectMemberPermission.class);
        ProjectMemberPermission projectMemberPermission1 = new ProjectMemberPermission();
        projectMemberPermission1.setId(1L);
        ProjectMemberPermission projectMemberPermission2 = new ProjectMemberPermission();
        projectMemberPermission2.setId(projectMemberPermission1.getId());
        assertThat(projectMemberPermission1).isEqualTo(projectMemberPermission2);
        projectMemberPermission2.setId(2L);
        assertThat(projectMemberPermission1).isNotEqualTo(projectMemberPermission2);
        projectMemberPermission1.setId(null);
        assertThat(projectMemberPermission1).isNotEqualTo(projectMemberPermission2);
    }
}
