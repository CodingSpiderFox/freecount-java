package org.codingspiderfox.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectMemberRoleAssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectMemberRoleAssignment.class);
        ProjectMemberRoleAssignment projectMemberRoleAssignment1 = new ProjectMemberRoleAssignment();
        projectMemberRoleAssignment1.setId(1L);
        ProjectMemberRoleAssignment projectMemberRoleAssignment2 = new ProjectMemberRoleAssignment();
        projectMemberRoleAssignment2.setId(projectMemberRoleAssignment1.getId());
        assertThat(projectMemberRoleAssignment1).isEqualTo(projectMemberRoleAssignment2);
        projectMemberRoleAssignment2.setId(2L);
        assertThat(projectMemberRoleAssignment1).isNotEqualTo(projectMemberRoleAssignment2);
        projectMemberRoleAssignment1.setId(null);
        assertThat(projectMemberRoleAssignment1).isNotEqualTo(projectMemberRoleAssignment2);
    }
}
