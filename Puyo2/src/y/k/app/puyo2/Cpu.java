package y.k.app.puyo2;

import static y.k.app.puyo2.Constant.*;

import android.content.DialogInterface;

public class Cpu extends Stage implements DialogInterface.OnClickListener{
	private int [] x,y;//ぷよを置く座標格納用
	private int difficulty;
	private int state;

	public Cpu(int w,int h){
		super(w,h,R.integer.two_player);
		difficulty=getInt(R.integer.cpu_normal);
		x = new int[2];
		y = new int[2];
	}
	/**
	 *
	 */
	public void init(){
		difficulty=getInt(R.integer.cpu_normal);
		state=getInt(R.integer.cpu_none);
	}


	/**
	 *
	 * @return
	 */
	public synchronized boolean jugHandicap(){
		if(state==getInt(R.integer.cpu_none)&&difficulty==getInt(R.integer.cpu_hard)){
			state=getInt(R.integer.set_handicap);
			return true;
		} else {
			return false;
		}
	}
	/**
	 *
	 */
	public void startCpu(){
		if(state!=getInt(R.integer.start_cpu))state=getInt(R.integer.start_cpu);
	}
	/**
	 * 3つくっついたぷよがいくつあるか数える
	 * @return
	 */
	private int search3StickPuyoNumber(int[][] stage){
		int stickNum=0;
		int num=0;
		for(int i=0;i<getInt(R.integer.stage_width_num);i++){
			for(int j=0;j<getInt(R.integer.stage_height_num);j++){
				stickNum=0;
				if(stage[j][i]!=R.drawable.jyama_puyo&&stage[j][i]!=0&&stage[j][i]!=-1){
					stickNum=countPuyo(i, j, stickNum, stage);
					if(stickNum==3)num++;
				}
			}
		}
		return num;
	}
	/**
	 *
	 * @return
	 */
	private boolean jugDifficulty(){
		int[][] stage=getTempStage();
		for(int i=0;i<getInt(R.integer.stage_width_num);i++){
			if(stage[5][i]!=0){return false;}
		}
		if(search3StickPuyoNumber(stage)<difficulty)return true;
		else return false;
	}

	/**
	 *	CPUでのぷよの置く場所を決定
	 */
	public synchronized void getControlPuyoPoint(){
		int point=0;
		Puyo[] puyo=getControlPuyo();
		int[] x=new int[puyo.length],y=new int[puyo.length],image=new int[puyo.length];
		for(int i=0;i<puyo.length;i++){
			this.x[i]=puyo[i].getX();
			this.y[i]=puyo[i].getY();
			image[i]=puyo[i].getImage();
		}
		boolean jug=jugDifficulty();
		for(int i=0;i<getInt(R.integer.stage_width_num);i++){
			x[0]=i;
			for(int j=-1;j<=1;j++){
				x[1]=i+j;
				if(j!=0){
					y[0]=0;
					y[1]=0;
					point=calculatePoint(x, y,image, 1, jug, point);
				}else{
					y[0]=1;
					y[1]=0;
					point=calculatePoint(x, y,image, 1, jug, point);
					y[0]=0;
					y[1]=1;
					point=calculatePoint(x, y,image, -1, jug, point);
				}
			}
		}
	}

	/**
	 *
	 * @param x
	 * @param start
	 * @param end
	 * @param inc
	 * @param jug
	 * @param point
	 * @return
	 */
	private int calculatePoint(int[] x,int[] y,int[] image,int inc,boolean jug,int point){

		int[] h=new int[image.length];
		int[][] stage=getTempStage();
		int start = 0,end=0;
		boolean bool=true;
		if(inc==1){
			start=0;
			end=2;
		}else if(inc==-1){
			start=1;
			end=-1;
		}
		for(int i=start;i!=end;i+=inc){
			int j=0;
			while(j<getInt(R.integer.stage_height_num)){
				if(jugStage(x[i], j, stage))j++;
				else break;
			}
			h[i]=j-1;
			if(jugStage(x[i], h[i], stage))stage[h[i]][x[i]]=image[i];
			else bool=false;
		}
		int[] stickNum=new int[image.length];
		int tmpNum=0;
		if(bool){
			for(int i=0;i<image.length;i++){
				stickNum[i]=0;
				if(stage[h[i]][x[i]]==image[i])stickNum[i]=countPuyo(x[i], h[i], stickNum[i], stage);
				if(jug&&stickNum[i]==4)stickNum[i]=0;
			}
			for(int i=0;i<image.length;i++)tmpNum+=stickNum[i]*100+h[i];
		}
		if(point<=tmpNum){
			for(int i=0;i<image.length;i++){
				this.x[i]=x[i];
				this.y[i]=y[i];
			}
			return tmpNum;
		}else
			return point;
	}

	/**
	 *  ぷよが置けるか判定
	 * @param w
	 * @param h
	 * @param stage
	 * @return
	 */
	private boolean jugStage(int w,int h,int[][] stage){
		if(h<getInt(R.integer.stage_height_num) && h>=0 && w<getInt(R.integer.stage_width_num) && w>=0){
			if(stage[h][w]==0){
				return true;
			}else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	/**
	 * 一時的に保存するstage情報の初期化
	 * @param tmp
	 */
	private int[][] getTempStage(){
		int[][] stage=getStage(),tmp=new int[getInt(R.integer.stage_height_num)][getInt(R.integer.stage_width_num)];
		for(int i=0;i<getInt(R.integer.stage_height_num);i++)
			for(int j=0;j<getInt(R.integer.stage_width_num);j++)
				tmp[i][j]=stage[i][j];
		return tmp;
	}

	/**
	 * ぷよが落ちているときの処理
	 * @return
	 */
	protected synchronized boolean downPuyo(){
		if(getControlLock()&&movePuyo(0, 1)){
			return true;
		}else{
			setControlLock(false);
			return super.downPuyo();
		}
	}

	/**
	 *  CPU対戦時のゲームプレイ
	 * @param player	対戦相手
	 * @param view		描画判定メソッド用
	 * @return ゲームオーバーの判定
	 */
	public synchronized boolean play(Player player,PuyoView view){
		if(controlPuyo()|downJamaPuyo(player.getTime()))view.setDrawFlagTrue();
		if(player.getTime()!=0)return true;
		if(state!=getInt(R.integer.start_cpu))return true;
		//ゲームプレイ
		if(this.downPuyo()){				//移動ぷよ確認
			addJamaPuyo();
		} else if(diePuyo()){				//消せるぷよ確認
			searchDownPuyo();
			player.addJamaPuyoNum(sendJamaPuyoNum());
		} else if(jugDropJamaPuyo(player)){//じゃまぷよ確認
			createJamaPuyo();
		} else if(jugGameOver()){			//新規ぷよ作成
			setPuyo();
			getControlPuyoPoint();
		} else { 							//ゲームオーバー
			return false;
		}
		view.setDrawFlagTrue();
		return true;
	}

	/**
	 *　ぷよの移動
	 */
	public synchronized boolean controlPuyo() {
		if(!getControlLock())return false;
		boolean rtn=false;
		Puyo[] puyo =getControlPuyo();
		int[]	jug_x={puyo[0].getX()-puyo[1].getX(),x[0]-x[1]},
				jug_y={puyo[0].getY()-puyo[1].getY(),y[0]-y[1]};
		if(jug_x[0]!=jug_x[1] || jug_y[0]!=jug_y[1]){
			rtn=true;
			turnRightPuyo();
		}else if(puyo[0].getX()!=x[0]){
			if(x[0]>puyo[0].getX())
				rtn=movePuyo(1, 0);
			else if(x[0]<puyo[0].getX())
				rtn=movePuyo(-1, 0);
		}else if(difficulty!=getInt(R.integer.cpu_easy)){
			rtn=movePuyo(0, 1);
			if(!rtn)setControlLock(false);
		}
		return rtn;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		difficulty=which;
		state=getInt(R.integer.cpu_none);
	}

}