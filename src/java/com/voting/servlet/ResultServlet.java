package com.voting.servlet;

import com.voting.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/ResultServlet")
public class ResultServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String query = "SELECT option_name, vote_count FROM options ORDER BY vote_count DESC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int totalVotes = 0;
            while (rs.next()) {
                totalVotes += rs.getInt("vote_count");
            }

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Live Voting Results</title>");
            out.println("<link rel='stylesheet' type='text/css' href='style.css'></head><body>");
            out.println("<div class='container results-container'>");
            out.println("<h2>Live Poll Results</h2>");
            out.println("<p><strong>Total Votes Cast:</strong> " + totalVotes + "</p>");

            if (totalVotes == 0) {
                out.println("<p>No votes have been submitted yet.</p>");
            } else {
                try (ResultSet rsResults = stmt.executeQuery(query)) {
                    while (rsResults.next()) {
                        String name = rsResults.getString("option_name");
                        int votes = rsResults.getInt("vote_count");
                        double percentage = (totalVotes > 0) ? ((double) votes / totalVotes) * 100 : 0;

                        out.println("<div class='result-row'>");
                        out.println("<div class='label-info'><span>" + name + "</span> <span>" + votes + " vote(s) (" + String.format("%.1f", percentage) + "%)</span></div>");
                        out.println("<div class='bar-bg'>");
                        out.println("<div class='bar-fill' style='width: " + percentage + "%;'></div>");
                        out.println("</div></div>");
                    }
                }
            }

            out.println("<br>");
            out.println("<a href='index.html' class='btn'>Back to Voting</a> ");
            out.println("<a href='SessionInfoServlet' class='btn btn-secondary'>View Session Info</a>");
            out.println("</div></body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error fetching results: " + e.getMessage() + "</p>");
        }
    }
}