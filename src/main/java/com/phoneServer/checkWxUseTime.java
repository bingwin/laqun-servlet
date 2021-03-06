package com.phoneServer;

import com.common.InOutLog;
import com.common.config;
import com.common.utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet({"/api/phoneServer/checkWxUseTime"})
public class checkWxUseTime extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        String wxid = request.getParameter("wxid");
        String sn = request.getParameter("sn");
        try {
            conn = utils.getConnection();
            if (utils.snHttpTimeMap.containsKey(sn)) {
                utils.snHttpTimeMap.put(sn, utils.getCurrentTimeStr());
                stmt = conn.prepareStatement("select * from loginWx where wxid = ? limit 1");
                stmt.setString(1, wxid);
                ResultSet res = stmt.executeQuery();
                if (res.next()) {
                    JSONObject wxJo = new JSONObject();
                    if ((System.currentTimeMillis() / 1000) - ((long) (Integer.valueOf(config.get("loginWxUseTime")).intValue() * 60)) > ((long) res.getInt("lastGetTime"))) {
                        stmt = conn.prepareStatement("update loginWx set lastGetTime = ? where wxid = ? limit 1");
                        stmt.setInt(1, (int) (System.currentTimeMillis() / 1000));
                        stmt.setString(2, request.getParameter("wxid"));
                        stmt.executeUpdate();
                        resJo.put("res", "success");
                    } else {
                        resJo.put("errInfo", "时间未超过设置的登录微信使用时间");
                        resJo.put("res", "fail");
                    }
                } else {
                    resJo.put("errInfo", "没有这个微信");
                    resJo.put("res", "fail");
                }
            } else {
                resJo.put("errInfo", "sn不存在： " + request.getParameter("sn"));
                resJo.put("res", "fail");
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                }
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e2) {
            resJo.put("errInfo", utils.getExceptionMsg(e2));
            resJo.put("res", "fail");
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e3) {
                }
            }
        } catch (Throwable th) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e4) {
                }
            }
        }
        pw.println(resJo.toString());
        pw.close();
        InOutLog.logInOut(request, resJo);
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}