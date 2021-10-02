package org.codingspiderfox.service;

import lombok.extern.slf4j.Slf4j;
import org.codingspiderfox.domain.Project;
import org.codingspiderfox.domain.ProjectMember;
import org.codingspiderfox.domain.User;
import org.codingspiderfox.repository.ProjectMemberRepository;
import org.codingspiderfox.repository.ProjectRepository;
import org.codingspiderfox.security.SecurityUtils;
import org.codingspiderfox.service.dto.ProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Security;
import java.time.ZonedDateTime;

@Service
@Transactional
@Slf4j
public class ProjectCommandHandler {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectMemberCommandHandler projectMemberCommandHandler;

    @Autowired
    private UserQueryService userQueryService;

    /**
     * Create a project with the creator as admin
     *
     * @param creator the creator.
     * @param name    the name of the project.
     * @return the project key.
     */
    @Transactional
    public Project createProject(User creator, String name) {
        Project project = new Project();
        project.setCreateTimestamp(ZonedDateTime.now());
        project.setKey(replaceAllNonAsciiChars(name));
        project.setName(name);
        project = projectRepository.save(project);

        ProjectMember creatorAsAdminMemberOfProject = projectMemberCommandHandler.addAsAdmin(creator, project);

        return project;
    }

    private String replaceAllNonAsciiChars(String name) {
        return name.replaceAll("[^\\x00-\\x7F]", "");
    }

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        User currentUser = userQueryService.findByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("No login name present")))
            .orElseThrow(() -> new IllegalStateException("User not found"));
        Project project = createProject(currentUser, projectDTO.getName());

        return new ProjectDTO(project.getId(), project.getName(), project.getKey(), project.getCreateTimestamp());
    }
}
