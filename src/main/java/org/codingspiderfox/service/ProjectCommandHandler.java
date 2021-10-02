package org.codingspiderfox.service;

import lombok.extern.slf4j.Slf4j;
import org.codingspiderfox.domain.Project;
import org.codingspiderfox.domain.ProjectMember;
import org.codingspiderfox.domain.User;
import org.codingspiderfox.repository.ProjectMemberRepository;
import org.codingspiderfox.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Create a project with the creator as admin
     *
     * @param creator the creator.
     * @param name    the name of the project.
     * @return the project key.
     */
    @Transactional
    public String createProject(User creator, String name) {
        Project project = new Project();
        project.setCreateTimestamp(ZonedDateTime.now());
        project.setKey(replaceAllNonAsciiChars(name));
        project.setName(name);
        project = projectRepository.save(project);

        ProjectMember creatorAsAdminMemberOfProject = projectMemberCommandHandler.addAsAdmin(creator, project);

        return project.getKey();
    }

    private String replaceAllNonAsciiChars(String name) {
        return name.replaceAll("[^\\x00-\\x7F]", "");
    }
}
