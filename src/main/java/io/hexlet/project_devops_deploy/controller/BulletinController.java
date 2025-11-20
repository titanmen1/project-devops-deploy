package io.hexlet.project_devops_deploy.controller;

import io.hexlet.project_devops_deploy.dto.BulletinDto;
import io.hexlet.project_devops_deploy.dto.BulletinRequest;
import io.hexlet.project_devops_deploy.service.BulletinService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BulletinController {

    @Autowired
    private BulletinService service;

    @PostMapping("/bulletins")
    @ResponseStatus(HttpStatus.CREATED)
    public BulletinDto create(@Valid @RequestBody BulletinRequest request) {
        return service.create(request);
    }

    @GetMapping("/bulletins")
    public List<BulletinDto> index() {
        return service.findAll();
    }

    @GetMapping("/bulletins/{id}")
    public BulletinDto show(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/bulletins/{id}")
    public BulletinDto update(@PathVariable Long id, @Valid @RequestBody BulletinRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/bulletins/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
