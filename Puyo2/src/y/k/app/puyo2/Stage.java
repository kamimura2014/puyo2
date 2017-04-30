package y.k.app.puyo2;

import static y.k.app.puyo2.Constant.*;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 *
 * @author V2
 *
 */
public class Stage {

	private int[][]	stage;							/**ステージ上のぷよの場所**/
	private NextPuyo	nextPuyo;						/**次に落ちてくるぷよの表示用**/
	private Puyo[]		controlPuyo = new Puyo[2];		/**動かすぷよを入れる**/
	private List<Puyo>	puyo = new ArrayList<Puyo>();	/**描画用のぷよを入れる**/
	private Paint		frame=new Paint();				/**描画用**/
	private Paint		fill=new Paint();				/**描画用**/
	private boolean	controlLock;					/**ぷよの操作ができるか**/
	private JamaPuyo	jamaPuyo;						/**じゃまぷよ**/
	private TopStage	topStage;						/**ステージ上部の表示用**/
	private Rect		rect;							/**ステージの大きさ**/

	/**
	 * スコアアタックの場合
	 * @param w
	 * @param h
	 */
	public Stage(int w,int h){
		setPaint();
		this.stage = new int[getInt(R.integer.stage_height_num)][getInt(R.integer.stage_width_num)];
		this.nextPuyo=new NextPuyo(getInt(R.integer.one_player));
		this.rect=new Rect(
				w/2 - Image.getwidth()*(getInt(R.integer.stage_width_num)/2),
				Image.getheight()*2,
				w/2 + Image.getwidth()*(getInt(R.integer.stage_width_num)/2),
				(3*h)/4
				);
		nextPuyo.setNextPuyoOneP(rect.right);
		controlLock=false;
	}
	/**
	 * ゲーム終了時に変数内クリア
	 */
	public void clear(){
		clearControlPuyo();
		puyo.clear();
		clearStage();
		topStage.clear();
		nextPuyo.clear();
		controlLock=false;
	}

	/**
	 *　変数内クリア
	 */
	private void clearStage(){
		for(int i=0;i<getInt(R.integer.stage_width_num);i++){
			for(int j=0;j<getInt(R.integer.stage_height_num);j++){
				stage[j][i]=0;
			}
		}
	}
	/**
	 *　変数内クリア
	 */
	private void clearControlPuyo(){
		if(controlPuyo[0]!=null)controlPuyo[0]=null;
		if(controlPuyo[1]!=null)controlPuyo[1]=null;

	}
	/**
	 *スコアアタック以外の場合
	 * @param w
	 * @param h
	 * @param playerType
	 */
	public Stage(int w,int h,int playerType){
		setPaint();
		this.stage = new int[getInt(R.integer.stage_height_num)][getInt(R.integer.stage_width_num)];
		this.nextPuyo=new NextPuyo(getInt(playerType));
		jamaPuyo=new JamaPuyo();
		switch (playerType) {
			case R.integer.one_player:
				this.rect=new Rect(
						0,
						Image.getheight()*2,
						Image.getwidth()*getInt(R.integer.stage_width_num),
						(3*h)/4);
				nextPuyo.setNextPuyoOneP(rect.right);
				jamaPuyo.setDrawOneP(rect.left, rect.right, rect.top);
				break;
			case R.integer.two_player:
				this.rect=new Rect(
						w - (Image.getwidth()*getInt(R.integer.stage_width_num)),
						Image.getheight()*2,
						w,
						(3*h)/4);
				nextPuyo.setNextPuyoTwoP(rect.left);
				jamaPuyo.setDrawTwoP(rect.left, rect.right, rect.top);
				break;
			default:
				break;
		}
		topStage=jamaPuyo;
		controlLock=false;
	}

	/**
	 * 描画用ペイントの設定
	 */
	private void setPaint(){
		fill.setStyle(Paint.Style.FILL);
		fill.setColor(Color.BLACK);
		frame.setStyle(Paint.Style.STROKE);
		frame.setColor(Color.RED);
	}

	/**
	 * 動かすぷよを渡す
	 * @return
	 */
	public synchronized Puyo[] getControlPuyo(){
		return controlPuyo;
	}
	/**
	 * トップステージ情報を入れる
	 * @param topStage
	 */
	protected void setTopStage(TopStage topStage){
		this.topStage=topStage;
	}

	/**
	 * 初期化
	 */
	protected synchronized void setPuyo(){
		controlLock=true;
		topStage.initRensaNum();
		for(int i=0;i<2;i++){
			controlPuyo[i]=new Puyo((getInt(R.integer.stage_width_num)/2),rect.left,rect.top,nextPuyo.getNextPuyo(i));//puyo.get(puyo.size()-1);
		}
		nextPuyo.setNext();
	}
	/**
	 *
	 */
	public void setNextPuyo(){
		nextPuyo.setNext();
	}
	/**
	 * ゲームオーバーの確認
	 * @return
	 */
	protected synchronized boolean jugGameOver(){
		if(stage[1][(getInt(R.integer.stage_width_num)/2)]==0)
			return true;
		else
			return false;
	}

	/**
	 * ぷよが置けるか判定
	 * @param w
	 * @param h
	 * @return
	 */
	private boolean jugStage(int w,int h){
		if(h<getInt(R.integer.stage_height_num) && h>=0 && w<getInt(R.integer.stage_width_num) && w>=0){
			if(stage[h][w]==0)
				return true;
			else
				return false;
		}
		else
			return false;
	}

	/**
	 * ぷよを下に落とす
	 * @return
	 */
	protected synchronized boolean downPuyo(){
		if(controlLock)return true;
		boolean jug=false;
		int f_start,f_end,f_add;
		if(controlPuyo[0]!=null&&controlPuyo[1]!=null){
			if(controlPuyo[0].getY()>controlPuyo[1].getY()){
				f_start=0;
				f_end=2;
				f_add=1;
			}else{
				f_start=1;
				f_end=-1;
				f_add=-1;
			}
			for(int i=f_start;i!=f_end;i+=f_add){
				puyo.add(controlPuyo[i]);
			}
			clearControlPuyo();
		}
		for(Puyo p:puyo){
			if(jugStage(p.getX(),p.getY()+1)){
				p.update(0,1);
				if(!jug)jug=true;
			} else {
				stage[p.getY()][p.getX()]=p.getImage();
			}
		}
		return jug;
	}

	/**
	 * 追加でじゃまぷよを落とす
	 */
	protected synchronized void addJamaPuyo(){
		if(jamaPuyo!=null&&jamaPuyo.jugAddDrop())getJamaPuyo();
	}

	/**
	 * 4つ以上くっついているぷよを消す
	 * @return
	 */
	protected synchronized boolean diePuyo(){
		boolean jug=false;
		topStage.initEraseNum();
		for(int i=1;i<getInt(R.integer.stage_height_num);i++){
			for(int j=0;j<getInt(R.integer.stage_width_num);j++){
				if(stage[i][j]!=0&&stage[i][j]!=R.drawable.jyama_puyo){
					int num=0;//くっついてるぷよの数
					int tmp=stage[i][j];
					num=countPuyo(j,i,num,stage);
					if(num>=getInt(R.integer.erase_stick_num)){
						jug=true;
						erasePuyo(j, i);
					} else {
						restoreStage(j, i, tmp);
					}
				}
			}
		}
		return jug;
	}
	/**
	 * ぷよを消す
	 * @param x
	 * @param y
	 */
	private void erasePuyo(int x,int y){
		if(stage[y][x]!=-1 && stage[y][x]!=R.drawable.jyama_puyo)return;
		int tmp=stage[y][x];
		stage[y][x]=0;
		topStage.addEraseNum();
		int i=0;
		while(i!=puyo.size()){
			if(puyo.get(i).jugRemove(x,y))puyo.remove(i);
			else i++;
		}
		if(tmp==R.drawable.jyama_puyo)return;
		if(x+1 <  getInt(R.integer.stage_width_num)		){erasePuyo(x+1,y);}
		if(y+1 <  getInt(R.integer.stage_height_num)	){erasePuyo(x,y+1);}
		if(x-1 >= 0						){erasePuyo(x-1,y);}
		if(y-1 >  0						){erasePuyo(x,y-1);}
	}
	/**
	 * ステージを元に戻す
	 * @param x
	 * @param y
	 * @param image
	 */
	private void restoreStage(int x,int y,int image){
		if(stage[y][x]==-1)stage[y][x]=image;
		else return;
		if(x+1 <  getInt(R.integer.stage_width_num)		){restoreStage(x+1,y,image);}
		if(y+1 <  getInt(R.integer.stage_height_num)	){restoreStage(x,y+1,image);}
		if(x-1 >= 0						){restoreStage(x-1,y,image);}
		if(y-1 >  0						){restoreStage(x,y-1,image);}
	}

	/**
	 * くっついているぷよの個数を数える
	 * @param x
	 * @param y
	 * @param num
	 * @param stage
	 * @return
	 */
	protected int countPuyo(int x,int y,int num,int[][] stage){
		int tmp = stage[y][x];
		stage[y][x]=-1;
		num++;
		if(x+1 <  getInt(R.integer.stage_width_num)		&&	stage[y][x+1]==tmp){num=countPuyo(x+1,y,num,stage);}
		if(y+1 <  getInt(R.integer.stage_height_num)	&&	stage[y+1][x]==tmp){num=countPuyo(x,y+1,num,stage);}
		if(x-1 >= 0						&&	stage[y][x-1]==tmp){num=countPuyo(x-1,y,num,stage);}
		if(y-1 >  0						&&	stage[y-1][x]==tmp){num=countPuyo(x,y-1,num,stage);}
		return num;
	}

	/**
	 * 宙に浮かんだぷよを探す
	 */
	protected synchronized void searchDownPuyo(){
		for(Puyo p:puyo){
			if(jugStage(p.getX(), p.getY()+1)){
				stage[p.getY()][p.getX()]=0;
			}
		}
	}
	/**
	 * ぷよが動かせるか値を渡す
	 * @return
	 */
	public synchronized boolean getControlLock(){
		return controlLock;
	}
	/**
	 * ぷよが動かせるかの値を入れる
	 * @return
	 */
	protected synchronized void setControlLock(boolean control_lock){
		this.controlLock=control_lock;
	}
	/**
	 * ぷよの移動
	 * @param w
	 * @param h
	 * @return
	 */
	public boolean movePuyo(int w,int h){
		if(jugStage(controlPuyo[0].getX()+w, controlPuyo[0].getY()+h) &&jugStage(controlPuyo[1].getX()+w, controlPuyo[1].getY()+h)){
			for(int i=0;i<controlPuyo.length;i++)controlPuyo[i].update(w,h);
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 *ぷよの右回転
	 */
	public synchronized void turnRightPuyo(){
		int	turn_x = controlPuyo[0].getX()-controlPuyo[1].getX();
		int	turn_y = controlPuyo[0].getY()-controlPuyo[1].getY();
		if(turn_x==0&&jugStage(controlPuyo[1].getX()+turn_y, controlPuyo[1].getY()+turn_y)){
			controlPuyo[1].update(turn_y, turn_y);
		} else if(turn_x==0&&jugStage(controlPuyo[0].getX()-turn_y, controlPuyo[0].getY())&&jugStage(controlPuyo[1].getX(), controlPuyo[1].getY()+turn_y)){
			controlPuyo[0].update(-turn_y, 0);
			controlPuyo[1].update(0, turn_y);
		} else if(turn_y==0&&jugStage(controlPuyo[1].getX()+turn_x, controlPuyo[1].getY()-turn_x)){
			controlPuyo[1].update(turn_x, -turn_x);
		} else if(turn_y==0&&jugStage(controlPuyo[0].getX(), controlPuyo[0].getY()+turn_x)&&jugStage(controlPuyo[1].getX()+turn_x, controlPuyo[1].getY())){
			controlPuyo[0].update(0, turn_x);
			controlPuyo[1].update(turn_x, 0);
		} else {
			controlPuyo[0].update(-turn_x, -turn_y);
			controlPuyo[1].update(turn_x, turn_y);
		}
	}
	/**
	 * ぷよの左回転
	 */
	public synchronized void turnLeftPuyo(){
		int	turn_x = controlPuyo[0].getX()-controlPuyo[1].getX();
		int	turn_y = controlPuyo[0].getY()-controlPuyo[1].getY();
		if(turn_x==0&&jugStage(controlPuyo[1].getX()-turn_y, controlPuyo[1].getY()+turn_y)){
			controlPuyo[1].update(-turn_y, turn_y);
		} else if(turn_x==0&&jugStage(controlPuyo[0].getX()+turn_y, controlPuyo[0].getY())&&jugStage(controlPuyo[1].getX(), controlPuyo[1].getY()+turn_y)){
			controlPuyo[0].update(turn_y, 0);
			controlPuyo[1].update(0, turn_y);
		} else if(turn_y==0&&jugStage(controlPuyo[1].getX()+turn_x, controlPuyo[1].getY()+turn_x)){
			controlPuyo[1].update(turn_x, turn_x);
		} else if(turn_y==0&&jugStage(controlPuyo[0].getX(), controlPuyo[0].getY()+turn_x)&&jugStage(controlPuyo[1].getX()+turn_x, controlPuyo[1].getY())){
			controlPuyo[0].update(0, -turn_x);
			controlPuyo[1].update(turn_x, 0);
		}else {
			controlPuyo[0].update(-turn_x, -turn_y);
			controlPuyo[1].update(turn_x, turn_y);
		}
	}

	/**
	 * ステージ情報描画
	 * @param canvas
	 */
	public synchronized void draw(Canvas canvas){
		canvas.drawRect(rect, fill);
		topStage.draw(canvas);
		nextPuyo.draw(canvas);
		//ぷよの描画
		if(controlPuyo[0]!=null&&controlPuyo[1]!=null){
			for(Puyo p:controlPuyo){
				p.draw(canvas);
			}
		}
		for(Puyo p:puyo){
			p.draw(canvas);
		}
		canvas.drawRect(rect,frame);
	}

	/**
	 * 落とすじゃまぷよを作成
	 */
	private synchronized void getJamaPuyo(){
		int[] highestTmp = new int[getInt(R.integer.stage_width_num)];
		for(int i=0;i<highestTmp.length;i++){
			highestTmp[i]=-1;
			while(jugStage(i, highestTmp[i]+1)){
				highestTmp[i]+=1;
			}
			if(highestTmp[i]==0)highestTmp[i]=-1;
		}
		int [] x=new int[getInt(R.integer.stage_width_num)];
		int highest;
		for(int i=0;i<x.length;i++){
			x[i]=-1;
			highest=getInt(R.integer.stage_height_num);
			for(int j=0;j<highestTmp.length;j++){
				if(highestTmp[j]!=-1&&highest>highestTmp[j]){
					highest=highestTmp[j];
					x[i]=j;
				}
			}
			if(x[i]!=-1)highestTmp[x[i]]=-1;
		}
		for(Puyo p:jamaPuyo.getJamaPuyo(x,rect)){
			if(jugStage(p.getX(), p.getY()))puyo.add(p);
		}
	}
	/**
	 * 落とすじゃまぷよを作成
	 */
	protected synchronized void createJamaPuyo(){
		jamaPuyo.setDropNum();
		getJamaPuyo();
	}

	/**
	 * じゃまぷよを下に落とす
	 * @return
	 */
	protected synchronized boolean downJamaPuyo(int time){
		if(!controlLock&&time!=0){
			if(getDropJamaPuyoFlag()&&downPuyo()){
				addJamaPuyo();
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	/**
	 *対戦相手から送られてくるじゃまぷよをセットする
	 * @param num じゃまぷよの数
	 */
	public synchronized void addJamaPuyoNum(int num){
		jamaPuyo.addNum(num);
	}

	/**
	 * 一時保管のじゃまぷよの数とじゃまぷよの数を統合
	 */
	public void setJamaPuyoNum(){
		jamaPuyo.setNum();
	}
	/**
	 * じゃまぷよの数を設定する
	 */
	public synchronized void setJamaPuyoNum(int num){
		jamaPuyo.setNum(num);
	}

	/**
	 *じゃまぷよを落とせるか判定
	 * @return
	 */
	protected synchronized boolean jugDropJamaPuyo(Stage stage){
		stage.setJamaPuyoNum();
		return jamaPuyo.jugDrop();
	}
	/**
	 * じゃまぷよを落としている最中か判定
	 * @return
	 */
	protected boolean getDropJamaPuyoFlag(){
		return jamaPuyo.getDropFlag();
	}

	/**
	 * dropFlagを設定
	 * @param dropFlag
	 */
	public void setDropJamaPuyoFlag(boolean dropFlag){
		jamaPuyo.setDropFlag(dropFlag);
	}
	/**
	 *相手に送るじゃまぷよの数
	 * @return
	 */
	protected int sendJamaPuyoNum(){
		return jamaPuyo.getSendNum();
	}

	/**
	 *ステージの描画範囲を渡す
	 * @return
	 */
	protected synchronized Rect getRect(){
		return rect;
	}

	/**
	 * ステージを渡す
	 * @return
	 */
	protected synchronized int[][] getStage(){
		return stage;
	}

	/**
	 * ぷよのコントロール
	 * @param controlState
	 * @return
	 */
	protected synchronized boolean controlPuyo(int controlState){
		boolean rtn;
		switch (controlState) {
		case R.id.turn_right:
			turnRightPuyo();
			rtn=true;
			break;
		case R.id.turn_left:
			turnLeftPuyo();
			rtn=true;
			break;
		case R.id.move_left:
			rtn=movePuyo(-1, 0);
			break;
		case R.id.move_right:
			rtn=movePuyo(1, 0);
			break;
		case R.id.move_down:
			rtn =movePuyo(0, 1);
			if(!rtn)controlLock=false;
			break;
		default:
			rtn=false;
			break;
		}
		return rtn;
	}
}
