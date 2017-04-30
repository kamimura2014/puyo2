package y.k.app.puyo2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class NextPuyo {
	private Puyo[] nextPuyo;//次に落ちてくるぷよの表示用
	private int 	playerType;
	private Paint	paint;

	/**
	 *コンストラクタ
	 * @param point_x
	 * @param playerType
	 */
	public NextPuyo(int playerType){
		this.nextPuyo = new Puyo[2];
		this.playerType=playerType;
		paint=new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);

	}
	/**
	 *自分の表示用の変数を入れる
	 * @param right
	 */
	public void setNextPuyoOneP(int right){
		this.nextPuyo[0]=new Puyo(-1,1,right,0,Image.getImage(playerType));
		this.nextPuyo[1]=new Puyo(-1,0,right,0,Image.getImage(playerType));
	}

	/**
	 *対戦相手の表示用の変数を入れる
	 * @param left
	 */
	public void setNextPuyoTwoP(int left){
		this.nextPuyo[0]=new Puyo(0,1,left,0,Image.getImage(playerType));
		this.nextPuyo[1]=new Puyo(0,0,left,0,Image.getImage(playerType));
	}

	/**
	 *
	 * @return
	 */
	public Puyo getNextPuyo(int num){
		return nextPuyo[num];
	}
	/**
	 *
	 */
	public void clear(){
		for(Puyo p:nextPuyo){p.setImage(0);}
	}
	/**
	 *
	 */
	public void setNext(){
		for(Puyo p:nextPuyo){p.setImage(Image.getImage(playerType));}
	}

	/**
	 *次に落ちてくるぷよを表示
	 * @param canvas
	 */
	public void draw(Canvas canvas){
		for(Puyo p:nextPuyo){
			canvas.drawRect(p.getRect(), paint);
			p.draw(canvas);
		}
	}
}
