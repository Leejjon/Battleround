package org.stofkat.battleround.server.captcha;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.stofkat.battleround.database.AuthenticationConnection;
import org.stofkat.battleround.database.DatabaseException;
import org.stofkat.battleround.server.security.EncryptionUtility;

import com.google.appengine.api.utils.SystemProperty;

/**
 * Servlet implementation class CaptchaServlet
 */
public class CaptchaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger("CaptchaServlet");
	public static final String NUMBER_OF_SHEEPS = "numberOfSheeps";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CaptchaServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession httpSession = request.getSession(true);
		boolean captchaAllowed = false;
		
		try {
			boolean productionMode = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
			AuthenticationConnection connection = new AuthenticationConnection(productionMode, true);
			captchaAllowed = connection.isAllowedToAttemptCaptcha(EncryptionUtility.getSHA512HashAsByteArray(request.getRemoteAddr()));
			connection.close();
		} catch (DatabaseException e) {
			log.log(Level.SEVERE, e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			log.log(Level.SEVERE, e.getMessage());
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
