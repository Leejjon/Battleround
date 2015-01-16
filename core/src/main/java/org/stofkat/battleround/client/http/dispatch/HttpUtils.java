package org.stofkat.battleround.client.http.dispatch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.results.Result;


public class HttpUtils {
	public static String BINARY_TYPE = "application/octet-stream";
	static int NetworkConnectionTimeout_ms = 10000;

	public static HttpClient getHttpClient(
			ClientConnectionManager connectionManager)
	{
		return getHttpClient(connectionManager, NetworkConnectionTimeout_ms);
	}

	public static HttpClient getHttpClient(
			ClientConnectionManager connectionManager, int connectionTimeout_ms)
	{
		// PoolingClientConnectionManager manager = new
		// PoolingClientConnectionManager();
		HttpParams params = new BasicHttpParams();

		// set params for connection...
		HttpConnectionParams.setStaleCheckingEnabled(
				params, false);
		HttpConnectionParams.setConnectionTimeout(params,
				connectionTimeout_ms);
		HttpConnectionParams.setSoTimeout(params,
				connectionTimeout_ms);
		final DefaultHttpClient httpClient = connectionManager == null ?
				new DefaultHttpClient(params) : new DefaultHttpClient(
						connectionManager,
						params);
		return httpClient;
	}

	public static Object getResult(
			String dispatchServiceUri,
			HttpClient httpClient,
			final CookieStore cookieStore,
			final Action<?> action)
	{
		return getResult(dispatchServiceUri, httpClient, cookieStore, action,
				null);
	}

	/** this method is called in a non-"edt" thread */
	public static Object getResult(
			String dispatchServiceUri,
			HttpClient httpClient,
			final CookieStore cookieStore,
			final Action<?> action,
			final Object[] additionalData)
	{
//		log.debug(action.getClass().getSimpleName(),
//				"getting result - start");

		try {
			// create post method
			HttpPost postMethod = new HttpPost(dispatchServiceUri);
//			postMethod.addHeader(header);
			
			// create request entity
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(action);
			if (additionalData != null) {
				for (Object object : additionalData) {
					oos.writeObject(object);
				}
			}
			ByteArrayEntity req_entity = new ByteArrayEntity(baos.toByteArray());
			req_entity.setContentType(BINARY_TYPE);

			// associating entity with method
			postMethod.setEntity(req_entity);

			final HttpContext context = getHttpContext(cookieStore);
//			synchronized (context)
			{
				// RESPONSE
				Object result = httpClient.execute(postMethod,
						new ResponseHandler<Object>() {
							@Override
							public Object handleResponse(HttpResponse response)
									throws ClientProtocolException, IOException {
								int statusCode = response.getStatusLine()
										.getStatusCode();
								HttpEntity resp_entity = response.getEntity();
								if (statusCode == HttpStatus.SC_OK
										&& resp_entity != null) {

									try {
										byte[] data = EntityUtils
												.toByteArray(resp_entity);
										ObjectInputStream ois = new ObjectInputStream(
												new ByteArrayInputStream(data));
										Object result = ois.readObject();
										// log.debug(
										// action.getClass()
										// .getSimpleName(),
										// "data size from servlet="
										// + data.toString());
										ois.close();
										return result;
									} catch (Exception e) {
										// log.error(
										// action.getClass()
										// .getSimpleName(),
										// "problem processing post response",
										// e);
										return e;
									}

								} else {
									throw new IOException(new StringBuffer()
											.append("HTTP response : ")
											.append(response.getStatusLine())
											.toString());
								}
							}
						}, context);
				return result;
			}

		} catch (Exception e) {
			// log.error(action.getClass().getSimpleName(), e);
			return e;
		} finally {
			// log.debug(action.getClass().getSimpleName(),
			// "getting result - end");
		}
	}

	public static HttpContext getHttpContext(CookieStore cookieStore) {
		// Create local HTTP context
		HttpContext localContext = new BasicHttpContext();
		// Bind custom cookie store to the local context
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		return localContext;
	}

	@SuppressWarnings("unchecked")
	public static <R extends Result> void processResult(Object result,
			final AsyncCallback<R> callback) {
		try {
			if (result == null || result instanceof Result) {
				callback.onSuccess((R) result);
			} else if (result instanceof Throwable) {
				callback.onFailure((Throwable) result);
			} else {
				callback.onFailure(new Exception("Invalid result" + result));
			}
		} catch (Throwable t) {
			callback.onFailure(t);
		}
	}
}
