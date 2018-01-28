package com.hawallen.poetry;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hawallen.poetry.bean.Favour;
import com.hawallen.poetry.bean.Poetry;
import com.hawallen.poetry.bean.Word;
import com.hawallen.poetry.layout.PoetryOne;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PoetryActivity extends Activity {

	public SensorManager sensorManager;
	private SensorEventListener sensorEventListener;
	private Handler handler;
	private final int MSG_SENSOR_SHAKE = 060243;
	private final int MSG_REMOVE_VIEW = 060244;
	private MediaPlayer mMediaPlayer;

	private GestureDetector gestureDetector;
	
	private ScrollView svPoetries;
	private LinearLayout llPoetries;
	private Button btnFavour;
	private Button btnClear;
	private TextView tvPageName;

	public static Word[] words;
	public static List<Favour> favours = new ArrayList<Favour>();
	public static List<String> produceDates = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poetry);

		init();
		operate();
	}

	private void init() {
		svPoetries = (ScrollView) findViewById(R.id.sv_poetries);
		svPoetries.setScrollbarFadingEnabled(true);
		svPoetries.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return onTouchEvent(event);
			}
		});
		llPoetries = (LinearLayout) findViewById(R.id.ll_poetries);
		btnFavour = (Button) findViewById(R.id.btn_favour);
		btnClear = (Button) findViewById(R.id.btn_clear);
		tvPageName = (TextView) findViewById(R.id.tv_page_name);
		tvPageName.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				event.setEdgeFlags(MotionEvent.EDGE_TOP);
				return onTouchEvent(event);
			}
		});
		gestureDetector = new GestureDetector(new DefaultGestureDetector());
	}

	private void operate() {
		mMediaPlayer = MediaPlayer.create(PoetryActivity.this, R.raw.drip);
		sensorEventListener = new MySensorEventListener();
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		handler = new MyHandler();

		MyButtonOnClickListener mOnClickListener = new MyButtonOnClickListener();
		btnFavour.setOnClickListener(mOnClickListener);
		btnClear.setOnClickListener(mOnClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.menu_quit) {
			setResult(MainActivity.FINISH);
			finish();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onBackPressed() {
		setResult(MainActivity.OK);
//		llPoetry.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_top_out));
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
		if (sensorManager != null && words != null) {
			sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		}
		for (int i = 0; i < llPoetries.getChildCount(); i++) {
			PoetryOne poetryOne = (PoetryOne) llPoetries.getChildAt(i);
			if (produceDates.contains(poetryOne.getPoetry().produceDate)) {
				poetryOne.setFavorState(-1);
			}
		}
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
		if (sensorManager != null) {
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

	private class MySensorEventListener implements SensorEventListener {
		public void onSensorChanged(SensorEvent event) {
			float[] values = event.values;
			float x = values[0];
			float y = values[1];
			float z = values[2];
			int medumValue = 12;
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
				try {
					if (sensorManager != null) {
						sensorManager.unregisterListener(sensorEventListener);
					}
					Thread.sleep(800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = MSG_SENSOR_SHAKE;
				handler.sendMessage(msg);
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	}

	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SENSOR_SHAKE:
				PoetryOne poetryOne = (PoetryOne) View.inflate(PoetryActivity.this, R.layout.poetry_one, null);
				poetryOne.showPoetry(producePoetry(), -1);
				llPoetries.addView(poetryOne, 0);
				svPoetries.setSmoothScrollingEnabled(true);
				svPoetries.smoothScrollTo(0, 0);
				if (mMediaPlayer != null) {
					mMediaPlayer.start();
				}
				if (sensorManager != null && words != null) {
					sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
				}
				break;
			case MSG_REMOVE_VIEW:
				llPoetries.removeAllViews();
			}

		}
	};

	private Poetry producePoetry() {
		Poetry poetry = new Poetry();
		String[] poetryStrings = new String[2];
		int sentence = (int) (Math.random() * 3 + 4);
		int[] words = new int[sentence];
		int[] combination = new int[sentence];
		for (int i = 0; i < words.length; i++) {
			words[i] = (int) (Math.random() * 2 + 2);
			combination[i] = (int) (Math.random() * 2);
		}

		for (int i = 0; i < poetryStrings.length; i++) {
			String part = "";
			for (int j = 0; j < sentence; j++) {
				switch (words[j]) {// words number in each sentence
				case 2: // combination way in each sentence, word2+word2 or
						// word2+word3
					part += getWord(part, 0);
					switch (combination[j]) {
					case 0:
						part += getWord(part, 0);
						break;
					case 1:
						part += getWord(part, 1);
						break;
					}
					break;
				case 3: // combination way in each sentence, word2+word2+word3
					part += getWord(part, 0);
					part += getWord(part, 0);
					part += getWord(part, 1);
					break;
				}
				part += "，";
			}
			poetryStrings[i] = part.substring(0, part.lastIndexOf("，")) + "。";
		}
		poetry.poetryStrings = poetryStrings;
		poetry.produceDate = String.valueOf(Calendar.getInstance().getTimeInMillis());
		return poetry;
	}

	private String getWord(String part, int index) {
		String word = "";
		if (words.length > index) {
			word = words[index].wordStrings[(int) (Math.random() * words[index].wordStrings.length)];
			while (part.contains(word)) {
				word = words[index].wordStrings[(int) (Math.random() * words[index].wordStrings.length)];
			}
		}
		return word;
	}

	private class MyButtonOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.btn_favour:
				Intent intent = new Intent(PoetryActivity.this, FavourActivity.class);
				intent.putExtra("pageName", tvPageName.getText().toString());
				produceDates.clear();
				startActivity(intent);
				break;
			case R.id.btn_clear:
				for (int i = 0; i < llPoetries.getChildCount(); i++) {
					PoetryOne poetryOne = (PoetryOne) llPoetries.getChildAt(i);
					poetryOne.visibility = View.GONE;
					if (poetryOne.getVisibility() == View.VISIBLE) {
						poetryOne.startAnimation(AnimationUtils.loadAnimation(PoetryActivity.this, R.anim.popup_exit));
					}
				}
				break;
			}
		}
	}

	private class DefaultGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			final int FLING_MIN_DISTANCE = 250;
			final int FLING_MIN_VELOCITY = 500;
			final int SCROLLING_MIN_DISTANCE = 300;
			final int SCROLLING_MIN_VELOCITY = 500;
			if ((e1.getX() - e2.getX()) > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {//fling left
				btnFavour.performClick();
				return true;
			} else if ((e2.getY() -  e1.getY()) > SCROLLING_MIN_DISTANCE && Math.abs(velocityY) > SCROLLING_MIN_VELOCITY) {//fling down
				if (e1.getEdgeFlags() == MotionEvent.EDGE_TOP) {
					onBackPressed();
					return true;
				}
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
