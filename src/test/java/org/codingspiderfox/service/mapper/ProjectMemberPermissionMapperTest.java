package org.codingspiderfox.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectMemberPermissionMapperTest {

    private ProjectMemberPermissionMapper projectMemberPermissionMapper;

    @BeforeEach
    public void setUp() {
        projectMemberPermissionMapper = new ProjectMemberPermissionMapperImpl();
    }
}
