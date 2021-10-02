package org.codingspiderfox.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectMemberRoleAssignmentMapperTest {

    private ProjectMemberRoleAssignmentMapper projectMemberRoleAssignmentMapper;

    @BeforeEach
    public void setUp() {
        projectMemberRoleAssignmentMapper = new ProjectMemberRoleAssignmentMapperImpl();
    }
}
