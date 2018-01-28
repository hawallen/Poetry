package com.hawallen.poetry.layout;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hawallen.poetry.R;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

import java.io.IOException;


public class ShareDialog extends Dialog implements RequestListener {

	private EditText etShare;
	private TextView tvNum;
	private Button btnShare;
	private Button btnColse;
	
	private MyHandler mHandler;

	private Oauth2AccessToken oauth2AccessToken;
	private final int WEIBO_MAX_LENGTH = 140;
	private final int WEIBO_SHARE_COMPLETE = 2;
	private final int WEIBO_SHARE_IOEXCEPTION = 1;
	private final int WEIBO_SHARE_ERROR = 0;
	
	private Exception exception;

	public ShareDialog(Context context, int theme, Oauth2AccessToken oauth2AccessToken) {
		super(context, theme);
		this.oauth2AccessToken = oauth2AccessToken;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_dialog);

		init();

		operate();
	}

	private void init() {
		etShare = (EditText) findViewById(R.id.et_share);
		tvNum = (TextView) findViewById(R.id.tv_num);
		btnShare = (Button) findViewById(R.id.btn_share);
		btnColse = (Button) findViewById(R.id.btn_close);
		mHandler = new MyHandler();
	}

	private void operate() {
		etShare.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String mText = etShare.getText().toString();
				int len = mText.length();
				if (len <= WEIBO_MAX_LENGTH) {
					len = WEIBO_MAX_LENGTH - len;
					tvNum.setTextColor(getContext().getResources().getColor(R.color.blue));
					if (!btnShare.isEnabled())
						btnShare.setEnabled(true);
				} else {
					len = len - WEIBO_MAX_LENGTH;
					tvNum.setTextColor(Color.RED);
					if (btnShare.isEnabled())
						btnShare.setEnabled(false);
				}
				tvNum.setText(String.valueOf(len));
			}
		});

		btnColse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});

		btnShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StatusesAPI api = new StatusesAPI(oauth2AccessToken);
				String content = etShare.getText().toString();
				if (TextUtils.isEmpty(content)) {
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.weibo_content_empty), Toast.LENGTH_LONG).show();
				} else {
					btnShare.setEnabled(false);
					api.update(content, "", "", ShareDialog.this);
				}
			}
		});
	}

	public void setShareText(String shareText) {
		etShare.setText(shareText);
	}

	@Override
	public void onComplete(String response) {
		mHandler.sendEmptyMessage(WEIBO_SHARE_COMPLETE);
	}

	@Override
	public void onIOException(IOException e) {
		exception = e;
		mHandler.sendEmptyMessage(WEIBO_SHARE_IOEXCEPTION);
	}

	@Override
	public void onError(WeiboException e) {
		exception = e;
		mHandler.sendEmptyMessage(WEIBO_SHARE_ERROR);
	}
	
	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			String str = "";
			switch (msg.what) {
			case WEIBO_SHARE_COMPLETE:
				str = getContext().getResources().getString(R.string.share_succeed);
				Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
				cancel();
				break;
			case WEIBO_SHARE_IOEXCEPTION:
				str = getContext().getResources().getString(R.string.share_fail)+":"+exception.getMessage();
				Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
				cancel();
				break;
			case WEIBO_SHARE_ERROR:
				str = getContext().getResources().getString(R.string.share_fail)+":"+exception.getMessage();
				Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
				cancel();
				break;
			}
		}
	}

}
