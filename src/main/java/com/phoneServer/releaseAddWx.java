package com.phoneServer;

import com.common.InOutLog;
import com.common.utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet({"/api/phoneServer/releaseAddWx"})
public class releaseAddWx extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = utils.getConnection();
            if (utils.snHttpTimeMap.containsKey(request.getParameter("sn"))) {
                utils.snHttpTimeMap.put(request.getParameter("sn"), utils.getCurrentTimeStr());
//                stmt = conn.prepareStatement("select customer from addWx where phone = ?");
//                stmt.setString(1, request.getParameter("addWx"));
//                ResultSet res = stmt.executeQuery();
//                res.next();
//                String customer = res.getString("customer");
                stmt = conn.prepareStatement("update addWx set isUse = 0, loginWx = '' where phone = ?");
                stmt.setString(1, request.getParameter("addWx"));
                stmt.executeUpdate();
//                stmt.execute("update customer set addNum = addNum - 1 where name = '" + customer + "'");
                stmt.execute("update loginWx set addNum = addNum - 1 where wxid = '" + request.getParameter("loginWx") + "'");
                resJo.put("res", "success");
            } else {
                resJo.put("errInfo", "noSn" + request.getParameter("sn"));
                resJo.put("res", "fail");
            }
            if (stmt != null) {
                try {
                    stmt.execute("unlock tables");
                    stmt.close();
                } catch (Exception e) {
                }
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            resJo.put("errInfo", utils.getExceptionMsg(e2));
            resJo.put("res", "fail");
            if (stmt != null) {
                try {
                    stmt.execute("unlock tables");
                    stmt.close();
                } catch (Exception e3) {
                }
            }
        } catch (Throwable th) {
            if (stmt != null) {
                try {
                    stmt.execute("unlock tables");
                    stmt.close();
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