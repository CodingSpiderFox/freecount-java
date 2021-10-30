package org.codingspiderfox.service;

import lombok.SneakyThrows;
import org.codingspiderfox.IntegrationTest;
import org.codingspiderfox.domain.*;
import org.codingspiderfox.repository.*;
import org.codingspiderfox.service.dto.CreateProjectMemberDTO;
import org.codingspiderfox.service.dto.ProjectDTO;
import org.codingspiderfox.service.mapper.ProjectMapper;
import org.codingspiderfox.service.mapper.ProjectMemberMapper;
import org.codingspiderfox.web.rest.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
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

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    private User user1;
    private User user2;
    private FinanceAccount accountOfUser1;
    private FinanceAccount accountOfUser2;
    private Project project;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private ProjectMemberPermissionAssignmentRepository projectMemberPermissionAssignmentRepository;

    @Autowired
    private ProjectMemberRoleAssignmentRepository projectMemberRoleAssignmentRepository;

    @Autowired
    private ProjectMemberRoleRepository projectMemberRoleRepository;

    @Autowired
    private ProjectMemberPermissionRepository projectMemberPermissionRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        user1 = createUser("user");
        user2 = createUser("user2");
        accountOfUser1 = createFinanceAccount(user1);
        accountOfUser2 = createFinanceAccount(user2);
        project = createProject(user1);
        userRepository.saveAllAndFlush(Arrays.asList(user1, user2));
    }

    private Project createProject(User user1) {
        Project project = new Project();
        project.setName("projectabcd");
        project.setKey("projectabcd");
        project.setCreateTimestamp(ZonedDateTime.now());
        return project;
    }

    @SneakyThrows
    @Test
    void createProjectAndCalculateBillSucceeds() {
        ProjectDTO projectDTO = projectMapper.toDto(project);
        mockMvc
            .perform(
                post("/api/projects-command")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projectDTO))
            )
            .andExpect(status().isCreated());

        Project project = projectRepository.findAll().stream().findFirst().get();
        assertEquals("projectabcd", project.getKey());

        List<ProjectMemberRole> projectMemberRoles = projectMemberRoleRepository.findAll();

        assertEquals(1, projectMemberRoles.size());

        List<ProjectMemberPermissionAssignment> projectMemberPermissionAssignments = projectMemberPermissionAssignmentRepository.findAll();
        List<ProjectMemberRoleAssignment> projectMemberRoleAssignments = projectMemberRoleAssignmentRepository.findAll();
        assertEquals(0, projectMemberPermissionAssignments.size());
        assertEquals(1, projectMemberRoleAssignments.size());


        ProjectMember member1 = new ProjectMember();
        member1.setProject(project);
        member1.setUser(user2);
        CreateProjectMemberDTO projectMemberDTO = new CreateProjectMemberDTO(null, user2.getId(), project.getId());

        mockMvc
            .perform(post("/api/project-members")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(projectMemberDTO))
            )
            .andExpect(status().isCreated());

        List<ProjectMember> projectMembers = projectMemberRepository.findAll();
        //we expect an admin to be implicitly created on project creation and the one member we added manually
        assertEquals(2, projectMembers.size());

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
        result.setId(UUID.randomUUID().toString());
        result.setActivated(true);
        result.setLogin(userName);

        return result;
    }
}
