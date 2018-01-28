package com.hawallen.poetry.util;

import android.content.Context;

import com.google.gson.Gson;
import com.hawallen.poetry.bean.Favour;

public class FileService {

	public static String[] getFavourFileList(Context context) {
		return FileManager.getFileList(context, "Favour");
	}

	public static void saveFavour(Context context, String data, String fileName) {
		FileManager.saveData(context, data, "Favour", fileName);
	}

	public static Favour getFavour(Context context, String fileName) {
		String strFavour = FileManager.getData(context, "Favour", fileName);
		Gson gson = new Gson();
		Favour favour = gson.fromJson(strFavour, Favour.class);
		return favour;
	}
	
	public static void deleteAllFavour(Context context) {
		String[] list = getFavourFileList(context);
		for (int i = 0; i < list.length; i++) {
			FileManager.deleteFile(context, "Favour", list[i]);
		}
	}
}
