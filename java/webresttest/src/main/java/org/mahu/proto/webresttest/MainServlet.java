package org.mahu.proto.webresttest;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MainServlet", urlPatterns = { "/main" }, initParams = { @WebInitParam(name = "simpleParam", value = "paramValue") })
public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	//private static final String NL = "\n";

	protected static final Logger LOGGER = Logger.getLogger(MainServlet.class.getName());
	
	public MainServlet() {
		LOGGER.info("Starting the MainServlet");
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("main_page.html");
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}
