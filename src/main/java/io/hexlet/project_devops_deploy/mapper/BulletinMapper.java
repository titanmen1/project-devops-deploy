package io.hexlet.project_devops_deploy.mapper;

import io.hexlet.project_devops_deploy.dto.BulletinDto;
import io.hexlet.project_devops_deploy.dto.BulletinRequest;
import io.hexlet.project_devops_deploy.model.Bulletin;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BulletinMapper {

    BulletinDto toDto(Bulletin entity);

    Bulletin toEntity(BulletinDto dto);

    Bulletin toEntity(BulletinRequest request);

    void updateEntity(BulletinRequest request, @MappingTarget Bulletin bulletin);
}
