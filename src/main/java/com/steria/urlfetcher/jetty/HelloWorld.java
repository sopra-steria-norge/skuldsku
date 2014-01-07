package com.steria.urlfetcher.jetty;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Simple example of how to wire up the filter when using embedded jetty
 * @author ofriberg
 *
 */
public class HelloWorld
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        ServletHandler handler = new ServletHandler();   
        server.setHandler(handler);

        handler.addServletWithMapping(HelloServlet.class, "/*");
        handler.addFilterWithMapping(ResponseLogger.class, "/*", EnumSet.of(DispatcherType.REQUEST));

        server.start();
        server.join();

    }
    
    /**
     * Sample servlet
     *
     */
    public static class HelloServlet extends HttpServlet {
      private static final long serialVersionUID = 1L;

      @Override
      protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello SimpleServlet</h1>");
      }
    }
}

