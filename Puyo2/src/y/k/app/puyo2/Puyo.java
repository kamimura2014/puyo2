package y.k.app.puyo2;

import android.graphics.Canvas;
import android.graphics.Rect;
/**
 *
 * @author V2
 *
 */
public class Puyo {

	private int
		x,y/**ぷよの位置*/,
		image/**ぷよの描画イメージ*/;
	private Rect src;/***/
	private Rect dst;/***/

	/**
	 * 次に落ちてくるぷよ用
	 * @param x
	 * @param y
	 * @param sw
	 * @param sh
	 * @param image
	 */
	public Puyo(int x,int y,int sw,int sh,int image){
		setX(x);
		setY(y);
		this.setImage(image);
		setRect(sw, sh);
	}

	/**
	 * ゲーム最初に作る場合
	 * @param sw
	 * @param sh
	 * @param puyo
	 */
	public Puyo(int x,int sw,int sh,Puyo puyo){
		setX(x);
		setY(puyo.getY()-2);
		this.setImage(puyo.getImage());
		setRect(sw, sh);
	}
	/**
	 * じゃまぷよの数表示用
	 * @param sw
	 * @param play_type
	 */
	public Puyo(int x,int sw){
		setX(x);
		setY(1);
		this.setImage(R.drawable.jyama_puyo);
		setRect(sw, 0);
	}

	/**
	 * じゃまぷよ用
	 * @param x
	 * @param y
	 * @param sw
	 * @param sh
	 */
	public Puyo(int x,int y,int sw,int sh){
		setX(x);
		setY(y);
		this.setImage(R.drawable.jyama_puyo);
		setRect(sw, sh);
	}

	/**
	 *
	 * @param sw
	 * @param sh
	 */
	private void setRect(int sw,int sh){
		dst=new Rect(
				sw+this.x*Image.getwidth(),    sh+this.y*Image.getheight(),
				sw+(this.x+1)*Image.getwidth(),sh+(this.y+1)*Image.getheight());
		if(image==0){
			src = new Rect(0,0,Image.getBitmap(R.drawable.puyo1).getWidth(),Image.getBitmap(R.drawable.puyo1).getHeight());
		}else{
			src = new Rect(0,0,Image.getBitmap(image).getWidth(),Image.getBitmap(image).getHeight());
		}
	}

	/**
	 *
	 * @return
	 */
	public Rect getRect(){
		return dst;
	}
	/**
	 *
	 * @param x
	 */
	private void setX(int x){
		this.x=x;
	}

	/**
	 * ｘ軸を渡す
	 * @return
	 */
	public synchronized int getX(){
		return x;
	}

	/**
	 *
	 * @param y
	 */
	private void setY(int y){
		this.y=y;
	}

	/**
	 * ｙ軸を渡す
	 * @return
	 */
	public synchronized int getY(){
		return y+1;
	}
	/**
	 * イメージを渡す
	 * @return
	 */
	public synchronized int getImage(){
		return image;
	}
	/**
	 * イメージをセットする
	 * @param image
	 */
	public synchronized void setImage(int image){
		this.image=image;
	}

	/**
	 * 描画の必要がなくなったかどうかを判定
	 * @param x
	 * @param y
	 * @return
	 */
	public synchronized boolean jugRemove(int x,int y){
		if(getX()==x&&getY()==y)
			return true;
		else
			return false;
	}

	/**
	 * ぷよを描画する
	 * @param canvas
	 */
	public synchronized void draw(Canvas canvas){
		if(y<0|image==0) return;
		canvas.drawBitmap(Image.getBitmap(image),src,dst,null);
	}

	/**
	 * ぷよの更新
	 * @param w
	 * @param h
	 */
	public synchronized void update(int w,int h){
		x+=w;
		y+=h;
		dst.offset(w*Image.getwidth(), h*Image.getheight());
	}
}