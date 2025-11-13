package com.dxh.Elearning.service.interfac;

import com.dxh.Elearning.dto.request.SubmitRLPartRequest;
import com.dxh.Elearning.dto.request.UserExamRequest;
import com.dxh.Elearning.dto.response.SubmitRLPartResponse;
import com.dxh.Elearning.dto.response.UserExamResponse;

public interface UserExamPartService {

    SubmitRLPartResponse submitRLPart(SubmitRLPartRequest req);
}
