package org.codingspiderfox.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.codingspiderfox.domain.Project;
import org.codingspiderfox.domain.ProjectMember;
import org.codingspiderfox.domain.ProjectMemberRole;
import org.codingspiderfox.domain.ProjectMemberRoleAssignment;
import org.codingspiderfox.domain.User;
import org.codingspiderfox.domain.enumeration.ProjectMemberRoleEnum;
import org.codingspiderfox.repository.ProjectMemberRepository;
import org.codingspiderfox.repository.ProjectMemberRoleAssignmentRepository;
import org.codingspiderfox.repository.ProjectMemberRoleRepository;
import org.codingspiderfox.repository.ProjectRepository;
import org.codingspiderfox.security.SecurityUtils;
import org.codingspiderfox.service.dto.CreateProjectMemberDTO;
import org.codingspiderfox.service.dto.ProjectMemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ProjectMemberCommandHandler {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    private ProjectMemberQueryService projectMemberQueryService;

    @Autowired
    private ProjectMemberRoleAssignmentRepository projectMemberRoleAssignmentRepository;

    @Autowired
    private ProjectMemberRoleRepository projectMemberRoleRepository;

    @Autowired
    private ProjectMemberRoleQueryService projectMemberRoleQueryService;

    @Autowired
    private ProjectMemberPermissionAssignmentQueryService projectMemberPermissionAssignmentQueryService;

    @Autowired
    private ProjectMemberRoleAssignmentQueryService projectMemberRoleAssignmentQueryService;

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Transactional
    public ProjectMemberDTO createProjectMember(CreateProjectMemberDTO createProjectMemberDTO) {

        addMembersToProjectWithDefaultPermissions(createProjectMemberDTO.getProjectId(), Arrays.asList(createProjectMemberDTO.getUserId()));
        return new ProjectMemberDTO();
    }

    @Transactional
    public ProjectMember addAsAdmin(User newAdmin, Project project) {
        ProjectMember creatorAsAdminMemberOfProject = new ProjectMember();
        creatorAsAdminMemberOfProject.setProject(project);
        creatorAsAdminMemberOfProject.setUser(newAdmin);
        creatorAsAdminMemberOfProject.setAddedTimestamp(ZonedDateTime.now());
        creatorAsAdminMemberOfProject = projectMemberRepository.save(creatorAsAdminMemberOfProject);

        ProjectMemberRole adminRole = projectMemberRoleRepository
            .findByProjectMemberRole(ProjectMemberRoleEnum.PROJECT_ADMIN)
            .stream()
            .findFirst()
            .orElseGet(() -> projectMemberRoleRepository.save(new ProjectMemberRole(null, ZonedDateTime.now(), ProjectMemberRoleEnum.PROJECT_ADMIN)));

        ProjectMemberRoleAssignment projectAdminRoleAssignment = new ProjectMemberRoleAssignment();
        Set<ProjectMemberRole> rolesSet = new HashSet<>();
        rolesSet.add(adminRole);
        projectAdminRoleAssignment.setProjectMemberRoles(rolesSet);
        projectAdminRoleAssignment.setAssignmentTimestamp(ZonedDateTime.now());
        projectAdminRoleAssignment.setProjectMember(creatorAsAdminMemberOfProject);
        projectMemberRoleAssignmentRepository.save(projectAdminRoleAssignment);

        return creatorAsAdminMemberOfProject;
    }

    @SneakyThrows
    public Boolean addMembersToProjectWithDefaultPermissions(Long projectId, List<String> newMemberUserIds) {
        Future<Boolean> currentLoggedInUserHasAdminPermissions = checkCurrentUserHasPermissionToAddMemberToProject(
            SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No login name present")),
            projectId
        );

        String projectAccessErrorMsg = "Project for id " + projectId + " does not exist or no permission";
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException(projectAccessErrorMsg));
        if (!currentLoggedInUserHasAdminPermissions.get()) {
            throw new IllegalArgumentException(projectAccessErrorMsg);
        }

        Future<List<User>> usersForMemberIds = getUsersForMemberIds(newMemberUserIds);
        Set<String> newMemberUserIdsDistinct = newMemberUserIds.stream().collect(Collectors.toSet());
        List<ProjectMember> membersToAdd = new ArrayList<>();
        List<ProjectMember> finalMembersToAdd = membersToAdd;
        List<ProjectMemberRoleAssignment> memberRoleAssignmentsToAdd = new ArrayList<>();

        Future<Set<ProjectMemberRole>> memberRolesDefault = getMemberRolesAsync();
        List<ProjectMemberRoleAssignment> finalMemberRoleAssignmentsToAdd = memberRoleAssignmentsToAdd;
        newMemberUserIdsDistinct.forEach(userId -> {
            ProjectMember newMember = new ProjectMember();
            newMember.setProject(project);
            newMember.setAddedTimestamp(ZonedDateTime.now());
            ProjectMemberRoleAssignment projectMemberRoleAssignment = new ProjectMemberRoleAssignment();
            try {
                projectMemberRoleAssignment.setProjectMemberRoles(memberRolesDefault.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            projectMemberRoleAssignment.setAssignmentTimestamp(ZonedDateTime.now());
            projectMemberRoleAssignment.setProjectMember(newMember);
            finalMemberRoleAssignmentsToAdd.add(projectMemberRoleAssignment);
            try {
                newMember.setUser(usersForMemberIds.get().stream().filter(user -> user.getId().equals(userId)).findFirst().get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            finalMembersToAdd.add(newMember);
        });

        membersToAdd = projectMemberRepository.saveAllAndFlush(finalMembersToAdd);
        memberRoleAssignmentsToAdd = projectMemberRoleAssignmentRepository.saveAllAndFlush(finalMemberRoleAssignmentsToAdd);
        return true;
    }

    private Future<Set<ProjectMemberRole>> getMemberRolesAsync() {
        return executor.submit(() -> {
            HashSet<ProjectMemberRole> roles = new HashSet<>();
            roles.add(
                projectMemberRoleRepository
                    .findByProjectMemberRole(ProjectMemberRoleEnum.BILL_CONTRIBUTOR)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> projectMemberRoleRepository.save(new ProjectMemberRole(null, ZonedDateTime.now(), ProjectMemberRoleEnum.BILL_CONTRIBUTOR)))
            );
            return roles;
        });
    }

    private Future<List<User>> getUsersForMemberIds(List<String> newMemberUserIds) {
        return executor.submit(() -> userQueryService.findByIdIn(newMemberUserIds));
    }

    @SneakyThrows
    private Future<Boolean> checkCurrentUserHasPermissionToAddMemberToProject(String currentUserLogin, Long projectId) {
        Future<Boolean> hasAddMemberPermission = executor.submit(() -> projectMemberPermissionAssignmentQueryService.hasAddMemberPermissionAssignmentForProjectIdAndUserLogin(currentUserLogin, projectId));
        Future<Boolean> hasRoleThatAllowsToAddMemberToProject = executor.submit(() -> projectMemberRoleAssignmentQueryService.hasRoleAssignmentForProjectAndUserThatAllowsAddingMembersToProject(currentUserLogin, projectId));
        return executor.submit(() -> hasAddMemberPermission.get() && hasRoleThatAllowsToAddMemberToProject.get());
    }
}
