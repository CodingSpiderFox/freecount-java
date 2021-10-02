package org.codingspiderfox.service;

import org.codingspiderfox.IntegrationTest;
import org.codingspiderfox.domain.User;
import org.codingspiderfox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class TwoUsersFullIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        user1 = new User();
        user1.setActivated(true);
        user1.setLogin("user1");

        user2 = new User();
        user2.setActivated(true);
        user2.setLogin("user2");
    }
}
