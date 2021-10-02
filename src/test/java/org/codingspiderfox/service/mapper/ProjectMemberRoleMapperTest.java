package org.codingspiderfox.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectMemberRoleMapperTest {

    private ProjectMemberRoleMapper projectMemberRoleMapper;

    @BeforeEach
    public void setUp() {
        projectMemberRoleMapper = new ProjectMemberRoleMapperImpl();
    }
}
