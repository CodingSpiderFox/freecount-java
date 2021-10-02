package org.codingspiderfox.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectMemberPermissionAssignmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectMemberPermissionAssignmentDTO.class);
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO1 = new ProjectMemberPermissionAssignmentDTO();
        projectMemberPermissionAssignmentDTO1.setId(1L);
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO2 = new ProjectMemberPermissionAssignmentDTO();
        assertThat(projectMemberPermissionAssignmentDTO1).isNotEqualTo(projectMemberPermissionAssignmentDTO2);
        projectMemberPermissionAssignmentDTO2.setId(projectMemberPermissionAssignmentDTO1.getId());
        assertThat(projectMemberPermissionAssignmentDTO1).isEqualTo(projectMemberPermissionAssignmentDTO2);
        projectMemberPermissionAssignmentDTO2.setId(2L);
        assertThat(projectMemberPermissionAssignmentDTO1).isNotEqualTo(projectMemberPermissionAssignmentDTO2);
        projectMemberPermissionAssignmentDTO1.setId(null);
        assertThat(projectMemberPermissionAssignmentDTO1).isNotEqualTo(projectMemberPermissionAssignmentDTO2);
    }
}
