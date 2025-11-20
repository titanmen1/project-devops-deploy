package io.hexlet.project_devops_deploy.service;

import io.hexlet.project_devops_deploy.dto.BulletinDto;
import io.hexlet.project_devops_deploy.dto.BulletinRequest;
import io.hexlet.project_devops_deploy.exception.ResourceNotFoundException;
import io.hexlet.project_devops_deploy.mapper.BulletinMapper;
import io.hexlet.project_devops_deploy.model.Bulletin;
import io.hexlet.project_devops_deploy.repository.BulletinRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BulletinService {

    @Autowired
    private final BulletinRepository repository;

    @Autowired
    private final BulletinMapper mapper;

    public BulletinDto create(BulletinRequest request) {
        Bulletin bulletin = mapper.toEntity(request);
        return mapper.toDto(repository.save(bulletin));
    }

    @Transactional(readOnly = true)
    public List<BulletinDto> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public BulletinDto findById(Long id) {
        return mapper.toDto(getBulletin(id));
    }

    private Bulletin getBulletin(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bulletin %d not found".formatted(id)));
    }

    public BulletinDto update(Long id, BulletinRequest request) {
        Bulletin bulletin = getBulletin(id);
        mapper.updateEntity(request, bulletin);
        return mapper.toDto(repository.save(bulletin));
    }

    public void delete(Long id) {
        Bulletin bulletin = getBulletin(id);
        repository.delete(bulletin);
    }
}
