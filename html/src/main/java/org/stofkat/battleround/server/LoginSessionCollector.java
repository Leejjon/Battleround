package org.stofkat.battleround.server;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Alright this is a hack. The problem is we log in using a local html page via
 * a webview. Since I already made a login.html for the GWT version of my game I
 * thought it would be easy to reuse it rather than create a login screen in the
 * android UI.
 * 
 * So here we collect all LoginSessions that are being created. According to
 * stackoverflow this "smells":
 * http://stackoverflow.com/questions/3092363/how-can-i-load-java-httpsession-from-jsessionid
 * 
 * Probably because if we get a couple of million users the server will run out
 * of memory.
 * 
 * After we've logged in via the webview and communicate using the HttpDispatch
 * methods, we can simply grab the login session here and copy it into the new
 * session. Yes I'm aware of the session hijacking vulnerbilities and I will fix
 * this before releasing.
 * 
 * @author Leejjon
 */
public class LoginSessionCollector implements HttpSessionListener {
	private static final Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		sessions.put(session.getId(), session);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		sessions.remove(event.getSession().getId());
	}

	public static HttpSession find(String sessionId) {
		return sessions.get(sessionId);
	}
}
