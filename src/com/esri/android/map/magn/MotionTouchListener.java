package com.esri.android.map.magn;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;

/**
 * ���Ƽ���
 */
public class MotionTouchListener extends MapOnTouchListener {

	private Context context;
	private MagnifierView mag; // �Ŵ�
	private MapView mymap;
	private boolean redrawCache = true; // �Ƿ����
	private boolean isMotionTouch = true; // �Ƿ���Ӧ�¼�
	private boolean showmag = false;
	private Bitmap snapshot = null; // ����
	private Handler handler;

	public int MSG_CHOOSEPOINT = 0x01;

	public MotionTouchListener(Context paramContext, MapView paramMapView,
			Handler handler) {
		super(paramContext, paramMapView);
		this.context = paramContext;
		this.mymap = paramMapView;
		this.handler = handler;
	}

	public boolean isMotionTouch() {
		return isMotionTouch;
	}

	public void setMotionTouch(boolean isMotionTouch) {
		this.isMotionTouch = isMotionTouch;
	}

	/**
	 * �Ŵ����
	 * 
	 * @param paramMotionEvent
	 *            �����¼�
	 */
	private void magnify(MotionEvent paramMotionEvent) {
		if (this.mag == null) {
			this.mag = new MagnifierView(this.context, this.mymap);
			this.mymap.addView(this.mag);
			this.mag.prepareDrawingCacheAt(paramMotionEvent.getX(),
					paramMotionEvent.getY());
		} else {
			this.redrawCache = false;
			this.mag.prepareDrawingCacheAt(paramMotionEvent.getX(),
					paramMotionEvent.getY());
		}
	}

	/**
	 * ���϶�ʱ�Ƿ���ʾ�Ŵ�
	 */
	@Override
	public boolean onDragPointerMove(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2) {
		if (this.showmag) {
			magnify(paramMotionEvent2);
			return true;
		}
		return super.onDragPointerMove(paramMotionEvent1, paramMotionEvent2);
	}

	/**
	 * �����ɿ��¼�
	 */
	public boolean onDragPointerUp(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2) {
		if (this.showmag) {
			if (this.mag != null)
				this.mag.hide();
			Point mapPoint = this.mymap.toMapPoint(paramMotionEvent2.getX(),
					paramMotionEvent2.getY());
			this.mag.postInvalidate();
			this.showmag = false;
			this.redrawCache = true;

//			if (isMotionTouch) {
//				// this.isMotionTouch = false;
//				// �����߼�
//			} else {
//
//			}
			Message msg = Message.obtain();
			msg.what = MSG_CHOOSEPOINT;
			msg.obj = mapPoint;
			handler.sendMessage(msg);
			return true;
		}
		return super.onDragPointerUp(paramMotionEvent1, paramMotionEvent2);
	}

	/**
	 * ��������ʾ�Ŵ�
	 */
	public void onLongPress(MotionEvent paramMotionEvent) {
		super.onLongPress(paramMotionEvent);
		if (isMotionTouch) {
			magnify(paramMotionEvent);
			this.showmag = true;
		}
	}
}