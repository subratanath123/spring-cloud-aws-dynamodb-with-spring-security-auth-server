package com.authorization.server.mapper;

import com.authorization.server.entity.User;
import com.authorization.server.view.UserView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class DtoMapperService {

    private DtoMapperService mapper = Mappers.getMapper(DtoMapperService.class);

    public abstract UserView sourceToDestination(User source);
}