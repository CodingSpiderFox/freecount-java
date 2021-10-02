package org.codingspiderfox.service;

import org.codingspiderfox.IntegrationTest;
import org.codingspiderfox.domain.FinanceAccount;
import org.codingspiderfox.domain.Project;
import org.codingspiderfox.domain.User;
import org.codingspiderfox.repository.FinanceAccountRepository;
import org.codingspiderfox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class TwoUsersFullIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinanceAccountRepository financeAccountRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectCommandHandler projectCommandHandler ;


    private User user1;
    private User user2;
    private FinanceAccount accountOfUser1;
    private FinanceAccount accountOfUser2;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        user1 = createUser("user1");
        user2 = createUser("user2");
        accountOfUser1 = createFinanceAccount(user1);
        accountOfUser2 = createFinanceAccount(user2);
    }

    @Test
    void createProjectAndCalculateBillSucceeds() {
        projectService.
    }

    private FinanceAccount createFinanceAccount(User owner) {
        FinanceAccount result = new FinanceAccount();
        result.setTitle(owner.getLogin() + " main account");
        result.setCurrentBalance(100.00);
        result.setOwner(owner);

        return financeAccountRepository.saveAndFlush(result);
    }

    private User createUser(String userName) {
        User result = new User();
        result.setActivated(true);
        result.setLogin(userName);

        return userRepository.saveAndFlush(result);
    }
}
