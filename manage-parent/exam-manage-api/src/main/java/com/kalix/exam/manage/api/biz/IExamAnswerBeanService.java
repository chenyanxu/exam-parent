package com.kalix.exam.manage.api.biz;

import com.kalix.exam.manage.dto.ExamQuesAttachmentDto;
import com.kalix.exam.manage.dto.ExamingDto;
import com.kalix.exam.manage.dto.PaperQuesAnswerDto;
import com.kalix.exam.manage.entities.ExamAnswerBean;
import com.kalix.framework.core.api.biz.IBizService;
import com.kalix.framework.core.api.persistence.JsonData;
import com.kalix.framework.core.api.persistence.JsonStatus;

import java.util.List;
import java.util.Map;

public interface IExamAnswerBeanService extends IBizService<ExamAnswerBean> {
    /**
     * 生成考卷
     * @param paperId
     * @param examId
     * @return
     */
    Map<String, Object> getExamingPaper(Long paperId, Long examId);

    /**
     * 获取预先生成的考卷
     * @param paperId
     * @param examId
     * @return
     */
    Map<String, Object> getPerCreateExamingPaper(Long paperId, Long examId);

    /**
     * 考试提交
     * @param examingDto
     * @return
     */
    JsonStatus commitExaming(ExamingDto examingDto);

    /**
     * 获取考生一次考试的所有答题列表
     * @param examId
     * @param paperId
     * @param userId
     * @return
     */
    List<ExamAnswerBean> getExamUserAnswer(Long examId, Long paperId, Long userId);

    /**
     * 获取考卷题列表及考试成绩
     * @return
     */
    List<PaperQuesAnswerDto> getPaperQuesAnswerList(Long examId, Long paperId);

    /**
     * 获取考题素材上传列表
     * @param name
     * @param subjectVal
     * @return
     */
    List<ExamQuesAttachmentDto> getQuesAnswerMaterial(String name, String subjectVal);

    /**
     * 获取下系统时间(考试时前端每个10分钟获取一次，防止session超时)
     * @param examId
     * @return
     */
    JsonData checkExamingTime(Long examId);
}
