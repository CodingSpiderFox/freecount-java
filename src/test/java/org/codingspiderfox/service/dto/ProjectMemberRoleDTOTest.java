package org.codingspiderfox.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectMemberRoleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectMemberRoleDTO.class);
        ProjectMemberRoleDTO projectMemberRoleDTO1 = new ProjectMemberRoleDTO();
        projectMemberRoleDTO1.setId(1L);
        ProjectMemberRoleDTO projectMemberRoleDTO2 = new ProjectMemberRoleDTO();
        assertThat(projectMemberRoleDTO1).isNotEqualTo(projectMemberRoleDTO2);
        projectMemberRoleDTO2.setId(projectMemberRoleDTO1.getId());
        assertThat(projectMemberRoleDTO1).isEqualTo(projectMemberRoleDTO2);
        projectMemberRoleDTO2.setId(2L);
        assertThat(projectMemberRoleDTO1).isNotEqualTo(projectMemberRoleDTO2);
        projectMemberRoleDTO1.setId(null);
        assertThat(projectMemberRoleDTO1).isNotEqualTo(projectMemberRoleDTO2);
    }
}
