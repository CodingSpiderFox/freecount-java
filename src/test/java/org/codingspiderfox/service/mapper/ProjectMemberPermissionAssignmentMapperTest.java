package org.codingspiderfox.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectMemberPermissionAssignmentMapperTest {

    private ProjectMemberPermissionAssignmentMapper projectMemberPermissionAssignmentMapper;

    @BeforeEach
    public void setUp() {
        projectMemberPermissionAssignmentMapper = new ProjectMemberPermissionAssignmentMapperImpl();
    }
}
