package org.codingspiderfox.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BillPositionMapperTest {

    private BillPositionMapper billPositionMapper;

    @BeforeEach
    public void setUp() {
        billPositionMapper = new BillPositionMapperImpl();
    }
}
