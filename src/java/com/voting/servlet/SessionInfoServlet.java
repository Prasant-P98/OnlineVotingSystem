package com.voting.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@WebServlet("/SessionInfoServlet")
public class SessionInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Session Diagnostics</title>");
        out.println("<link rel='stylesheet' type='text/css' href='style.css'></head><body>");
        out.println("<div class='container'>");
        out.println("<h2>HttpSession Diagnostics</h2>");

        if (session == null) {
            out.println("<p>No active session found.</p>");
        } else {
            Boolean hasVoted = (Boolean) session.getAttribute("hasVoted");

            out.println("<table border='1' class='session-table'>");
            out.println("<tr><th>Attribute</th><th>Value</th></tr>");
            out.println("<tr><td>Session ID</td><td>" + session.getId() + "</td></tr>");
            out.println("<tr><td>Is New Session?</td><td>" + session.isNew() + "</td></tr>");
            out.println("<tr><td>Creation Time</td><td>" + new Date(session.getCreationTime()) + "</td></tr>");
            out.println("<tr><td>Last Accessed Time</td><td>" + new Date(session.getLastAccessedTime()) + "</td></tr>");
            out.println("<tr><td>hasVoted Attribute</td><td>" + (hasVoted != null && hasVoted) + "</td></tr>");
            out.println("</table>");
        }

        out.println("<br><a href='index.html' class='btn'>Back to Voting</a> ");
        out.println("<a href='ResultServlet' class='btn btn-secondary'>View Results</a>");
        out.println("</div></body></html>");
    }
}