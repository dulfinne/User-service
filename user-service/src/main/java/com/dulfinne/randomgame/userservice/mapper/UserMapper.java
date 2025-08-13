package com.dulfinne.randomgame.userservice.mapper;

import com.dulfinne.randomgame.userservice.dto.request.UserRequest;
import com.dulfinne.randomgame.userservice.dto.response.UserResponse;
import com.dulfinne.randomgame.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

  @Mapping(target = "balance", expression = "java(setScale(user.getBalance()))")
  UserResponse toResponse(User user);

  User toEntity(UserRequest request);

  void updateUser(UserRequest request, @MappingTarget User user);

  default BigDecimal setScale(BigDecimal value) {
    return value != null ? value.setScale(2, RoundingMode.HALF_EVEN) : null;
  }
}
