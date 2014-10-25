package com.esri.android.map.magn;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Point;

public class MagnifiterActivity extends Activity implements
		OnStatusChangedListener {

	MotionTouchListener motionTouchListener;

	MapView mMapView = null;
	ArcGISTiledMapServiceLayer tileLayer;

	Handler MapHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {

				Point mapPoint = (Point) msg.obj;
				Toast.makeText(
						MagnifiterActivity.this,
						"地图选取点坐标是 ：X , " + mapPoint.getX() + " Y , "
								+ mapPoint.getY(), 2000).show();
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Retrieve the map and initial extent from XML layout
		mMapView = (MapView) findViewById(R.id.map);
		/* create a @ArcGISTiledMapServiceLayer */
		tileLayer = new ArcGISTiledMapServiceLayer(
				"http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer");
		// Add tiled layer to MapView
		mMapView.addLayer(tileLayer);
		mMapView.setOnStatusChangedListener(this);

		motionTouchListener = new MotionTouchListener(this, mMapView,
				MapHandler); // 选点监听

	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.unpause();
	}

	@Override
	public void onStatusChanged(Object source, STATUS status) {
		// TODO Auto-generated method stub
		if (status == STATUS.INITIALIZED) { // 初始化成功

		} else if (status == STATUS.LAYER_LOADED) { // 图层加载成功

			if (mMapView.isLoaded()) {

				Toast.makeText(MagnifiterActivity.this, "长按地图选取位置点 !", 2000)
						.show();

				motionTouchListener.setMotionTouch(true);
				mMapView.setOnTouchListener(motionTouchListener);

			}

		} else if ((status == STATUS.INITIALIZATION_FAILED)) { // 初始化失败

		} else if ((status == STATUS.LAYER_LOADING_FAILED)) { // 图层加载失败
		}

	}

}