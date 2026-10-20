package com.voting.servlet;

import com.voting.util.DBUtil;

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

@WebServlet("/VoteServlet")
public class VoteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("index.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(true);

        Boolean hasVoted = (Boolean) session.getAttribute("hasVoted");
        if (hasVoted != null && hasVoted) {
            renderMessagePage(out, "Duplicate Vote", 
                "You have already cast your vote in this session! Duplicate votes are prevented.", 
                "ResultServlet", "View Live Results");
            return;
        }

        String selectedSubject = request.getParameter("subject");

        if (selectedSubject == null || selectedSubject.trim().isEmpty()) {
            renderMessagePage(out, "Invalid Selection", 
                "Please select an option before submitting.", 
                "index.html", "Go Back");
            return;
        }

        String updateSql = "UPDATE options SET vote_count = vote_count + 1 WHERE option_name = ?";
        String logSql = "INSERT INTO vote_log (session_id, option_id) SELECT ?, option_id FROM options WHERE option_name = ?";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateSql)) {
                pstmtUpdate.setString(1, selectedSubject);
                int rowsUpdated = pstmtUpdate.executeUpdate();

                if (rowsUpdated > 0) {
                    try (PreparedStatement pstmtLog = conn.prepareStatement(logSql)) {
                        pstmtLog.setString(1, session.getId());
                        pstmtLog.setString(2, selectedSubject);
                        pstmtLog.executeUpdate();
                    }

                    conn.commit();
                    session.setAttribute("hasVoted", true);

                    renderMessagePage(out, "Vote Recorded", 
                        "Thank you! Your vote for <strong>" + selectedSubject + "</strong> has been registered successfully.", 
                        "ResultServlet", "View Live Results");
                } else {
                    conn.rollback();
                    renderMessagePage(out, "Error", 
                        "Selected option was not found in database.", 
                        "index.html", "Try Again");
                }
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderMessagePage(out, "Database Error", 
                "An error occurred while casting your vote: " + e.getMessage(), 
                "index.html", "Try Again");
        }
    }

    private void renderMessagePage(PrintWriter out, String title, String message, String targetUrl, String buttonText) {
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>" + title + "</title>");
        out.println("<link rel='stylesheet' type='text/css' href='style.css'></head><body>");
        out.println("<div class='container'>");
        out.println("<h2>" + title + "</h2>");
        out.println("<p>" + message + "</p>");
        out.println("<a href='" + targetUrl + "' class='btn'>" + buttonText + "</a>");
        out.println("</div></body></html>");
    }
}