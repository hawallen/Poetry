package com.hawallen.poetry;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.hawallen.poetry.bean.Word;
import com.hawallen.poetry.layout.TipsDialog;
import com.hawallen.poetry.util.FileService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

	private ViewFlipper vfMain;
	private GestureDetector gestureDetector;
	private Animation leftIn, leftOut, rightIn, rightOut;
	
	private Word[] words;
	
	private final int REQUEST_CODE = 060243;
	public static final int OK = 1;
	public static final int FINISH = 0;
	
	private boolean isStarted = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		vfMain = (ViewFlipper) findViewById(R.id.vf_main);
		ImageView imageView = new ImageView(this);
		imageView.setBackgroundResource(R.drawable.main_bg1);
		vfMain.addView(imageView);
		int index = (int) (Math.random()*vfMain.getChildCount());
		for (int i = 0; i < index; i++) {
			vfMain.showNext();
		}
		
		MyAsyncTask mAsyncTask = new MyAsyncTask();
		mAsyncTask.execute("");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE) {
			isStarted = true;
			if (resultCode == OK) {
				leftIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_left_in);
				leftOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_left_out);
				rightIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_in);
				rightOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_out);
				gestureDetector = new GestureDetector(new DefaultGestureDetector());
//				vfMain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_top_in));
			} else if (resultCode == FINISH) {
				PoetryActivity.favours.clear();
				finish();
			}
			
		}
	}
	
	@Override
	public void onBackPressed() {
		if (isStarted) {
			final TipsDialog tipsDialog = new TipsDialog(MainActivity.this, R.style.MyDialog);
			tipsDialog.show();
			tipsDialog.setTipsText(getResources().getString(R.string.tips_quit));
			tipsDialog.setOnOKClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					tipsDialog.dismiss();
					PoetryActivity.favours.clear();
					finish();
				}
			});
		}
	}
	
	private class MyAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				AssetManager assetManager = getAssets();
				String[] list = assetManager.list("words");
				words = new Word[list.length];
				InputStreamReader inputReader = null;
				BufferedReader bufReader = null;
				for (int i = 0; i < words.length; i++) {
					inputReader = new InputStreamReader(assetManager.open("words/" + list[i]), "gb2312");
					bufReader = new BufferedReader(inputReader);
					String line = "";
					String result = "";
					while ((line = bufReader.readLine()) != null) {
						result += line;
					}
					Word word = new Gson().fromJson(result, Word.class);
					MainActivity.this.words[i] = word;
					inputReader.close();
					bufReader.close();
				}
				
				PoetryActivity.words = words;
				String[] favourList = FileService.getFavourFileList(MainActivity.this);
				for (int i = 0; i < favourList.length; i++) {
					String fileName = favourList[i].substring(0, favourList[i].lastIndexOf("."));
					PoetryActivity.favours.add(FileService.getFavour(MainActivity.this, fileName));
				}
				Thread.sleep(2000);
				Intent intent = new Intent(MainActivity.this, PoetryActivity.class);
				startActivityForResult(intent, REQUEST_CODE);
//				finish();
			} catch (IOException e) {
				System.out.println("Open Assets Exception");
			} catch (InterruptedException e) {
				System.out.println("Thread InterruptedException");
			}
			return null;
		}
	}
	
	private class DefaultGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			final int FLING_MIN_DISTANCE = 200;
			final int FLING_MIN_VELOCITY = 500;
			final int SCROLLING_MIN_DISTANCE = 300;
			final int SCROLLING_MIN_VELOCITY = 500;
			if ((e1.getX() - e2.getX()) > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {//fling left
				vfMain.setInAnimation(rightIn);
				vfMain.setOutAnimation(rightOut);
				vfMain.showNext();
				return true;
			} else if ((e1.getX() - e2.getX()) < -FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {//fling right
				vfMain.setInAnimation(leftIn);
				vfMain.setOutAnimation(leftOut);
				vfMain.showPrevious();
				return true;
			} else if (e1.getY() -  e2.getY()> SCROLLING_MIN_DISTANCE && Math.abs(velocityY) > SCROLLING_MIN_VELOCITY) {//fling up
				Intent intent = new Intent(MainActivity.this, PoetryActivity.class);
				startActivityForResult(intent, REQUEST_CODE);
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
