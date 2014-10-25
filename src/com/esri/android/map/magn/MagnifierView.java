package com.esri.android.map.magn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;
import com.esri.android.map.MapView;

public class MagnifierView extends View {
	private static final int canvasop = 31;
	private static final float magnifyRatio = 2.0F; // 放大比例
	private Bitmap background;
	private boolean draw = false;
	private final Bitmap lens;
	private int lensWinH;
	private int lensWinW;
	private MapView mapView;
	private final Bitmap mask;
	private int vertOffset = 20;
	private float x;
	private float y;

	public MagnifierView(Context paramContext, MapView paramMapView) {
		super(paramContext);
		this.mapView = paramMapView;
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inPurgeable = true; // 存储pixel的内存可回收
		this.lens = BitmapFactory.decodeResource(getResources(),
				R.drawable.magnify_lens, localOptions);
		this.lensWinW = this.lens.getWidth();
		this.lensWinH = this.lens.getHeight();
		this.mask = Bitmap.createBitmap(this.lensWinW, this.lensWinH,
				Bitmap.Config.ARGB_8888);
		Paint localPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 抗锯齿
		localPaint.setColor(Color.RED);
		new Canvas(this.mask).drawOval(
				new RectF(8.0F, 8.0F, this.lens.getWidth() - 9, this.lens
						.getHeight() - 9), localPaint); // 绘制范围的圆
	}

	/**
	 * 隐藏放大镜
	 */
	public void hide() {
		this.draw = false;
		try {
			this.background.recycle();
			invalidate();
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDraw(Canvas paramCanvas) {
		if (!this.draw)
			return;
		try {
			Paint localPaint = new Paint();
			localPaint.setFilterBitmap(false);
			localPaint.setStyle(Paint.Style.FILL);
			int i = paramCanvas.saveLayer(2.0F + this.x, 2.0F + this.y, this.x
					+ this.lens.getWidth() - 2.0F,
					this.y + this.lens.getHeight() - 2.0F, null, canvasop);
			paramCanvas.translate(this.x, this.y);

			if ((this.background != null) && (!this.background.isRecycled()))
				paramCanvas.drawBitmap(this.background, 0.0F, 0.0F, localPaint);
			localPaint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.DST_IN));
			paramCanvas.drawBitmap(this.mask, 0.0F, 0.0F, localPaint);
			localPaint.setXfermode(null);
			paramCanvas.drawBitmap(this.lens, 0.0F, 0.0F, localPaint);
			paramCanvas.restoreToCount(i);
		} catch (Exception e) {
		}
	}

	/**
	 * 预备绘制mapview指定域(左上角)缓存
	 * 
	 * @param paramFloat1
	 *            X
	 * @param paramFloat2
	 *            Y
	 */
	public void prepareDrawingCacheAt(float paramFloat1, float paramFloat2) {
		float f1 = this.lensWinW / 2.0F;
		float f2 = this.lensWinH / 2.0F;
		Bitmap localBitmap = this.mapView.getDrawingMapCache(paramFloat1 - f1
				/ 2.0F, paramFloat2 - f2 / 2.0F, (int) f1, (int) f2);
		try {
			if (localBitmap != null) {
				this.draw = true;
				this.background = Bitmap.createScaledBitmap(localBitmap,
						(int) (2.0F * localBitmap.getWidth()),
						(int) (2.0F * localBitmap.getHeight()), false);
				this.x = (paramFloat1 - this.lensWinW / 2);
				this.y = (paramFloat2 - this.vertOffset - this.lensWinH);
				invalidate();
			}
		} catch (Exception e) {
		}
	}
}
