package org.mahu.proto.maven.mojo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// copied (modified) from: http://www.hascode.com/2013/07/embedding-jetty-or-tomcat-in-your-java-application/
public class ImageDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final static String MIME_PNG = "image/png";

	private static final Logger log = Logger
			.getLogger(ImageDownloadServlet.class.getName());

	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {
		// example requestURI: /app/image/image2.png
		// Example pathInfo: /image2.png
		String imageName = req.getPathInfo().substring(1);
		log.info("ImageDownloadServlet.execute, image: " + imageName);

		if (MIME_PNG.equals(req.getContentType())) {
			log.info("ImageDownloadServlet.execute, request contains not supported content-type: "
					+ req.getContentType());
			res.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			return;
		}
		OutputStream out = res.getOutputStream();
		byte[] fakePngBytes = new byte[10];
		out.write(fakePngBytes);

		res.setContentType(MIME_PNG);
		res.setContentLength(fakePngBytes.length);

		log.info("ImageDownloadServlet.execute, returned requested image");
	}
}