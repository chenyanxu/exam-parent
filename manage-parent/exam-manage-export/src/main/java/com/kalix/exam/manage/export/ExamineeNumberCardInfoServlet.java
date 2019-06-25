package com.kalix.exam.manage.export;

import com.kalix.exam.manage.api.export.IExamineeNumberCardInfoService;
import com.kalix.framework.core.util.JNDIHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ExamineeNumberCardInfoServlet extends HttpServlet {
    private IExamineeNumberCardInfoService examineeNumberCardInfoService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OutputStream out = null;
        PrintWriter outHtml = null;
        try {
            // 实体名称
            String jsonStr = req.getParameter("jsonStr") == null ? "" : req.getParameter("jsonStr");
            if (jsonStr != null && !jsonStr.isEmpty()) {
                examineeNumberCardInfoService = JNDIHelper.getJNDIServiceForName(IExamineeNumberCardInfoService.class.getName());
                examineeNumberCardInfoService.doExport(jsonStr, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (outHtml != null) {
                outHtml.flush();
                outHtml.close();
            }
        }
    }
}
