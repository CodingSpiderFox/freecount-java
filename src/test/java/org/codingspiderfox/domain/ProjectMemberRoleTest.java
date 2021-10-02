package org.codingspiderfox.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectMemberRoleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectMemberRole.class);
        ProjectMemberRole projectMemberRole1 = new ProjectMemberRole();
        projectMemberRole1.setId(1L);
        ProjectMemberRole projectMemberRole2 = new ProjectMemberRole();
        projectMemberRole2.setId(projectMemberRole1.getId());
        assertThat(projectMemberRole1).isEqualTo(projectMemberRole2);
        projectMemberRole2.setId(2L);
        assertThat(projectMemberRole1).isNotEqualTo(projectMemberRole2);
        projectMemberRole1.setId(null);
        assertThat(projectMemberRole1).isNotEqualTo(projectMemberRole2);
    }
}
