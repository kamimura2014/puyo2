package y.k.app.puyo2;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class PuyoView extends SurfaceView implements SurfaceHolder.Callback{
	private Game			game;
	private Resources		resource;
	private Canvas			canvas;
	private boolean		drawFlag;
	private SurfaceHolder	holder;//サーフェイスホルダー
	/**
	 *
	 * @param context
	 * @param game_type
	 */
	public PuyoView(Context context,Game game) {
		super(context);
		resource=context.getResources();
		holder=getHolder();
		holder.addCallback(this);
		holder.setFixedSize(getWidth(),getHeight());
		this.game = game;
		setFocusable(true); // タッチイベントとトラックボールイベントを使うために必須
		drawFlag=true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {


	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawFlag=true;
		game.setWindowsSize(getWidth(), getHeight());
		game.initialization(this);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		game.viewDestroyed();
	}


	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(game.onTouchEventAction(event.getX(),event.getY(),event.getAction()))drawFlag=true;
		return true;
	}
	/**
	 *
	 */
	public Resources getResources(){
		return resource;
	}

	/**
	 * 描画判定を真にする
	 */
	public void setDrawFlagTrue(){
		if(!drawFlag)drawFlag=true;
	}

	/**
	 * 描画
	 */
	public void draw(){
//		Log.d("debag", "PuyoView:HeapSize:"+Runtime.getRuntime().maxMemory()+":"+
//						"Native:"+Debug.getNativeHeapAllocatedSize()+":"+
//						"memmory:"+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
		if(drawFlag){
			canvas=holder.lockCanvas();
			game.draw(canvas);
			holder.unlockCanvasAndPost(canvas);
			drawFlag=false;
		}
	}
}
