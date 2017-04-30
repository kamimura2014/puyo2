package y.k.app.puyo2;

import static y.k.app.puyo2.Constant.*;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Score extends TopStage{
	/****/
	private int score;

	/**
	 *
	 * @param player
	 * @param point_x
	 * @param point_y
	 */
	public Score(Rect stage_rect) {
		super();
		setRectOneP(stage_rect.left,stage_rect.right,stage_rect.top);
		setTextPointX(stage_rect.left);
		score=0;
	}

	/**
	 * スコアの計算
	 * @param rensa
	 * @param erase_num
	 */
	public void calculate(){
		addRensaNum();
		if(getRensaNum()==1)
			score+=10;
		else
			score+=100*getRensaNum();
		score+=10*(getEraseNum()-getInt(R.integer.erase_stick_num));
	}

	/**
	 * スコアの描画
	 */
	public void draw(Canvas canvas){
		canvas.drawRect(getRect(), getBackPaint());
		canvas.drawText("SCORE: "+score, getTextPointX(), getTextPointY(), getTxtPaint());
	}

	public void clear() {
		score=0;
	}
}
