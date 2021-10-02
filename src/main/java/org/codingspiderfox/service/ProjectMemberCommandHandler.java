package org.codingspiderfox.service;

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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Transactional
    public ProjectMember addAsAdmin(User newAdmin, Project project) {
        ProjectMember creatorAsAdminMemberOfProject = new ProjectMember();
        creatorAsAdminMemberOfProject.setProject(project);
        creatorAsAdminMemberOfProject.setUser(newAdmin);
        creatorAsAdminMemberOfProject.setRoleInProject(ProjectMemberRole.PROJECT_ADMIN);
        creatorAsAdminMemberOfProject.setAddedTimestamp(ZonedDateTime.now());
        creatorAsAdminMemberOfProject = projectMemberRepository.save(creatorAsAdminMemberOfProject);

        return creatorAsAdminMemberOfProject;
    }

    public Boolean addMembersToProjectWithDefaultPermissions(Long projectId, List<Long> newMemberUserIds) {
        Future<Boolean> currentLoggedInUserHasAdminPermissions = checkCurrentUserHasAdminPermissions(SecurityUtils.getCurrentUserLogin(), projectId);

        String projectAccessErrorMsg = "Project for id " + projectId + " does not exist or no permission";
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException(projectAccessErrorMsg));


    }

    private Future<Boolean> checkCurrentUserHasAdminPermissions(Optional<String> currentUserLogin, Long projectId) {

        return executor.submit(() -> {
            Boolean result = false;

        })
    }
}
