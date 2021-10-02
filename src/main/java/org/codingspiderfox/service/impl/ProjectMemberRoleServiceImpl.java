package org.codingspiderfox.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.codingspiderfox.domain.ProjectMemberRole;
import org.codingspiderfox.repository.ProjectMemberRoleRepository;
import org.codingspiderfox.repository.search.ProjectMemberRoleSearchRepository;
import org.codingspiderfox.service.ProjectMemberRoleService;
import org.codingspiderfox.service.dto.ProjectMemberRoleDTO;
import org.codingspiderfox.service.mapper.ProjectMemberRoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProjectMemberRole}.
 */
@Service
@Transactional
public class ProjectMemberRoleServiceImpl implements ProjectMemberRoleService {

    private final Logger log = LoggerFactory.getLogger(ProjectMemberRoleServiceImpl.class);

    private final ProjectMemberRoleRepository projectMemberRoleRepository;

    private final ProjectMemberRoleMapper projectMemberRoleMapper;

    private final ProjectMemberRoleSearchRepository projectMemberRoleSearchRepository;

    public ProjectMemberRoleServiceImpl(
        ProjectMemberRoleRepository projectMemberRoleRepository,
        ProjectMemberRoleMapper projectMemberRoleMapper,
        ProjectMemberRoleSearchRepository projectMemberRoleSearchRepository
    ) {
        this.projectMemberRoleRepository = projectMemberRoleRepository;
        this.projectMemberRoleMapper = projectMemberRoleMapper;
        this.projectMemberRoleSearchRepository = projectMemberRoleSearchRepository;
    }

    @Override
    public ProjectMemberRoleDTO save(ProjectMemberRoleDTO projectMemberRoleDTO) {
        log.debug("Request to save ProjectMemberRole : {}", projectMemberRoleDTO);
        ProjectMemberRole projectMemberRole = projectMemberRoleMapper.toEntity(projectMemberRoleDTO);
        projectMemberRole = projectMemberRoleRepository.save(projectMemberRole);
        ProjectMemberRoleDTO result = projectMemberRoleMapper.toDto(projectMemberRole);
        projectMemberRoleSearchRepository.save(projectMemberRole);
        return result;
    }

    @Override
    public Optional<ProjectMemberRoleDTO> partialUpdate(ProjectMemberRoleDTO projectMemberRoleDTO) {
        log.debug("Request to partially update ProjectMemberRole : {}", projectMemberRoleDTO);

        return projectMemberRoleRepository
            .findById(projectMemberRoleDTO.getId())
            .map(existingProjectMemberRole -> {
                projectMemberRoleMapper.partialUpdate(existingProjectMemberRole, projectMemberRoleDTO);

                return existingProjectMemberRole;
            })
            .map(projectMemberRoleRepository::save)
            .map(savedProjectMemberRole -> {
                projectMemberRoleSearchRepository.save(savedProjectMemberRole);

                return savedProjectMemberRole;
            })
            .map(projectMemberRoleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberRoleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectMemberRoles");
        return projectMemberRoleRepository.findAll(pageable).map(projectMemberRoleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectMemberRoleDTO> findOne(Long id) {
        log.debug("Request to get ProjectMemberRole : {}", id);
        return projectMemberRoleRepository.findById(id).map(projectMemberRoleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProjectMemberRole : {}", id);
        projectMemberRoleRepository.deleteById(id);
        projectMemberRoleSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberRoleDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectMemberRoles for query {}", query);
        return projectMemberRoleSearchRepository.search(query, pageable).map(projectMemberRoleMapper::toDto);
    }
}
