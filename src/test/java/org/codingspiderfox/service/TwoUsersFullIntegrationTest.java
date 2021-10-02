package org.codingspiderfox.service;

import lombok.SneakyThrows;
import org.codingspiderfox.IntegrationTest;
import org.codingspiderfox.domain.FinanceAccount;
import org.codingspiderfox.domain.Project;
import org.codingspiderfox.domain.User;
import org.codingspiderfox.repository.FinanceAccountRepository;
import org.codingspiderfox.repository.UserRepository;
import org.codingspiderfox.service.dto.ProjectDTO;
import org.codingspiderfox.service.dto.UserDTO;
import org.codingspiderfox.service.mapper.ProjectMapper;
import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@Transactional
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TwoUsersFullIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinanceAccountRepository financeAccountRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectCommandHandler projectCommandHandler;

    @Autowired
    private ProjectMapper projectMapper;

    private User user1;
    private User user2;
    private FinanceAccount accountOfUser1;
    private FinanceAccount accountOfUser2;
    private Project project;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        user1 = createUser("user1");
        user2 = createUser("user2");
        accountOfUser1 = createFinanceAccount(user1);
        accountOfUser2 = createFinanceAccount(user2);
        project = createProject(user1);
    }

    private Project createProject(User user1) {
        Project project = new Project();
        project.setName("project");
        project.setKey("project");
        project.setCreateTimestamp(ZonedDateTime.now());
        return project;
    }

    @SneakyThrows
    @Test
    @Transactional
    void createProjectAndCalculateBillSucceeds() {
        ProjectDTO projectDTO = projectMapper.toDto(project);
        mockMvc
            .perform(
                post("/api/projects")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectDTO))
            )
            .andExpect(status().isCreated());
    }

    private FinanceAccount createFinanceAccount(User owner) {
        FinanceAccount result = new FinanceAccount();
        result.setTitle(owner.getLogin() + " main account");
        result.setCurrentBalance(100.00);
        result.setOwner(owner);

        return result;
    }

    private User createUser(String userName) {
        User result = new User();
        result.setActivated(true);
        result.setLogin(userName);

        return result;
    }
}
