package org.codingspiderfox.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.codingspiderfox.domain.ProjectSettings;
import org.codingspiderfox.repository.ProjectRepository;
import org.codingspiderfox.repository.ProjectSettingsRepository;
import org.codingspiderfox.repository.search.ProjectSettingsSearchRepository;
import org.codingspiderfox.service.ProjectSettingsService;
import org.codingspiderfox.service.dto.ProjectSettingsDTO;
import org.codingspiderfox.service.mapper.ProjectSettingsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProjectSettings}.
 */
@Service
@Transactional
public class ProjectSettingsServiceImpl implements ProjectSettingsService {

    private final Logger log = LoggerFactory.getLogger(ProjectSettingsServiceImpl.class);

    private final ProjectSettingsRepository projectSettingsRepository;

    private final ProjectSettingsMapper projectSettingsMapper;

    private final ProjectSettingsSearchRepository projectSettingsSearchRepository;

    private final ProjectRepository projectRepository;

    public ProjectSettingsServiceImpl(
        ProjectSettingsRepository projectSettingsRepository,
        ProjectSettingsMapper projectSettingsMapper,
        ProjectSettingsSearchRepository projectSettingsSearchRepository,
        ProjectRepository projectRepository
    ) {
        this.projectSettingsRepository = projectSettingsRepository;
        this.projectSettingsMapper = projectSettingsMapper;
        this.projectSettingsSearchRepository = projectSettingsSearchRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public ProjectSettingsDTO save(ProjectSettingsDTO projectSettingsDTO) {
        log.debug("Request to save ProjectSettings : {}", projectSettingsDTO);
        ProjectSettings projectSettings = projectSettingsMapper.toEntity(projectSettingsDTO);
        Long projectId = projectSettingsDTO.getProject().getId();
        projectRepository.findById(projectId).ifPresent(projectSettings::project);
        projectSettings = projectSettingsRepository.save(projectSettings);
        ProjectSettingsDTO result = projectSettingsMapper.toDto(projectSettings);
        projectSettingsSearchRepository.save(projectSettings);
        return result;
    }

    @Override
    public Optional<ProjectSettingsDTO> partialUpdate(ProjectSettingsDTO projectSettingsDTO) {
        log.debug("Request to partially update ProjectSettings : {}", projectSettingsDTO);

        return projectSettingsRepository
            .findById(projectSettingsDTO.getId())
            .map(existingProjectSettings -> {
                projectSettingsMapper.partialUpdate(existingProjectSettings, projectSettingsDTO);

                return existingProjectSettings;
            })
            .map(projectSettingsRepository::save)
            .map(savedProjectSettings -> {
                projectSettingsSearchRepository.save(savedProjectSettings);

                return savedProjectSettings;
            })
            .map(projectSettingsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectSettingsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectSettings");
        return projectSettingsRepository.findAll(pageable).map(projectSettingsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectSettingsDTO> findOne(Long id) {
        log.debug("Request to get ProjectSettings : {}", id);
        return projectSettingsRepository.findById(id).map(projectSettingsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProjectSettings : {}", id);
        projectSettingsRepository.deleteById(id);
        projectSettingsSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectSettingsDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectSettings for query {}", query);
        return projectSettingsSearchRepository.search(query, pageable).map(projectSettingsMapper::toDto);
    }
}
