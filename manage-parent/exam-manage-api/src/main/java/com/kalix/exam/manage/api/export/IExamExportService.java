package com.kalix.exam.manage.api.export;


import com.kalix.framework.core.api.IService;

import javax.servlet.http.HttpServletResponse;

public interface IExamExportService extends IService {

    void doExport(String jsonStr, HttpServletResponse response);
}
