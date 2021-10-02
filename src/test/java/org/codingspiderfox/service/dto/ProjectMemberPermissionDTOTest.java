package org.codingspiderfox.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectMemberPermissionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectMemberPermissionDTO.class);
        ProjectMemberPermissionDTO projectMemberPermissionDTO1 = new ProjectMemberPermissionDTO();
        projectMemberPermissionDTO1.setId(1L);
        ProjectMemberPermissionDTO projectMemberPermissionDTO2 = new ProjectMemberPermissionDTO();
        assertThat(projectMemberPermissionDTO1).isNotEqualTo(projectMemberPermissionDTO2);
        projectMemberPermissionDTO2.setId(projectMemberPermissionDTO1.getId());
        assertThat(projectMemberPermissionDTO1).isEqualTo(projectMemberPermissionDTO2);
        projectMemberPermissionDTO2.setId(2L);
        assertThat(projectMemberPermissionDTO1).isNotEqualTo(projectMemberPermissionDTO2);
        projectMemberPermissionDTO1.setId(null);
        assertThat(projectMemberPermissionDTO1).isNotEqualTo(projectMemberPermissionDTO2);
    }
}
