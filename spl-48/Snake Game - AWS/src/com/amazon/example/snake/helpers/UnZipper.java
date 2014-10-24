package com.amazon.example.snake.helpers;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZipper {
	private String zipFile;
	private String location;

	public UnZipper(String zipFile, String location) {
		this.zipFile = zipFile;
		this.location = location;

		checkDir("");
	}

	public void unzip() {
		try {
			FileInputStream fin = new FileInputStream(zipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				Log.v("UnZipper", "Unzipping " + ze.getName());

				if (ze.isDirectory()) {
					checkDir(ze.getName());
				} else {
					FileOutputStream fout = new FileOutputStream(location
							+ ze.getName());
					for (int c = zin.read(); c != -1; c = zin.read()) {
						fout.write(c);
					}

					zin.closeEntry();
					fout.close();
				}

			}
			zin.close();
		} catch (Exception e) {
			Log.e("UnZipper", "unzip", e);
		}

	}

	private void checkDir(String dir) {
		File f = new File(location + dir);

		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}
}