package net.mare.wingtpatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class GTPatcher {

	public static String usage = "Usage: java -jar GrowtopiaPatcher.jar <growtopia exe> <new ip address> [alternative ip address] \nNOTE: IP address must be maximum of 14 characters.";

	public static void main(String[] args) {
		String filename = "";
		String ip = "";
		String altIp = "";
		if (args != null && args.length > 0 && args[0] != null) {
			filename = args[0];
			File apkfile = new File(filename);
			if (!(apkfile.exists() && !apkfile.isDirectory() && apkfile.canRead())) {
				System.err.println("Invalid file: " + filename);
				return;
			}
		} else {
			System.err.println(usage);
			return;
		}

		if (args.length < 2) {
			System.err.println(usage);
			return;
		}
		ip = args[1];
		if (args.length < 3) {
			altIp = ip;
		} else if (args.length == 3) {
			altIp = args[2];
		}
		
		try {
			patch(new File(filename), ip, altIp);
		} catch (Exception e) {
			System.err.println("Something went wrong. Please submit a report with the details below: ");
			e.printStackTrace();
			return;
		}
	}
	
	public static void patch(File file, String ip, String altIp) throws IOException {
		RandomAccessFile f = new RandomAccessFile(file, "r");
		byte[] b = new byte[(int) f.length()];
		f.readFully(b);
		f.close();
		byte[] toreplace = "growtopia1.com".getBytes();
		byte[] replacer = ip.getBytes();
		for (int i = 0; i < b.length - toreplace.length; i++) {
			boolean success = true;
			for (int j = 0; j < toreplace.length; j++) { //find location of growtopia1.com
				if (b[i + j] != toreplace[j]) {
					success = false; //growtopia1.com found
					break;
				}
			}
			if (success) { //check if growtopia1.com was found.
				for (int j = 0; j < toreplace.length; j++) {
					if (j < replacer.length) {
						b[i + j] = replacer[j]; //replace growtopia1.com with some other ip.
					} else {
						b[i + j] = 0; //pad with zeros if necessary.
					}
				}
			}
		}

		toreplace = "growtopia2.com".getBytes();
		replacer = altIp.getBytes();
		for (int i = 0; i < b.length - toreplace.length; i++) {
			boolean success = true;
			for (int j = 0; j < toreplace.length; j++) { //find location of growtopia2.com
				if (b[i + j] != toreplace[j]) {
					success = false; //growtopia2.com found
					break;
				}
			}
			if (success) { //check if growtopia2.com was found
				for (int j = 0; j < toreplace.length; j++) {
					if (j < replacer.length) {
						b[i + j] = replacer[j]; //replace growtopia2.com with some other ip.
					} else {
						b[i + j] = 0; //pad with zeros if necessary.
					}
				}
			}
		}
		String filename = file.toString();
		String baseName = filename.substring(0, filename.lastIndexOf("."));
		String baseExt = filename.substring(filename.lastIndexOf("."));
		File outputFile = new File(baseName + "_patched" + baseExt);
		outputFile.createNewFile();
		Files.write(outputFile.toPath(), b, StandardOpenOption.WRITE);
	}
	
}
