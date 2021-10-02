package org.codingspiderfox.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.codingspiderfox.domain.ProjectMemberPermissionAssignment;
import org.codingspiderfox.repository.ProjectMemberPermissionAssignmentRepository;
import org.codingspiderfox.repository.ProjectMemberRepository;
import org.codingspiderfox.repository.search.ProjectMemberPermissionAssignmentSearchRepository;
import org.codingspiderfox.service.ProjectMemberPermissionAssignmentService;
import org.codingspiderfox.service.dto.ProjectMemberPermissionAssignmentDTO;
import org.codingspiderfox.service.mapper.ProjectMemberPermissionAssignmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProjectMemberPermissionAssignment}.
 */
@Service
@Transactional
public class ProjectMemberPermissionAssignmentServiceImpl implements ProjectMemberPermissionAssignmentService {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberPermissionAssignmentServiceImpl.class);

    private final ProjectMemberPermissionAssignmentRepository projectMemberPermissionAssignmentRepository;

    private final ProjectMemberPermissionAssignmentMapper projectMemberPermissionAssignmentMapper;

    private final ProjectMemberPermissionAssignmentSearchRepository projectMemberPermissionAssignmentSearchRepository;

    private final ProjectMemberRepository projectMemberRepository;

    public ProjectMemberPermissionAssignmentServiceImpl(
        ProjectMemberPermissionAssignmentRepository projectMemberPermissionAssignmentRepository,
        ProjectMemberPermissionAssignmentMapper projectMemberPermissionAssignmentMapper,
        ProjectMemberPermissionAssignmentSearchRepository projectMemberPermissionAssignmentSearchRepository,
        ProjectMemberRepository projectMemberRepository
    ) {
        this.projectMemberPermissionAssignmentRepository = projectMemberPermissionAssignmentRepository;
        this.projectMemberPermissionAssignmentMapper = projectMemberPermissionAssignmentMapper;
        this.projectMemberPermissionAssignmentSearchRepository = projectMemberPermissionAssignmentSearchRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    public ProjectMemberPermissionAssignmentDTO save(ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO) {
        log.debug("Request to save ProjectMemberPermissionAssignment : {}", projectMemberPermissionAssignmentDTO);
        ProjectMemberPermissionAssignment projectMemberPermissionAssignment = projectMemberPermissionAssignmentMapper.toEntity(
            projectMemberPermissionAssignmentDTO
        );
        Long projectMemberId = projectMemberPermissionAssignmentDTO.getProjectMember().getId();
        projectMemberRepository.findById(projectMemberId).ifPresent(projectMemberPermissionAssignment::projectMember);
        projectMemberPermissionAssignment = projectMemberPermissionAssignmentRepository.save(projectMemberPermissionAssignment);
        ProjectMemberPermissionAssignmentDTO result = projectMemberPermissionAssignmentMapper.toDto(projectMemberPermissionAssignment);
        projectMemberPermissionAssignmentSearchRepository.save(projectMemberPermissionAssignment);
        return result;
    }

    @Override
    public Optional<ProjectMemberPermissionAssignmentDTO> partialUpdate(
        ProjectMemberPermissionAssignmentDTO projectMemberPermissionAssignmentDTO
    ) {
        log.debug("Request to partially update ProjectMemberPermissionAssignment : {}", projectMemberPermissionAssignmentDTO);

        return projectMemberPermissionAssignmentRepository
            .findById(projectMemberPermissionAssignmentDTO.getId())
            .map(existingProjectMemberPermissionAssignment -> {
                projectMemberPermissionAssignmentMapper.partialUpdate(
                    existingProjectMemberPermissionAssignment,
                    projectMemberPermissionAssignmentDTO
                );

                return existingProjectMemberPermissionAssignment;
            })
            .map(projectMemberPermissionAssignmentRepository::save)
            .map(savedProjectMemberPermissionAssignment -> {
                projectMemberPermissionAssignmentSearchRepository.save(savedProjectMemberPermissionAssignment);

                return savedProjectMemberPermissionAssignment;
            })
            .map(projectMemberPermissionAssignmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberPermissionAssignmentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectMemberPermissionAssignments");
        return projectMemberPermissionAssignmentRepository.findAll(pageable).map(projectMemberPermissionAssignmentMapper::toDto);
    }

    public Page<ProjectMemberPermissionAssignmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return projectMemberPermissionAssignmentRepository
            .findAllWithEagerRelationships(pageable)
            .map(projectMemberPermissionAssignmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectMemberPermissionAssignmentDTO> findOne(Long id) {
        log.debug("Request to get ProjectMemberPermissionAssignment : {}", id);
        return projectMemberPermissionAssignmentRepository
            .findOneWithEagerRelationships(id)
            .map(projectMemberPermissionAssignmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProjectMemberPermissionAssignment : {}", id);
        projectMemberPermissionAssignmentRepository.deleteById(id);
        projectMemberPermissionAssignmentSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberPermissionAssignmentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectMemberPermissionAssignments for query {}", query);
        return projectMemberPermissionAssignmentSearchRepository
            .search(query, pageable)
            .map(projectMemberPermissionAssignmentMapper::toDto);
    }
}
