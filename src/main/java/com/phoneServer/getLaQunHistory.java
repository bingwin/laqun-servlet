package com.phoneServer;

import com.common.InOutLog;
import com.common.config;
import com.common.utils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet({"/api/phoneServer/getLaQunHistory"})
public class getLaQunHistory extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            String[] wxidArr= request.getParameter("wxidlist").split(",");
            conn = utils.getConnection();
            if (utils.snHttpTimeMap.containsKey(request.getParameter("sn"))) {
                utils.snHttpTimeMap.put(request.getParameter("sn"), utils.getCurrentTimeStr());
                JSONArray ja = new JSONArray();
                for (int i=0; i<wxidArr.length; i++) {
                    stmt = conn.prepareStatement("select laNum from laQunHistory where wxid = ? limit 1");
                    stmt.setString(1, wxidArr[i]);
                    res = stmt.executeQuery();
                    JSONObject jo = new JSONObject();
                    if(res.next()) {
                        jo.put(wxidArr[i], res.getString("laNum"));
                    }else{
                        jo.put(wxidArr[i], 0);
                    }
                    ja.put(jo);
                }
                resJo.put("res", "success");
                resJo.put("data", ja);
            }else{
                resJo.put("errInfo", "noSn" + request.getParameter("sn"));
                resJo.put("res", "fail");
            }


            res.close();
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
        InOutLog.logInOut(request, resJo);
        pw.println(resJo);
        pw.close();
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}