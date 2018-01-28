package com.hawallen.poetry.layout;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hawallen.poetry.FavourActivity;
import com.hawallen.poetry.PoetryActivity;
import com.hawallen.poetry.R;
import com.hawallen.poetry.bean.Favour;
import com.hawallen.poetry.bean.Poetry;
import com.hawallen.poetry.util.FileService;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.keep.AccessTokenKeeper;
import com.weibo.sdk.android.util.Utility;

import java.util.Calendar;


public class PoetryOne extends LinearLayout {

	private TextView tvUp;
	private TextView tvDown;
	private Button btnFavor;
	private Button btnShare;
	
	private Poetry poetry;
	
	private int fileIndex = -1;
	public int visibility = 0;

	public PoetryOne(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public PoetryOne(Context context) {
		super(context);
	}
	
	public Poetry getPoetry() {
		return poetry;
	}
	
	public void setFavorState(int fileIndex) {
		if (fileIndex > -1) {
			btnFavor.setBackgroundResource(R.drawable.favor_on);
		} else {
			btnFavor.setBackgroundResource(R.drawable.favor);
		}
		this.fileIndex = fileIndex;
	}
	
	public void showPoetry(Poetry poetry, int fileIndex) {
		btnFavor = (Button) findViewById(R.id.btn_favor);
		btnShare = (Button) findViewById(R.id.btn_share);
		MyButtonListener mButtonListener = new MyButtonListener();
		btnFavor.setOnClickListener(mButtonListener);
		btnShare.setOnClickListener(mButtonListener);
		
		tvUp = (TextView) findViewById(R.id.tv_up);
		tvDown = (TextView) findViewById(R.id.tv_down);
		if (poetry.poetryStrings.length > 1) {
			tvUp.setText(poetry.poetryStrings[0]);
			tvDown.setText(poetry.poetryStrings[1]);
		}
		setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.popup_enter));
		
		setFavorState(fileIndex);
		
		this.poetry = poetry;
	}
	
	@Override
	protected void onAnimationEnd() {
		switch (visibility) {
		case View.GONE:
			PoetryActivity.produceDates.add(poetry.produceDate);
			setVisibility(visibility);
			break;
		}
	}
	
	private class MyButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			int id = v.getId();
//			if (getContext() instanceof PoetryActivity) {
//			}
			switch (id) {
			case R.id.btn_favor:
				Favour favour = null;
				if (fileIndex > -1) { //whether is favored, > -1 means favored, others mean unfavored
					btnFavor.setBackgroundResource(R.drawable.favor);
					favour = PoetryActivity.favours.get(fileIndex);
					fileIndex = -1;
					favour.poetries.remove(poetry);

					if (getContext() instanceof FavourActivity) {
						setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.popup_exit));
						visibility = View.GONE;
					}
				} else {
					btnFavor.setBackgroundResource(R.drawable.favor_on);
					Calendar calendar = Calendar.getInstance();
					poetry.favorDate = String.valueOf(calendar.getTimeInMillis());
					String fileName = calendar.get(Calendar.YEAR) + "" +(calendar.get(Calendar.MONTH)+1);
					int existIndex = -1;
					for (int i = 0; i < PoetryActivity.favours.size(); i++) {
						if (PoetryActivity.favours.get(i).fileName.equals(fileName)) {
							existIndex = i;
						}
					}
					if (existIndex == -1) {
						favour = new Favour();
						favour.fileName = fileName;
						favour.poetries.add(poetry);
						PoetryActivity.favours.add(favour);
						fileIndex = PoetryActivity.favours.size() - 1;
					} else {
						fileIndex = existIndex;
						favour = PoetryActivity.favours.get(existIndex);
						favour.poetries.add(poetry);
					}
				}
				String strFavour = new Gson().toJson(favour);
				FileService.saveFavour(getContext(), strFavour, favour.fileName);
				break;
			case R.id.btn_share:
				final Oauth2AccessToken oauth2AccessToken = AccessTokenKeeper.readAccessToken(getContext());
				Weibo.isWifi=Utility.isWifi(getContext());
				if (oauth2AccessToken.isSessionValid()) {
					ShareDialog shareDialog = new ShareDialog(getContext(), R.style.MyDialog, oauth2AccessToken);
					shareDialog.show();
					shareDialog.setShareText(tvUp.getText().toString()+tvDown.getText().toString());
				} else {
					Weibo weibo = Weibo.getInstance(Weibo.app_key, Weibo.redirecturl);
					weibo.authorize(getContext(), new WeiboAuthListener() {
						@Override
						public void onWeiboException(WeiboException e) {
							
						}
						
						@Override
						public void onError(WeiboDialogError e) {
							
						}
						
						@Override
						public void onComplete(Bundle values) {
							CookieSyncManager.getInstance().sync();
							oauth2AccessToken.setToken(values.getString(Weibo.KEY_TOKEN));
							oauth2AccessToken.setExpiresIn(values.getString(Weibo.KEY_EXPIRES));
							oauth2AccessToken.setRefreshToken(values.getString(Weibo.KEY_REFRESHTOKEN));
							AccessTokenKeeper.keepAccessToken(getContext(), oauth2AccessToken);
							
							ShareDialog shareDialog = new ShareDialog(getContext(), R.style.MyDialog, oauth2AccessToken);
							shareDialog.show();
							shareDialog.setShareText(tvUp.getText().toString()+tvDown.getText().toString());
						}
						
						@Override
						public void onCancel() {
							
						}
					});
				}
				break;
			}
		}
	}
}
