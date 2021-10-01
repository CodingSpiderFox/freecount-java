package org.codingspiderfox.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectSettingsMapperTest {

    private ProjectSettingsMapper projectSettingsMapper;

    @BeforeEach
    public void setUp() {
        projectSettingsMapper = new ProjectSettingsMapperImpl();
    }
}
