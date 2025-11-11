package com.dxh.Elearning.mapper;

import com.dxh.Elearning.dto.request.UserCreationRequest;
import com.dxh.Elearning.dto.response.UserOrderResponse;
import com.dxh.Elearning.dto.response.UserResponse;
import com.dxh.Elearning.dto.response.UserUpdateResponse;
import com.dxh.Elearning.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
    UserOrderResponse toUserOrderResponse(User user);
    UserUpdateResponse toUserUpdateResponse(User user);
}
