package org.stofkat.battleround.server.authentication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.stofkat.battleround.common.User;
import org.stofkat.battleround.common.resources.Resources;
import org.stofkat.battleround.database.AuthenticationConnection;
import org.stofkat.battleround.database.DatabaseException;
import org.stofkat.battleround.database.security.ValidationUtility;
import org.stofkat.battleround.server.security.EncryptionUtility;

import com.google.appengine.api.utils.SystemProperty;

/**
 * Servlet implementation class Login
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(LoginServlet.class);
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
		BasicConfigurator.configure();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		AuthenticationConnection accountsConnection = null;
		ServletOutputStream out = null;
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		/*
		 * Set the response type to this according to Solution #2 from Dimon Buzermann
		 * 
		 * http://stackoverflow.com/questions/9841341/jquery-jsession-cookie-is-not-sent-to-a-server
		 * 
		 * This way the cookies will also be stored if the user has adblock plus enabled.
		 */
//		response.setContentType("text/plain");
		
		String answerKey = "";
		try {
			out = response.getOutputStream();
			if (username != null && password != null) {
				// Get the output stream of the response so we can put our response text in it.
				
				HttpSession httpSession = request.getSession(true);
	
				// First check if we aren't logged in yet. We don't want to overwrite our current login session.
				if (httpSession.getAttribute(AuthenticationConnection.userObjectKey) != null) {
					answerKey = Resources.LOGIN_ALREADY_LOGGED_IN.getKey();
				} else { // This session is not logged in yet.
					// Open the connection, with auto commit disabled.
					boolean productionMode = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
					accountsConnection = new AuthenticationConnection(productionMode, false);
					User user = accountsConnection.login(username, password, EncryptionUtility.getSHA512HashAsByteArray(request.getRemoteAddr()));
					if (user == null || !ValidationUtility.onlyContainsLettersAndNumbers(username) || !ValidationUtility.isThisPasswordValid(password)) {
						answerKey = Resources.LOGIN_FAILED.getKey();
					} else {
						user.setCurrentIpAddressHash(EncryptionUtility.getSHA512HashAsByteArray(request.getRemoteAddr()));
					
						httpSession.setAttribute(AuthenticationConnection.userObjectKey, user);
						answerKey = Resources.LOGIN_SUCCESSFUL.getKey();
					}
					accountsConnection.commit();
				}
				out.print(answerKey);
			} else {
				out.print(Resources.LOGIN_ERROR.getKey());
			}
		} catch (DatabaseException e) {
			if (accountsConnection != null) {
				accountsConnection.rollback();
			}
			out.print(Resources.LOGIN_ERROR.getKey());
		} catch (NoSuchAlgorithmException e) {
			log.error(e);
			if (accountsConnection != null) {
				accountsConnection.rollback();
			}
			out.print(Resources.LOGIN_ERROR.getKey());
		} finally {
			if (out != null) {
				out.close();
			}
			
			if (accountsConnection != null) {
				accountsConnection.close();
			}
		}
	}
}
