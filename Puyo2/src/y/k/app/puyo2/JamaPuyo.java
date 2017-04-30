package y.k.app.puyo2;

import static y.k.app.puyo2.Constant.*;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Rect;

public class JamaPuyo extends TopStage{
	private Puyo		viewJamaPuyo;	/**上部に表示するじゃまぷよ**/
	private int		totalNum;		/**落ちてくる予定のじゃまぷよの数**/
	private boolean	dropFlag;		/**続けて落とすぷよがあるかの判定用**/
	private int		dropNum;		/**一度に落ちてくるじゃまぷよの数**/
	private int		tempNum;		/**送られてくるじゃまぷよの数を一時保管**/
	/**
	 *コンストラクタ
	 * @param player
	 * @param point_x
	 * @param point_y
	 */
	public JamaPuyo() {
		super();
		clear();
	}

	/**
	 *
	 */
	public void clear(){
		dropFlag=false;
		totalNum=0;
		dropNum=0;
		tempNum=0;
	}
	/**
	 *自分の表示用変数を設定
	 * @param left
	 * @param right
	 * @param bottom
	 */
	public void setDrawOneP(int left, int right, int bottom){
		viewJamaPuyo=new Puyo(0,left);
		setRectOneP(left, right, bottom);
		setTextPointX(left+Image.getwidth());
	}
	/**
	 *対戦相手の表示用変数を設定
	 * @param left
	 * @param right
	 * @param bottom
	 */
	public void setDrawTwoP(int left, int right, int bottom){
		viewJamaPuyo=new Puyo(1,left);
		setRectTwoP(left, right, bottom);
		setTextPointX(left+Image.getwidth()*2);

	}
	/**
	 * 対戦相手に送るじゃまぷよの数
	 * @param rensa
	 * @param eraseNum
	 * @return
	 */
	public synchronized int getSendNum(){
		addRensaNum();
		int sendNum=0;
		if(getRensaNum()>1)sendNum+=getInt(R.integer.stage_width_num);
		if(getEraseNum()>getInt(R.integer.erase_stick_num))sendNum+=(getEraseNum()-getInt(R.integer.erase_stick_num));
		if(totalNum+tempNum>0){
			if(totalNum+tempNum>sendNum) {
				if(totalNum<sendNum){
					sendNum-=totalNum;
					tempNum-=sendNum;
					totalNum=0;
				}else{
					totalNum-=sendNum;
				}
				sendNum=0;
			} else if(totalNum+tempNum==sendNum) {
				sendNum=0;
				tempNum=0;
				totalNum=0;
			} else {
				if(totalNum>0)sendNum-=totalNum;
				if(tempNum>0)sendNum-=tempNum;
				totalNum=0;
				tempNum=0;
			}
		}
		return sendNum;
	}
	/**
	 *
	 */
	public synchronized void setDropNum(){
		if(totalNum<=getInt(R.integer.drop_jama_puyo_max_num)){
			dropNum=totalNum;
		}else{
			dropNum=getInt(R.integer.drop_jama_puyo_max_num);
		}
	}
	/**
	 * じゃまぷよの数を設定
	 * @param num
	 */
	public synchronized void addNum(int num){
		this.tempNum+=num;
	}

	/**
	 *追加のじゃまぷよを落とすか判定
	 * @return
	 */
	public synchronized boolean jugAddDrop(){
		if(dropNum>0)
			return true;
		else {
			return false;
		}
	}

	/**
	 * 落とすじゃまぷよを作成
	 * @param stage
	 * @return
	 */
	public synchronized List<Puyo> getJamaPuyo(int[] x,Rect rect){
		List<Puyo> jamaPuyo = new ArrayList<Puyo>();
		for(int i=0;i<x.length;i++){
			if(totalNum==0 || dropNum==0)break;
			totalNum--;
			dropNum--;
			if(x[i]!=-1)jamaPuyo.add(new Puyo(x[i],0,rect.left,rect.top));
		}
		return jamaPuyo;
	}

	/**
	 * じゃまぷよを落とすかどうか判定
	 * @return
	 */
	public synchronized boolean jugDrop(){
		if(!dropFlag&&totalNum>0){
			dropFlag=true;
			return true;
		} else {
			dropFlag=false;
			return false;
		}
	}
	/**
	 * 落ちている最中か判定
	 * @return
	 */
	public synchronized boolean getDropFlag(){
		return dropFlag;
	}

	/**
	 * dropFlagを設定
	 * @param dropFlag
	 */
	public synchronized void setDropFlag(boolean dropFlag){
		this.dropFlag=dropFlag;
	}

	/**
	 * 一時保管されていた送られてきたじゃまぷよの数をじゃまぷよの数に統合
	 */
	public synchronized void setNum(){
		if(tempNum>0){
			totalNum+=tempNum;
			tempNum=0;
		}
	}
	/**
	 * じゃまぷよの数を設定
	 * @param num
	 */
	public synchronized void setNum(int num){
		this.totalNum=num;
	}

	/**
	 * ステージトップのじゃまぷよの個数表示描画
	 */
	public void draw(Canvas canvas){
		canvas.drawRect(getRect(), getBackPaint());
		viewJamaPuyo.draw(canvas);
		canvas.drawText(getStr(R.string.kakeru)+(totalNum+tempNum), getTextPointX(), getTextPointY(), getTxtPaint());
	}

}
