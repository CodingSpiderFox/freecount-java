package org.codingspiderfox.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectMemberRoleAssignmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectMemberRoleAssignmentDTO.class);
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO1 = new ProjectMemberRoleAssignmentDTO();
        projectMemberRoleAssignmentDTO1.setId(1L);
        ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO2 = new ProjectMemberRoleAssignmentDTO();
        assertThat(projectMemberRoleAssignmentDTO1).isNotEqualTo(projectMemberRoleAssignmentDTO2);
        projectMemberRoleAssignmentDTO2.setId(projectMemberRoleAssignmentDTO1.getId());
        assertThat(projectMemberRoleAssignmentDTO1).isEqualTo(projectMemberRoleAssignmentDTO2);
        projectMemberRoleAssignmentDTO2.setId(2L);
        assertThat(projectMemberRoleAssignmentDTO1).isNotEqualTo(projectMemberRoleAssignmentDTO2);
        projectMemberRoleAssignmentDTO1.setId(null);
        assertThat(projectMemberRoleAssignmentDTO1).isNotEqualTo(projectMemberRoleAssignmentDTO2);
    }
}
