package org.stofkat.battleround.server.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * This class currently contains all encryption related methods.
 * 
 * @author Leejjon
 */
public class EncryptionUtility {
	/**
	 * Hash a string to a SHA 512 hash.
	 * @param input The value we are going to hash.
	 * @return The hash.
	 * @throws NoSuchAlgorithmException
	 */
	private static byte[] getSHA512Hash(String input) throws NoSuchAlgorithmException {
		MessageDigest SHA512MessageDigest = MessageDigest.getInstance("SHA-512");
		SHA512MessageDigest.update(input.getBytes());
        return SHA512MessageDigest.digest();
	}
	
	/**
	 * Hash a string into a SHA 512 hash string displayed in hexadecimal form.
	 * @param input The value we are going to hash.
	 * @return The hash value parsed to a string in hexadecimal format.
	 * @throws NoSuchAlgorithmException
	 */
	public static String getSHA512HashAsHexaDecimalString(String input) throws NoSuchAlgorithmException {
		byte[] hash = getSHA512Hash(input);
		
		String out = "";
		for (int i = 0; i < hash.length; i++) {
            byte temp = hash[i];
            String s = Integer.toHexString(new Byte(temp));
            while (s.length() < 2) {
                s = "0" + s;
            }
            s = s.substring(s.length() - 2);
            out += s;
        }
		return out;
	}
	
	/**
	 * Hash a string to a SHA 512 hash string.
	 * @param input The value we are going to hash.
	 * @return The hash value parsed into a string.
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] getSHA512HashAsByteArray(String input) throws NoSuchAlgorithmException {
		String defaultSalt = "leejjon.net";
		return getSHA512Hash(input + defaultSalt);
	}
	
	/**
	 * Hash a string to a SHA 512 hash string.
	 * @param input The value we are going to hash.
	 * @param salt The salt we use to avoid that this hash can be cracked easily using rainbow tables.
	 * @return The hash value parsed into a string.
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] getSHA512HashAsByteArray(String input, String salt) throws NoSuchAlgorithmException {
		return getSHA512Hash(input + salt);
	}
}
