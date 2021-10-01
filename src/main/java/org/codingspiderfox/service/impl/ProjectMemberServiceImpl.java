package org.codingspiderfox.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.codingspiderfox.domain.ProjectMember;
import org.codingspiderfox.repository.ProjectMemberRepository;
import org.codingspiderfox.repository.ProjectRepository;
import org.codingspiderfox.repository.search.ProjectMemberSearchRepository;
import org.codingspiderfox.service.ProjectMemberService;
import org.codingspiderfox.service.dto.ProjectMemberDTO;
import org.codingspiderfox.service.mapper.ProjectMemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProjectMember}.
 */
@Service
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberServiceImpl.class);

    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectMemberMapper projectMemberMapper;

    private final ProjectMemberSearchRepository projectMemberSearchRepository;

    private final ProjectRepository projectRepository;

    public ProjectMemberServiceImpl(
        ProjectMemberRepository projectMemberRepository,
        ProjectMemberMapper projectMemberMapper,
        ProjectMemberSearchRepository projectMemberSearchRepository,
        ProjectRepository projectRepository
    ) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectMemberMapper = projectMemberMapper;
        this.projectMemberSearchRepository = projectMemberSearchRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public ProjectMemberDTO save(ProjectMemberDTO projectMemberDTO) {
        log.debug("Request to save ProjectMember : {}", projectMemberDTO);
        ProjectMember projectMember = projectMemberMapper.toEntity(projectMemberDTO);
        Long projectId = projectMemberDTO.getProject().getId();
        projectRepository.findById(projectId).ifPresent(projectMember::project);
        projectMember = projectMemberRepository.save(projectMember);
        ProjectMemberDTO result = projectMemberMapper.toDto(projectMember);
        projectMemberSearchRepository.save(projectMember);
        return result;
    }

    @Override
    public Optional<ProjectMemberDTO> partialUpdate(ProjectMemberDTO projectMemberDTO) {
        log.debug("Request to partially update ProjectMember : {}", projectMemberDTO);

        return projectMemberRepository
            .findById(projectMemberDTO.getId())
            .map(existingProjectMember -> {
                projectMemberMapper.partialUpdate(existingProjectMember, projectMemberDTO);

                return existingProjectMember;
            })
            .map(projectMemberRepository::save)
            .map(savedProjectMember -> {
                projectMemberSearchRepository.save(savedProjectMember);

                return savedProjectMember;
            })
            .map(projectMemberMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectMembers");
        return projectMemberRepository.findAll(pageable).map(projectMemberMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectMemberDTO> findOne(Long id) {
        log.debug("Request to get ProjectMember : {}", id);
        return projectMemberRepository.findById(id).map(projectMemberMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProjectMember : {}", id);
        projectMemberRepository.deleteById(id);
        projectMemberSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectMembers for query {}", query);
        return projectMemberSearchRepository.search(query, pageable).map(projectMemberMapper::toDto);
    }
}
