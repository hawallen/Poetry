package com.hawallen.poetry.layout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hawallen.poetry.R;


public class TipsDialog extends Dialog {

	private TextView tvTips;
	private Button btnOK;
	private Button btnCancel;
	
	public TipsDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tips_dialog);

		init();

		operate();
	}

	private void init() {
		tvTips = (TextView) findViewById(R.id.tv_tips);
		btnOK = (Button) findViewById(R.id.btn_ok);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
	}

	private void operate() {
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});

		btnOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}
	
	public void setOnOKClickListener(View.OnClickListener listener) {
		btnOK.setOnClickListener(listener);
	}

	public void setTipsText(String tipsText) {
		tvTips.setText(tipsText);
	}

}
