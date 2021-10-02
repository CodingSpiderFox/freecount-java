package org.codingspiderfox.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectMemberPermissionAssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectMemberPermissionAssignment.class);
        ProjectMemberPermissionAssignment projectMemberPermissionAssignment1 = new ProjectMemberPermissionAssignment();
        projectMemberPermissionAssignment1.setId(1L);
        ProjectMemberPermissionAssignment projectMemberPermissionAssignment2 = new ProjectMemberPermissionAssignment();
        projectMemberPermissionAssignment2.setId(projectMemberPermissionAssignment1.getId());
        assertThat(projectMemberPermissionAssignment1).isEqualTo(projectMemberPermissionAssignment2);
        projectMemberPermissionAssignment2.setId(2L);
        assertThat(projectMemberPermissionAssignment1).isNotEqualTo(projectMemberPermissionAssignment2);
        projectMemberPermissionAssignment1.setId(null);
        assertThat(projectMemberPermissionAssignment1).isNotEqualTo(projectMemberPermissionAssignment2);
    }
}
