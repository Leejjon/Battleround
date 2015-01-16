package org.stofkat.battleround.server.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.stofkat.battleround.server.DefaultActionHandlerRegistry;
import org.stofkat.battleround.server.Dispatch;
import org.stofkat.battleround.server.actionhandlers.ChatActionHandler;
import org.stofkat.battleround.server.actionhandlers.CheckForUpdatesActionHandler;
import org.stofkat.battleround.server.actionhandlers.GetLevelActionHandler;
import org.stofkat.battleround.server.actionhandlers.SelectCharacterActionHandler;
import org.stofkat.battleround.server.InstanceActionHandlerRegistry;
import org.stofkat.battleround.server.SimpleDispatch;
import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.exceptions.DispatchException;
import org.stofkat.battleround.shared.dispatch.exceptions.ServiceException;
import org.stofkat.battleround.shared.dispatch.results.Result;

/**
 * Most code here is ripped from the AbstractStandardDispatchServlet class in
 * the http-dispatch project.
 * 
 * @author Leejjon
 */
public class HttpDispatchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Dispatch dispatch;

	public HttpDispatchServlet() {
		InstanceActionHandlerRegistry registry = new DefaultActionHandlerRegistry();
		registry.addHandler(new GetLevelActionHandler());
		registry.addHandler(new SelectCharacterActionHandler());
		registry.addHandler(new CheckForUpdatesActionHandler());
		registry.addHandler(new ChatActionHandler());
		dispatch = new SimpleDispatch(registry);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException {

		Throwable t = null;
		byte[] output = null;
		try {
			Object result = null;
			try {
				System.out.println("trying to extract action from request");

				BufferedInputStream bis = new BufferedInputStream(request.getInputStream());
				ObjectInputStream ois = new ObjectInputStream(bis);
				result = getResult(ois, request);
				ois.close();
				System.out.println("created response result, sending it back");

			} catch (DispatchException e) {
				System.out.println("generated dispatch exception, sending it back");
				result = e;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			{
				oos.writeObject(result);
			}
			output = baos.toByteArray();

			System.out.println("sent response back...");
		} catch (Throwable e) {
			System.out.println("Unable to process request" /* , e */);
			t = e;
		}
		processOutput(res, t, output);

	}

	@SuppressWarnings("unchecked")
	protected Object getResult(ObjectInputStream ois, HttpServletRequest request) throws IOException, ClassNotFoundException, DispatchException {
		Object result;
		Action<? extends Result> input = (Action<? extends Result>) ois.readObject();
		System.out.println("got the action from the client:" + input);

		result = execute(input, request.getSession());
		return result;
	}

	protected void processOutput(HttpServletResponse res, Throwable t, byte[] output) throws IOException {
		ServletOutputStream sos = res.getOutputStream();

		if (output != null) {
			res.setContentType("application/octet-stream");
			res.setContentLength(output.length);
			sos.write(output);
		} else {
			res.setContentType("text/plain");
			byte[] message = t.getMessage().getBytes();
			res.setContentLength(message.length);
			sos.write(message);
		}

		sos.flush();
		sos.close();
	}
	
	private <R extends Result> R execute(Action<R> action, HttpSession session) throws DispatchException {
		try {
			if (dispatch == null)
				throw new ServiceException("No dispatch found for servlet '" + getServletName() + "' . Please verify your server-side configuration.");

			return dispatch.execute(action, session);
		} catch (RuntimeException e) {
			log("Exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
}
