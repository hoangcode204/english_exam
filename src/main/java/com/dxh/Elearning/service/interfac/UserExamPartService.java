package com.dxh.Elearning.service.interfac;

import com.dxh.Elearning.dto.request.SubmitRLPartRequest;
import com.dxh.Elearning.dto.request.UserExamRequest;
import com.dxh.Elearning.dto.response.SubmitRLPartResponse;
import com.dxh.Elearning.dto.response.UserExamPartResponse;
import com.dxh.Elearning.dto.response.UserExamResponse;

import java.util.List;

public interface UserExamPartService {

    SubmitRLPartResponse submitRLPart(SubmitRLPartRequest req);



}
