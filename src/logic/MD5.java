package logic;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public static String fromFile(File file) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		// Change MD5 to SHA1 to get SHA checksum
		// MessageDigest md = MessageDigest.getInstance("SHA1");

		try (FileInputStream fileInputStream = new FileInputStream(file);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
			byte[] dataBytes = new byte[1024];
			int nRead = 0;
			while ((nRead = bufferedInputStream.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nRead);
			}
		} catch (FileNotFoundException e) {
			System.err.println("MD5: "+ file + " could not be found");
			return null;
		} catch (IOException e) {
			System.err.println("MD5: "+ file + " could not be read");
		}

		byte[] mdbytes = md.digest();

		// convert the byte to hex format
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
}
