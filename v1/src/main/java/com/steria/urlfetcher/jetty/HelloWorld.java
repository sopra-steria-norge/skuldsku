package com.steria.urlfetcher.jetty;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Simple example of how to wire up the filter when using embedded jetty
 *
 */
public class HelloWorld
{
    public static void main(String[] args) throws Exception
    {
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(HelloServlet.class, "/*");
        handler.addFilterWithMapping(ResponseFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

        SessionHandler sh = new SessionHandler();
        sh.setHandler(handler);
        
        Server server = new Server(8080);
        server.setHandler(sh);

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
        doPost(request, response);
      }
      
      @Override
      protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
      	response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        HttpSession session = request.getSession();
        System.out.print(session.getId());
        
      	Cookie[] cookies = request.getCookies();
      	if(cookies == null || cookies.length == 0){
      		System.out.println("got no cookies, adding one");
      		Cookie cookie = new Cookie("color", "cyan");
      		cookie.setMaxAge(24*60*60);
      		cookie.setPath("/");
      		response.addCookie(cookie);
      		Cookie cookie2 = new Cookie("color2", "black");
      		cookie2.setMaxAge(24*60*60);
      		cookie2.setPath("/");
      		response.addCookie(cookie2);
      	}else{
      		System.out.println("got cookies: "+cookies.length);
      	}
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello SimpleServlet</h1>");
        
      }
    }
}

