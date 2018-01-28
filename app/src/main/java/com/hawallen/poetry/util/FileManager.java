package com.hawallen.poetry.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import com.hawallen.poetry.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class FileManager {

	public static String FilePath = Environment.getExternalStorageDirectory().toString() + "/" + "Poetry" + "/"; // ��ȡSD��·��

	public static boolean create(Context context, String folderName) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(FilePath + folderName);
			try {
				if (!dir.exists()) {
					dir.mkdirs();
				}
				return true;
			} catch (Exception ex) {
				Toast.makeText(context, context.getResources().getString(R.string.mkdirsEX), Toast.LENGTH_LONG).show();
				return false;
			}
		} else {
			Toast.makeText(context, context.getResources().getString(R.string.SDCardNotExist), Toast.LENGTH_LONG).show();
			return false;
		}
		
	}

	public static String[] getFileList(Context context, String folderName) {
		String[] list = null;
		if (create(context, folderName)) {
			File dir = new File(FilePath + folderName);
			list = dir.list();
		}
		return list;
	}
	
	public static void deleteFile(Context context, String folderName, String fileName) {
		if (create(context, folderName)) {
			File file = new File(FilePath + folderName + "/" + fileName);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	public static void saveData(Context context, String data, String folderName, String fileName) {
		if (create(context, folderName)) {
			File file = new File(FilePath + folderName + "/" + fileName + ".txt");
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(data.getBytes());
				fos.flush();
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	public static String getData(Context context, String folderName, String fileName) {
		String data = "";
		if (create(context, folderName)) {
			File file = new File(FilePath + folderName + "/" + fileName + ".txt");
			String line = "";
			try {
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isb = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isb);
				while ((line = br.readLine()) != null) {
					data = data + line;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public static void savePicture(Context context, Bitmap bitmap, String folderName, String fileName) {
		if (create(context, folderName)) {
			File file = new File(FilePath + folderName + "/" + fileName + ".png");
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	public static Bitmap getPicture(Context context, String folderName, String fileName) {
		if (create(context, folderName)) {

		}
		return BitmapFactory.decodeFile(FilePath + folderName + "/" + fileName + ".png");
	}

}
