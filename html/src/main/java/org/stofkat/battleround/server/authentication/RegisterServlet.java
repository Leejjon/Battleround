package org.stofkat.battleround.server.authentication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.stofkat.battleround.database.AuthenticationConnection;
import org.stofkat.battleround.database.DatabaseConnection;
import org.stofkat.battleround.database.DatabaseException;
import org.stofkat.battleround.database.security.ValidationException;
import org.stofkat.battleround.server.captcha.CaptchaServlet;
import org.stofkat.battleround.server.security.EncryptionUtility;
import org.stofkat.battleround.common.resources.Resources;

/**
 * Servlet implementation class RegisterServlet
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger(RegisterServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AuthenticationConnection authenticationConnection = null;
		ServletOutputStream out = null;
		try {
			// Retrieve the ip address of the request.
			byte[] clientIpAddressHash = EncryptionUtility.getSHA512HashAsByteArray(request.getRemoteAddr());
			
			// Set the content type of our response.
			response.setContentType("text/html");
			
			// Get the output stream of the response so we can put our response text in it.
			out = response.getOutputStream();
			
			// Get the HTTP session, in which the answer to the captcha is stored.
			HttpSession httpSession = request.getSession(true);
			
			// Retrieve the captcha answer.
			Object o = httpSession.getAttribute(CaptchaServlet.NUMBER_OF_SHEEPS);
			
			// Remove the captcha answer from the session, as it's no longer valid.
			httpSession.removeAttribute(CaptchaServlet.NUMBER_OF_SHEEPS);
			
			// Will return the key of the message back to the user.
			String answerKey = "";
			
			if (o != null && o instanceof Integer) { // If there isn't a captcha answer stored, the user needs to refresh the image to get a new answer.
				authenticationConnection = new AuthenticationConnection(DatabaseConnection.getDbConfig(), false); // Create the connection with the database.
				
				// Check if the max captcha attempt count isn't reached.
				if (authenticationConnection.isAllowedToAttemptCaptcha(clientIpAddressHash)) { 
					String captchaParameter = request.getParameter("captcha");
					
					// Compare the inserted captcha answer with the generated answer.
					if (((Integer) o).toString().equals(captchaParameter)) {
						String userNameParameter = request.getParameter("username");
						String passwordParameter = request.getParameter("password");
						String emailParameter = request.getParameter("email");
						if (!authenticationConnection.checkIfUserExists(userNameParameter)) {
							boolean emailAlreadyInUse = false;
							if (emailParameter != null) {
								emailAlreadyInUse = authenticationConnection.checkIfEmailIsInUse(emailParameter);
							}
							
							if (emailAlreadyInUse) {
								answerKey = Resources.REGISTER_EMAIL_ALREADY_IN_USE.getKey();
							} else {
								int ipId;
								ipId = authenticationConnection.getIpAddressId(clientIpAddressHash);
								if(ipId == 0) {
									ipId = authenticationConnection.saveIpAddress(clientIpAddressHash);
								}
								authenticationConnection.registerUser(userNameParameter, passwordParameter, clientIpAddressHash, emailParameter);
								answerKey = Resources.REGISTER_SUCCEEDED.getKey();
							}
						} else {
							answerKey = Resources.REGISTER_USERNAME_ALREADY_EXISTS.getKey();
						}
					} else { // Too bad, the captcha answer wasn't good!
						authenticationConnection.saveFailedCaptchaAttempt(clientIpAddressHash);
						answerKey = Resources.REGISTER_CAPTCHA_WRONG_ANSWER.getKey();
					}
				} else { // Oh, is this guy blind? Maybe I should implement a voice captcha.
					answerKey = Resources.REGISTER_CAPTCHA_TOO_MANY_FAILED_ATTEMPTS.getKey();
				}
				// No database exceptions have occurred, so we can finish the transaction!
				authenticationConnection.commit();
			} else {
				answerKey = Resources.REGISTER_CAPTCHA_REFRESH.getKey();
			}
			
			// Print the answer after the transaction is complete.
			out.print(answerKey);
		} catch (DatabaseException e) {
			// A DatabaseException was an SQL Exception, meaning that something went wrong.
			// So it's probably better to roll back the changes so our database doesn't get corrupted.
			if (authenticationConnection != null) {
				authenticationConnection.rollback();
			}
			out.print(Resources.REGISTER_UNABLE_TO_REGISTER.getKey());
		}  catch (ValidationException e) {
			if (authenticationConnection != null) {
				authenticationConnection.rollback();
			}
			out.print(e.getUserMessage().toString());
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			if (authenticationConnection != null) {
				authenticationConnection.rollback();
			}
			// The hash might not been encrypted well, so do roll back.
			out.print(Resources.REGISTER_UNABLE_TO_REGISTER.getKey());
		} finally {
			// Close the output stream.
			if (out != null) {
				out.close();
			}
			if (authenticationConnection != null) {
				authenticationConnection.close();
			}
		}
	}
}
