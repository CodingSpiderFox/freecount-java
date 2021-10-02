package org.codingspiderfox.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.codingspiderfox.domain.Bill;
import org.codingspiderfox.domain.BillPosition;
import org.codingspiderfox.domain.Project;
import org.codingspiderfox.domain.ProjectMember;
import org.codingspiderfox.domain.User;
import org.codingspiderfox.domain.enumeration.ProjectMemberRole;
import org.codingspiderfox.domain.enumeration.ProjectPermission;
import org.codingspiderfox.repository.BillPositionRepository;
import org.codingspiderfox.repository.BillRepository;
import org.codingspiderfox.repository.ProjectMemberRepository;
import org.codingspiderfox.repository.ProjectRepository;
import org.codingspiderfox.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Transactional
    public ProjectMember addAsAdmin(User newAdmin, Project project) {
        ProjectMember creatorAsAdminMemberOfProject = new ProjectMember();
        creatorAsAdminMemberOfProject.setProject(project);
        creatorAsAdminMemberOfProject.setUser(newAdmin);
        creatorAsAdminMemberOfProject.setRoleInProject(Arrays.asList(ProjectMemberRole.PROJECT_ADMIN));
        creatorAsAdminMemberOfProject.setAddedTimestamp(ZonedDateTime.now());
        creatorAsAdminMemberOfProject = projectMemberRepository.save(creatorAsAdminMemberOfProject);

        return creatorAsAdminMemberOfProject;
    }

    @SneakyThrows
    public Boolean addMembersToProjectWithDefaultPermissions(Long projectId, List<String> newMemberUserIds) {
        Future<Boolean> currentLoggedInUserHasAdminPermissions = checkCurrentUserHasAdminPermissions(
            SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No login name present")),
            projectId);

        String projectAccessErrorMsg = "Project for id " + projectId + " does not exist or no permission";
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException(projectAccessErrorMsg));
        if (!currentLoggedInUserHasAdminPermissions.get()) {
            throw new IllegalArgumentException(projectAccessErrorMsg);
        }


        Future<List<User>> usersForMemberIds = getUsersForMemberIds(newMemberUserIds);
        Set<String> newMemberUserIdsDistinct = newMemberUserIds.stream().collect(Collectors.toSet());
        List<ProjectMember> membersToAdd = new ArrayList<>();
        List<ProjectMember> finalMembersToAdd = membersToAdd;
        newMemberUserIdsDistinct.forEach(userId -> {
            ProjectMember newMember = new ProjectMember();
            newMember.setProject(project);
            newMember.setAddedTimestamp(ZonedDateTime.now());
            newMember.setRoleInProject(Arrays.asList(ProjectMemberRole.BILL_CONTRIBUTOR));
            newMember.setAdditionalProjectPermissions(Collections.emptyList());
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
        return true;
    }

    private Future<List<User>> getUsersForMemberIds(List<String> newMemberUserIds) {
        return executor.submit(() -> userQueryService.findByIdIn(newMemberUserIds));
    }

    private Future<Boolean> checkCurrentUserHasAdminPermissions(String currentUserLogin, Long projectId) {
        return executor.submit(() -> {
            return !projectMemberQueryService.findByAdminUserLoginAndProject(currentUserLogin, projectId).isEmpty();
        });
    }
}
