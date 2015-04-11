package org.stofkat.battleround.server.captcha;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.stofkat.battleround.database.AuthenticationConnection;
import org.stofkat.battleround.database.DatabaseConnection;
import org.stofkat.battleround.database.DatabaseException;
import org.stofkat.battleround.server.DatabaseInformationObtainer;
import org.stofkat.battleround.server.security.EncryptionUtility;

/**
 * Servlet implementation class CaptchaServlet
 */
public class CaptchaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CaptchaServlet.class); 
	public static final String NUMBER_OF_SHEEPS = "numberOfSheeps";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CaptchaServlet() {
		super();
		BasicConfigurator.configure();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession httpSession = request.getSession(true);
		boolean captchaAllowed = false;
		
		try {
			AuthenticationConnection connection = new AuthenticationConnection(DatabaseInformationObtainer.getDbInfo(this.getServletContext()), true);
			captchaAllowed = connection.isAllowedToAttemptCaptcha(EncryptionUtility.getSHA512HashAsByteArray(request.getRemoteAddr()));
			connection.close();
		} catch (DatabaseException e) {
			log.error(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
		}
		
		response.setContentType(Captcha.mimeType);
		
		// Create the captcha.
		Captcha newCaptcha = new Captcha(captchaAllowed);
		
		OutputStream out = response.getOutputStream();
		newCaptcha.getCaptchaImageFile(out);
		out.close();
		
		if (captchaAllowed) {
			httpSession.setAttribute(NUMBER_OF_SHEEPS, new Integer(newCaptcha.getNumberOfSheeps()));
		}
	}
}
