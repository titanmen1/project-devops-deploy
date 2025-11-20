package io.hexlet.project_devops_deploy.dto;

import io.hexlet.project_devops_deploy.model.bulletin.BulletinState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulletinDto {

    private Long id;
    private String title;
    private String description;
    private BulletinState state;
    private String contact;
}
