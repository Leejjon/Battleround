package org.stofkat.battleround.common;

import java.io.Serializable;

/**
 * A simple class (POJO) to store user information in the HTTP session.
 * 
 * @author Leejjon
 */
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name;
	private String email;
	private byte[] currentIpAddressHash;
	private long lastLoginTimestamp;
	private byte[] lastLoginIPAddress;
	
	/**
	 * Warning, do not use this constructor, it's only required for RPC calls.
	 */
	public User() {}
	
	public User(long id, String name, String email, byte[] currentIpAddress) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.currentIpAddressHash = currentIpAddress;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getCurrentIpAddressHash() {
		return currentIpAddressHash;
	}

	public void setCurrentIpAddressHash(byte[] currentIpAddressHash) {
		this.currentIpAddressHash = currentIpAddressHash;
	}

	public long getLastLoginTimestamp() {
		return lastLoginTimestamp;
	}

	public void setLastLoginTimestamp(long lastLoginTimestamp) {
		this.lastLoginTimestamp = lastLoginTimestamp;
	}

	public byte[] getLastLoginIPAddress() {
		return lastLoginIPAddress;
	}

	public void setLastLoginIPAddress(byte[] lastLoginIPAddress) {
		this.lastLoginIPAddress = lastLoginIPAddress;
	}
}
