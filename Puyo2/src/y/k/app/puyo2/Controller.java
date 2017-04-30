package y.k.app.puyo2;


import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
/**
 *
 * @author V2
 *
 */
public class Controller {

	private Rect[]		downButtonRect;
	private Rect[]		leftButtonRect;
	private Rect[]		rightButtonRect;
	private Rect[]		aButtonRect;
	private Rect[]		bButtonRect;


	/**
	 *
	 * @param w
	 * @param h
	 * @param resource
	 */
	public void initialization(int w, int h){
		this.downButtonRect= new Rect[2];
		this.downButtonRect[0] = new Rect(0,0,Image.getBitmap(R.drawable.down_button).getWidth(),Image.getBitmap(R.drawable.down_button).getHeight());
		this.downButtonRect[1] = new Rect(0,7*h/8,w/2,h);
		this.leftButtonRect= new Rect[2];
		this.leftButtonRect[0] = new Rect(0,0,Image.getBitmap(R.drawable.left_button).getWidth(),Image.getBitmap(R.drawable.left_button).getHeight());
		this.leftButtonRect[1] = new Rect(0,3*h/4,w/4,7*h/8);
		this.rightButtonRect= new Rect[2];
		this.rightButtonRect[0] = new Rect(0,0,Image.getBitmap(R.drawable.right_button).getWidth(),Image.getBitmap(R.drawable.right_button).getHeight());
		this.rightButtonRect[1] = new Rect(w/4,3*h/4,w/2,7*h/8);
		this.aButtonRect = new Rect[2];
		this.aButtonRect[0] = new Rect(0,0,Image.getBitmap(R.drawable.a_button).getWidth(),Image.getBitmap(R.drawable.a_button).getHeight());
		this.aButtonRect[1] = new Rect(3*w/4,3*h/4,w,h);
		this.bButtonRect = new Rect[2];
		this.bButtonRect[0] = new Rect(0,0,Image.getBitmap(R.drawable.b_button).getWidth(),Image.getBitmap(R.drawable.b_button).getHeight());
		this.bButtonRect[1] = new Rect(w/2,3*h/4,3*w/4,h);
	}


	/**
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public synchronized int jugControl(float x,float y){
		if(y>=aButtonRect[1].top && y<=aButtonRect[1].bottom && x>=aButtonRect[1].left && x<=aButtonRect[1].right)
			return R.id.turn_right;
		else if(y>=bButtonRect[1].top && y<=bButtonRect[1].bottom && x>=bButtonRect[1].left && x<=bButtonRect[1].right)
			return R.id.turn_left;
		else if(y>=leftButtonRect[1].top && y<=leftButtonRect[1].bottom && x>=leftButtonRect[1].left && x<=leftButtonRect[1].right)
			return R.id.move_left;
		else if(y>=rightButtonRect[1].top && y<=rightButtonRect[1].bottom && x>=rightButtonRect[1].left && x<=rightButtonRect[1].right)
			return R.id.move_right;
		else if(y>=downButtonRect[1].top && y<=downButtonRect[1].bottom && x>=downButtonRect[1].left && x<=downButtonRect[1].right)
			return R.id.move_down;
		else return R.id.move_none;
	}

	/**
	 *
	 * @param canvas
	 */
	public void draw(Canvas canvas){
		canvas.drawBitmap(Image.getBitmap(R.drawable.down_button),downButtonRect[0],downButtonRect[1],null);
		canvas.drawBitmap(Image.getBitmap(R.drawable.left_button),leftButtonRect[0],leftButtonRect[1],null);
		canvas.drawBitmap(Image.getBitmap(R.drawable.right_button),rightButtonRect[0],rightButtonRect[1],null);
		canvas.drawBitmap(Image.getBitmap(R.drawable.a_button), aButtonRect[0],aButtonRect[1],null);
		canvas.drawBitmap(Image.getBitmap(R.drawable.b_button), bButtonRect[0],bButtonRect[1],null);
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param event
	 * @return
	 */
	public boolean onTouchEventAction(Player player,float x,float y,int event){
		if(!player.getControlLock())return false;
		switch (event) {
			case MotionEvent.ACTION_DOWN:
				if(player.controlPuyo(jugControl(x, y))){
					return true;
				}else{
					return false;
				}
			case MotionEvent.ACTION_UP:
				player.endTouch();
				return false;
			case MotionEvent.ACTION_MOVE:
				int controlState=jugControl(x, y);
				if(player.getControlState()!=controlState){
					boolean rtn=player.controlPuyo(controlState);
					return rtn;
				}else{
					return false;
				}
			default:
				return false;
		}
	}


}
