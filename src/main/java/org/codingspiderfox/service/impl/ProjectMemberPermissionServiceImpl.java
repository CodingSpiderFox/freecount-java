package org.codingspiderfox.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.codingspiderfox.domain.ProjectMemberPermission;
import org.codingspiderfox.repository.ProjectMemberPermissionRepository;
import org.codingspiderfox.repository.search.ProjectMemberPermissionSearchRepository;
import org.codingspiderfox.service.ProjectMemberPermissionService;
import org.codingspiderfox.service.dto.ProjectMemberPermissionDTO;
import org.codingspiderfox.service.mapper.ProjectMemberPermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProjectMemberPermission}.
 */
@Service
@Transactional
public class ProjectMemberPermissionServiceImpl implements ProjectMemberPermissionService {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberPermissionServiceImpl.class);

    private final ProjectMemberPermissionRepository projectMemberPermissionRepository;

    private final ProjectMemberPermissionMapper projectMemberPermissionMapper;

    private final ProjectMemberPermissionSearchRepository projectMemberPermissionSearchRepository;

    public ProjectMemberPermissionServiceImpl(
        ProjectMemberPermissionRepository projectMemberPermissionRepository,
        ProjectMemberPermissionMapper projectMemberPermissionMapper,
        ProjectMemberPermissionSearchRepository projectMemberPermissionSearchRepository
    ) {
        this.projectMemberPermissionRepository = projectMemberPermissionRepository;
        this.projectMemberPermissionMapper = projectMemberPermissionMapper;
        this.projectMemberPermissionSearchRepository = projectMemberPermissionSearchRepository;
    }

    @Override
    public ProjectMemberPermissionDTO save(ProjectMemberPermissionDTO projectMemberPermissionDTO) {
        log.debug("Request to save ProjectMemberPermission : {}", projectMemberPermissionDTO);
        ProjectMemberPermission projectMemberPermission = projectMemberPermissionMapper.toEntity(projectMemberPermissionDTO);
        projectMemberPermission = projectMemberPermissionRepository.save(projectMemberPermission);
        ProjectMemberPermissionDTO result = projectMemberPermissionMapper.toDto(projectMemberPermission);
        projectMemberPermissionSearchRepository.save(projectMemberPermission);
        return result;
    }

    @Override
    public Optional<ProjectMemberPermissionDTO> partialUpdate(ProjectMemberPermissionDTO projectMemberPermissionDTO) {
        log.debug("Request to partially update ProjectMemberPermission : {}", projectMemberPermissionDTO);

        return projectMemberPermissionRepository
            .findById(projectMemberPermissionDTO.getId())
            .map(existingProjectMemberPermission -> {
                projectMemberPermissionMapper.partialUpdate(existingProjectMemberPermission, projectMemberPermissionDTO);

                return existingProjectMemberPermission;
            })
            .map(projectMemberPermissionRepository::save)
            .map(savedProjectMemberPermission -> {
                projectMemberPermissionSearchRepository.save(savedProjectMemberPermission);

                return savedProjectMemberPermission;
            })
            .map(projectMemberPermissionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberPermissionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectMemberPermissions");
        return projectMemberPermissionRepository.findAll(pageable).map(projectMemberPermissionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectMemberPermissionDTO> findOne(Long id) {
        log.debug("Request to get ProjectMemberPermission : {}", id);
        return projectMemberPermissionRepository.findById(id).map(projectMemberPermissionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProjectMemberPermission : {}", id);
        projectMemberPermissionRepository.deleteById(id);
        projectMemberPermissionSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberPermissionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectMemberPermissions for query {}", query);
        return projectMemberPermissionSearchRepository.search(query, pageable).map(projectMemberPermissionMapper::toDto);
    }
}
