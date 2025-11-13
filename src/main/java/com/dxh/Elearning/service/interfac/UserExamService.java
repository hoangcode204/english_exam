package com.dxh.Elearning.service.interfac;

import com.dxh.Elearning.dto.request.UserExamRequest;
import com.dxh.Elearning.dto.response.UserExamResponse;

public interface UserExamService {

    UserExamResponse create(UserExamRequest req);
}
