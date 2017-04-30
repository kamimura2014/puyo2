package y.k.app.puyo2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public abstract class TopStage {
	private float textPointX;
	private float	textPointY;
	private Rect 	rect;
	private Paint	txtPaint;
	private Paint	backPaint;
	private int	rensaNum;						/**消した回数を入れる**/
	private int	eraseNum;						/**消したぷよの数を入れる**/
	/**
	 * コンストラクタ
	 */
	public TopStage() {
		textPointY=Image.getheight()*2;
		txtPaint=new Paint();
		txtPaint.setColor(Color.GREEN);
		txtPaint.setTextSize(Image.getheight()/2);
		backPaint=new Paint();
		backPaint.setStyle(Paint.Style.FILL);
		backPaint.setColor(Color.GRAY);
	}
	/**
	 * テキストの横の表示位置を渡す
	 * @return
	 */
	protected float getTextPointX() {
		return textPointX;
	}
	/**
	 * テキストの横の表示位置を設定する
	 * @param textPointX
	 */
	protected void setTextPointX(float textPointX) {
		this.textPointX = textPointX;
	}
	/**
	 * テキストの縦の表示位置を渡す
	 * @return
	 */
	protected float getTextPointY() {
		return textPointY;
	}
	/**
	 * ステージ上部の表示範囲用rectを渡す
	 * @return
	 */
	protected Rect getRect() {
		return rect;
	}
	/**
	 * バックグラウンド用のペイントを渡す
	 * @return
	 */
	protected Paint getBackPaint() {
		return backPaint;
	}
	/**
	 * テキスト表示用のペイントを渡す
	 * @return
	 */
	protected Paint getTxtPaint() {
		return txtPaint;
	}
	/**
	 *連鎖数を初期化する
	 */
	public void initRensaNum(){
		rensaNum=0;
	}
	/**
	 *消したぷよの数を初期化する
	 */
	public void initEraseNum(){
		eraseNum=0;
	}
	/**
	 *消したぷよの数を加算する。
	 */
	public void addEraseNum(){
		eraseNum++;
	}
	/**
	 *連鎖数を加算する
	 */
	protected void addRensaNum(){
		rensaNum++;
	}
	/**
	 * one_playerの場合の描画rect設定
	 * @param left
	 * @param right
	 * @param bottom
	 */
	protected void setRectOneP(int left,int right,int bottom){
		rect=new Rect(
				left,
				0,
				right-Image.getwidth(),
				bottom);
	}
	/**
	 * 連鎖数を渡す
	 * @return
	 */
	protected int getRensaNum() {
		return rensaNum;
	}

	/**
	 * 消したぷよの数を渡す
	 * @return
	 */
	protected int getEraseNum(){
		return eraseNum;
	}

	/**
	 * two_playerの場合の描画rect設定
	 * @param left
	 * @param right
	 * @param bottom
	 */
	protected void setRectTwoP(int left,int right,int bottom){
		rect=new Rect(
				left+Image.getwidth(),
				0,
				right,
				bottom);
	}
	/**
	 *  ステージ上部を描画
	 * @param canvas
	 */
	public abstract void draw(Canvas canvas);

	/**
	 *  ステージ上部の情報を削除
	 * @param canvas
	 */
	public abstract void clear();
}
