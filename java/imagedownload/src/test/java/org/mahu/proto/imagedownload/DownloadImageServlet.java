package org.mahu.proto.imagedownload;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// copied (modified) from: http://www.hascode.com/2013/07/embedding-jetty-or-tomcat-in-your-java-application/
public class DownloadImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		System.out.println("pathInfo: " + pathInfo);
		res.setStatus(500);
	}
}