package org.codingspiderfox.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.codingspiderfox.domain.ProjectMemberRoleAssignment;
import org.codingspiderfox.repository.ProjectMemberRepository;
import org.codingspiderfox.repository.ProjectMemberRoleAssignmentRepository;
import org.codingspiderfox.repository.search.ProjectMemberRoleAssignmentSearchRepository;
import org.codingspiderfox.service.ProjectMemberRoleAssignmentService;
import org.codingspiderfox.service.dto.ProjectMemberRoleAssignmentDTO;
import org.codingspiderfox.service.mapper.ProjectMemberRoleAssignmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProjectMemberRoleAssignment}.
 */
@Service
@Transactional
public class ProjectMemberRoleAssignmentServiceImpl implements ProjectMemberRoleAssignmentService {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberRoleAssignmentServiceImpl.class);

    private final ProjectMemberRoleAssignmentRepository projectMemberRoleAssignmentRepository;

    private final ProjectMemberRoleAssignmentMapper projectMemberRoleAssignmentMapper;

    private final ProjectMemberRoleAssignmentSearchRepository projectMemberRoleAssignmentSearchRepository;

    private final ProjectMemberRepository projectMemberRepository;

    public ProjectMemberRoleAssignmentServiceImpl(
        ProjectMemberRoleAssignmentRepository projectMemberRoleAssignmentRepository,
        ProjectMemberRoleAssignmentMapper projectMemberRoleAssignmentMapper,
        ProjectMemberRoleAssignmentSearchRepository projectMemberRoleAssignmentSearchRepository,
        ProjectMemberRepository projectMemberRepository
    ) {
        this.projectMemberRoleAssignmentRepository = projectMemberRoleAssignmentRepository;
        this.projectMemberRoleAssignmentMapper = projectMemberRoleAssignmentMapper;
        this.projectMemberRoleAssignmentSearchRepository = projectMemberRoleAssignmentSearchRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    public ProjectMemberRoleAssignmentDTO save(ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO) {
        log.debug("Request to save ProjectMemberRoleAssignment : {}", projectMemberRoleAssignmentDTO);
        ProjectMemberRoleAssignment projectMemberRoleAssignment = projectMemberRoleAssignmentMapper.toEntity(
            projectMemberRoleAssignmentDTO
        );
        Long projectMemberId = projectMemberRoleAssignmentDTO.getProjectMember().getId();
        projectMemberRepository.findById(projectMemberId).ifPresent(projectMemberRoleAssignment::projectMember);
        projectMemberRoleAssignment = projectMemberRoleAssignmentRepository.save(projectMemberRoleAssignment);
        ProjectMemberRoleAssignmentDTO result = projectMemberRoleAssignmentMapper.toDto(projectMemberRoleAssignment);
        projectMemberRoleAssignmentSearchRepository.save(projectMemberRoleAssignment);
        return result;
    }

    @Override
    public Optional<ProjectMemberRoleAssignmentDTO> partialUpdate(ProjectMemberRoleAssignmentDTO projectMemberRoleAssignmentDTO) {
        log.debug("Request to partially update ProjectMemberRoleAssignment : {}", projectMemberRoleAssignmentDTO);

        return projectMemberRoleAssignmentRepository
            .findById(projectMemberRoleAssignmentDTO.getId())
            .map(existingProjectMemberRoleAssignment -> {
                projectMemberRoleAssignmentMapper.partialUpdate(existingProjectMemberRoleAssignment, projectMemberRoleAssignmentDTO);

                return existingProjectMemberRoleAssignment;
            })
            .map(projectMemberRoleAssignmentRepository::save)
            .map(savedProjectMemberRoleAssignment -> {
                projectMemberRoleAssignmentSearchRepository.save(savedProjectMemberRoleAssignment);

                return savedProjectMemberRoleAssignment;
            })
            .map(projectMemberRoleAssignmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberRoleAssignmentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectMemberRoleAssignments");
        return projectMemberRoleAssignmentRepository.findAll(pageable).map(projectMemberRoleAssignmentMapper::toDto);
    }

    public Page<ProjectMemberRoleAssignmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return projectMemberRoleAssignmentRepository.findAllWithEagerRelationships(pageable).map(projectMemberRoleAssignmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectMemberRoleAssignmentDTO> findOne(Long id) {
        log.debug("Request to get ProjectMemberRoleAssignment : {}", id);
        return projectMemberRoleAssignmentRepository.findOneWithEagerRelationships(id).map(projectMemberRoleAssignmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProjectMemberRoleAssignment : {}", id);
        projectMemberRoleAssignmentRepository.deleteById(id);
        projectMemberRoleAssignmentSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberRoleAssignmentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectMemberRoleAssignments for query {}", query);
        return projectMemberRoleAssignmentSearchRepository.search(query, pageable).map(projectMemberRoleAssignmentMapper::toDto);
    }
}
