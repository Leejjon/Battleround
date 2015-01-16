package org.stofkat.battleround.desktop;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.stofkat.battleround.client.http.dispatch.AsyncCallback;
import org.stofkat.battleround.client.http.dispatch.CustomCookieStore;
import org.stofkat.battleround.client.http.dispatch.HttpDispatchServiceAsync;
import org.stofkat.battleround.client.http.dispatch.HttpUtils;
import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.results.Result;

public class DesktopDispatchServiceAsync implements HttpDispatchServiceAsync {
	private final String dispatchServiceUri;
	private HttpClient httpClient;
	private CustomCookieStore cookieStore;
	
	public DesktopDispatchServiceAsync(String dispatchServiceUri) {
		this(dispatchServiceUri, new PoolingClientConnectionManager());
	}
	
	public DesktopDispatchServiceAsync(String dispatchServiceUri,
			ClientConnectionManager connectionManager) {
		this.dispatchServiceUri = dispatchServiceUri;
		httpClient = HttpUtils.getHttpClient(connectionManager);
		cookieStore = new CustomCookieStore();
	}

	public Object getResult(Action<?> action) {
		return HttpUtils.getResult(dispatchServiceUri, httpClient, cookieStore,
				action);
	}

	@Override
	public <R extends Result> void execute(final Action<R> action,
			final AsyncCallback<R> callback) {
		new Thread()
		{
			@Override
			public void run()
			{
				final Object result = getResult(action);
				HttpUtils.processResult(result, callback);
			}
		}.start();
	}
}
