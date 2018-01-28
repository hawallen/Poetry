package com.hawallen.poetry;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hawallen.poetry.bean.Poetry;
import com.hawallen.poetry.layout.PoetryOne;
import com.hawallen.poetry.layout.TipsDialog;
import com.hawallen.poetry.util.FileService;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class FavourActivity extends Activity {

	private ScrollView svFavours;
	private LinearLayout llFavours;
	private Button btnBack;
	private Button btnDelete;
	private Button btnCheckUpdate;
	private TextView tvWeibo;
	private TextView tvVersion;
	
	private TextView tvPageName;
	private RelativeLayout llHandler;
	private SlidingDrawer slidingDrawer;

	private GestureDetector gestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favour);

		init();
		operate();
	}
	
	private void init() {
		svFavours = (ScrollView) findViewById(R.id.sv_favours);
		svFavours.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return onTouchEvent(event);
			}
		});
		llFavours = (LinearLayout) findViewById(R.id.ll_favours);
		btnBack = (Button)findViewById(R.id.btn_back);
		btnDelete = (Button) findViewById(R.id.btn_delete);
		
		tvPageName = (TextView) findViewById(R.id.tv_page_name);
		slidingDrawer = (SlidingDrawer) findViewById(R.id.sd_about);
		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				tvPageName.setText(getString(R.string.about));
				tvPageName.setTextColor(getResources().getColor(R.color.black));
				btnDelete.setEnabled(false);
			}
		});
		slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			@Override
			public void onDrawerClosed() {
				tvPageName.setText(getString(R.string.favour));
				tvPageName.setTextColor(getResources().getColor(R.color.red));
				btnDelete.setEnabled(true);
			}
		});
		llHandler = (RelativeLayout) findViewById(R.id.handle);

		gestureDetector = new GestureDetector(new DefaultGestureDetector());
		
		btnCheckUpdate = (Button) findViewById(R.id.btn_check_update);
		btnCheckUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
					@Override
					public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
						switch (updateStatus) {
						case 0:
							// has update
							UmengUpdateAgent.showUpdateDialog(FavourActivity.this, updateInfo);
							break;
						case 1:
							// has no update
							Toast.makeText(FavourActivity.this, "没有更新", Toast.LENGTH_SHORT).show();
							break;
						case 2:
							// none wifi
							Toast.makeText(FavourActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();

							break;
						case 3:
							// time out
							Toast.makeText(FavourActivity.this, "超时", Toast.LENGTH_SHORT).show();
						}
					}

				});
				UmengUpdateAgent.setOnDownloadListener(new UmengDownloadListener() {
					@Override
					public void OnDownloadEnd(int result) {
						Toast.makeText(FavourActivity.this, "download result : " + result, Toast.LENGTH_SHORT).show();
					}
				});
				UmengUpdateAgent.update(FavourActivity.this);
			}
		});
		
		tvVersion = (TextView) findViewById(R.id.tv_version);
		PackageManager packageManager = getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			tvVersion.setText("Version:" + packageInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		tvVersion =  
		tvWeibo = (TextView) findViewById(R.id.tv_weibo);
		tvWeibo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://weibo.com/u/1910482442"));
				startActivity(it);
			}
		});
		
		Intent intent = getIntent();
		String pageName = intent.getStringExtra("pageName");
		if (pageName != null) {
			btnBack.setText(pageName);
		}
	}
	
	private void operate() {
		MyButtonOnClickListener mButtonOnClickListener = new MyButtonOnClickListener();
		btnBack.setOnClickListener(mButtonOnClickListener);
		btnDelete.setOnClickListener(mButtonOnClickListener);
		
		for (int i = 0; i < PoetryActivity.favours.size(); i++) {
			for (int j = 0; j < PoetryActivity.favours.get(i).poetries.size(); j++) {
				Poetry poetry = PoetryActivity.favours.get(i).poetries.get(j);
				PoetryOne poetryOne = (PoetryOne) View.inflate(this, R.layout.poetry_one, null);
				poetryOne.showPoetry(poetry, i);
				llFavours.addView(poetryOne, 0);
			}
		}
	}

	private class MyButtonOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.btn_back:
				finish();
				break;
			case R.id.btn_delete:
				final TipsDialog tipsDialog = new TipsDialog(FavourActivity.this, R.style.MyDialog);
				tipsDialog.show();
				tipsDialog.setTipsText(getResources().getString(R.string.tips_delete_favour));
				tipsDialog.setOnOKClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						for (int i = 0; i < llFavours.getChildCount(); i++) {
							PoetryOne poetryOne = (PoetryOne) llFavours.getChildAt(i);
							poetryOne.visibility = View.GONE;
							poetryOne.startAnimation(AnimationUtils.loadAnimation(FavourActivity.this, R.anim.popup_exit));
						}
						FileService.deleteAllFavour(FavourActivity.this);
						PoetryActivity.favours.clear();
						tipsDialog.dismiss();
					}
				});
				break;
			}
		}
	}
	private class DefaultGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			final int FLING_MIN_DISTANCE = 250;
			final int FLING_MIN_VELOCITY = 500;
			if ((e1.getX() - e2.getX()) > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {//fling left
				if (!slidingDrawer.isOpened()) {
					llHandler.performClick();
				}
				return true;
			} else if ((e1.getX() - e2.getX()) < -FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {//fling right
				if (slidingDrawer.isOpened()) {
					llHandler.performClick();
				} else {
					finish();
				}
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector != null) {
			return gestureDetector.onTouchEvent(event);
		}
		return super.onTouchEvent(event);
	}

}
