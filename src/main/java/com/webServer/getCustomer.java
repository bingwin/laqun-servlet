package com.webServer;

import com.common.config;
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
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet({"/api/webServer/getCustomer"})
public class getCustomer extends HttpServlet {
    /* Access modifiers changed, original: protected */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter pw = response.getWriter();
        JSONObject resJo = new JSONObject();
        HttpSession session = request.getSession();
        if (session.getAttribute("loginPassword") == null || !session.getAttribute("loginPassword").equals(config.get("loginPassword"))) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "请先登录");
            pw.println(resJo);
            return;
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = utils.getConnection();
            stmt = conn.prepareStatement("select * from customer where name like ? limit ?, ?");
            stmt.setString(1, "%" + request.getParameter("customerName") + "%");
            stmt.setInt(2, (Integer.valueOf(request.getParameter("page"))-1)*10);
            stmt.setInt(3, Integer.valueOf(request.getParameter("pageSize")));
            ResultSet res = stmt.executeQuery();
            JSONArray ja = new JSONArray();
            while (res.next()) {
                JSONObject jo = new JSONObject();
                jo.put("name", res.getString("name"));
                jo.put("addNum", res.getString("addNum"));
                jo.put("friendNum", res.getString("friendNum"));
                jo.put("laNum", res.getString("laNum"));
                jo.put("oddNum", res.getInt("oddNum") - res.getInt("addNum"));
                jo.put("id", res.getString("id"));
                jo.put("addTime", res.getString("addTime"));
                ja.put(jo);
            }
            resJo.put("res", "success");
            resJo.put("data", ja);
            stmt = conn.prepareStatement("select count(*) as total from customer where name like ?");
            stmt.setString(1, "%" + request.getParameter("customerName") + "%");
            res = stmt.executeQuery();
            if (res.next()) {
                resJo.put("total", res.getInt("total"));
            }else{
                resJo.put("total", 0);
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
            resJo.put("res", "fail");
            resJo.put("errInfo", e2.getMessage());
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
        pw.println(resJo);
        pw.close();
    }

    /* Access modifiers changed, original: protected */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}