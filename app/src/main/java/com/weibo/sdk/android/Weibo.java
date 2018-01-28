package com.weibo.sdk.android;

import com.weibo.sdk.android.util.Utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 
 * @author luopeng (luopeng@staff.sina.com.cn)
 */
public class Weibo {
	// private static final String WEIBO_SDK_VERSION = "2.0";

	public static String URL_OAUTH2_ACCESS_AUTHORIZE = "https://open.weibo.cn/oauth2/authorize";

	private static Weibo mWeiboInstance = null;

	public static String app_key = "3049064394";// 绗笁鏂瑰簲鐢ㄧ殑appkey
	public static String redirecturl = "http://www.sina.com";// 閲嶅畾鍚憉rl

	public Oauth2AccessToken accessToken = null;// AccessToken瀹炰緥

	public static final String KEY_TOKEN = "access_token";
	public static final String KEY_EXPIRES = "expires_in";
	public static final String KEY_REFRESHTOKEN = "refresh_token";
	public static boolean isWifi = false;// 褰撳墠鏄惁涓簑ifi

	/**
	 * 
	 * @param appKey
	 *            绗笁鏂瑰簲鐢ㄧ殑appkey
	 * @param redirectUrl
	 *            绗笁鏂瑰簲鐢ㄧ殑鍥炶皟椤�
	 * @return Weibo鐨勫疄渚�
	 */
	public synchronized static Weibo getInstance(String appKey, String redirectUrl) {
		if (mWeiboInstance == null) {
			mWeiboInstance = new Weibo();
		}
		app_key = appKey;
		Weibo.redirecturl = redirectUrl;
		return mWeiboInstance;
	}

	/**
	 * 璁惧畾绗笁鏂逛娇鐢ㄨ�鐨刟ppkey鍜岄噸瀹氬悜url
	 * 
	 * @param appKey
	 *            绗笁鏂瑰簲鐢ㄧ殑appkey
	 * @param redirectUrl
	 *            绗笁鏂瑰簲鐢ㄧ殑鍥炶皟椤�
	 */
	public void setupConsumerConfig(String appKey, String redirectUrl) {
		app_key = appKey;
		redirecturl = redirectUrl;
	}

	/**
	 * 
	 * 杩涜寰崥璁よ瘉
	 * 
	 * @param activity
	 *            璋冪敤璁よ瘉鍔熻兘鐨凜ontext瀹炰緥
	 * @param listener
	 *            WeiboAuthListener 寰崥璁よ瘉鐨勫洖璋冩帴鍙�
	 */
	public void authorize(Context context, WeiboAuthListener listener) {
		isWifi = Utility.isWifi(context);
		startAuthDialog(context, listener);
	}

	public void startAuthDialog(Context context, final WeiboAuthListener listener) {
		WeiboParameters params = new WeiboParameters();
		// CookieSyncManager.createInstance(context);
		startDialog(context, params, listener);
	}

	public void startDialog(Context context, WeiboParameters parameters, final WeiboAuthListener listener) {
		parameters.add("client_id", app_key);
		parameters.add("response_type", "token");
		parameters.add("redirect_uri", redirecturl);
		parameters.add("display", "mobile");

		if (accessToken != null && accessToken.isSessionValid()) {
			parameters.add(KEY_TOKEN, accessToken.getToken());
		}
		String url = URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters);
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			Utility.showAlert(context, "Error", "Application requires permission to access the Internet");
		} else {
			new WeiboDialog(context, url, listener).show();
		}
	}

}
