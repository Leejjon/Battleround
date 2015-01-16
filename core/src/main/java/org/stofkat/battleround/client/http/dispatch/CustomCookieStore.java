package org.stofkat.battleround.client.http.dispatch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;

public class CustomCookieStore implements CookieStore, Serializable {
	private static final long serialVersionUID = 1L;

	private final TreeSet<Cookie> cookies;

	public CustomCookieStore()
	{
		super();
		this.cookies = new TreeSet<Cookie>(new CookieIdentityComparator());
	}

	/**
	 * Adds an {@link Cookie HTTP cookie}, replacing any existing equivalent
	 * cookies.
	 * If the given cookie has already expired it will not be added, but
	 * existing
	 * values will still be removed.
	 * 
	 * @param cookie
	 *            the {@link Cookie cookie} to be added
	 * 
	 * @see #addCookies(Cookie[])
	 * 
	 */
	@Override
	public synchronized void addCookie(Cookie cookie)
	{
		if (cookie != null)
		{
			// first remove any old cookie that is equivalent
			cookies.remove(cookie);
			if (!cookie.isExpired(new Date()))
			{
				cookies.add(cookie);
			}
		}
	}

	/**
	 * Adds an array of {@link Cookie HTTP cookies}. Cookies are added
	 * individually and
	 * in the given array order. If any of the given cookies has already
	 * expired it will
	 * not be added, but existing values will still be removed.
	 * 
	 * @param cookies
	 *            the {@link Cookie cookies} to be added
	 * 
	 * @see #addCookie(Cookie)
	 * 
	 */
	public synchronized void addCookies(Cookie[] cookies)
	{
		if (cookies != null)
		{
			for (Cookie cooky : cookies)
			{
				this.addCookie(cooky);
			}
		}
	}

	/**
	 * Returns an immutable array of {@link Cookie cookies} that this HTTP
	 * state currently contains.
	 * 
	 * @return an array of {@link Cookie cookies}.
	 */
	@Override
	public synchronized List<Cookie> getCookies()
	{
		// create defensive copy so it won't be concurrently modified
		return new ArrayList<Cookie>(cookies);
	}

	/**
	 * Removes all of {@link Cookie cookies} in this HTTP state
	 * that have expired by the specified {@link java.util.Date date}.
	 * 
	 * @return true if any cookies were purged.
	 * 
	 * @see Cookie#isExpired(Date)
	 */
	@Override
	public synchronized boolean clearExpired(final Date date) {
		if (date == null) {
			return false;
		}
		boolean removed = false;
		for (Iterator<Cookie> it = cookies.iterator(); it.hasNext();) {
			if (it.next().isExpired(date)) {
				it.remove();
				removed = true;
			}
		}
		return removed;
	}

	/**
	 * Removes all of {@link Cookie cookies} in this HTTP state
	 * that have specified name.
	 * 
	 * @param cookieName
	 *            - the name of cookie to remove
	 * @return true if any cookie was purged.
	 * 
	 */
	public synchronized boolean removeCookie(String cookieName) {
		if (cookieName == null) {
			return false;
		}
		boolean removed = false;
		for (Iterator<Cookie> it = cookies.iterator(); it.hasNext();) {
			if (it.next().getName().equals(cookieName)) {
				it.remove();
				removed = true;
			}
		}
		return removed;
	}

	/**
	 * Clears all cookies.
	 */
	@Override
	public synchronized void clear() {
		cookies.clear();
	}

	@Override
	public synchronized String toString() {
		return cookies.toString();
	}

	public String getCookie(String cookieName) {
		if (cookieName == null) {
			return null;
		}
		String result = null;
		for (Iterator<Cookie> it = cookies.iterator(); it.hasNext();) {
			Cookie cookie = it.next();
			if (cookie.getName().equals(cookieName)) {
				result = cookie.getValue();
			}
		}
		return result;
	}
}
