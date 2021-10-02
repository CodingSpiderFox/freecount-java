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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ProjectMemberCommandHandler {
    @Autowired
    private ProjectMemberRepository projectMemberRepository;

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

}
