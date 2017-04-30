package y.k.app.puyo2;


import static y.k.app.puyo2.Constant.*;


public class Player extends Stage{

	private Score	score;			//スコア表示用
	private int	controlState;	//
	private int	touchState;		//
	private int	time;
	private String	sendStr="";

	/**
	 *
	 * @param w
	 * @param h
	 */
	public Player(int w,int h){
		super(w, h);
		score = new Score(getRect());
		setTopStage(score);
	}
	/**
	 *
	 * @param w
	 * @param h
	 * @param playerType
	 */
	public Player(int w,int h,int playerType){
		super(w, h, playerType);
		endTouch();
		time=9;
	}
	/**
	 *
	 */
	public synchronized void init(){
		endTouch();
		time=9;
	}

	/**
	 *
	 *
	 */
	public synchronized void calculatePlayTime(){
		time++;
		time%=getInt(R.integer.max_time_number);
	}

	/**
	 *
	 * @return
	 */
	public synchronized int getTime(){
		return time;
	}

	/**
	 *
	 * @return
	 */
	public synchronized int getControlState() {
		return controlState;
	}

	/**
	 * スコアアタック開始
	 * @param view		描画判定メソッド用
	 * @return ゲームオーバーの判定
	 */
	public synchronized boolean play(PuyoView view){
		calculatePlayTime();
		if(controlPuyo())view.setDrawFlagTrue();
		if(time!=0)return true;
		//ゲームプレイ
		if(downPuyo(false)){					//移動ぷよ確認
		} else if(diePuyo()){					//消せるぷよ確認
			searchDownPuyo();
			score.calculate();
		} else if(jugGameOver()){				//新規ぷよ作成
			setPuyo();
		} else { 								//ゲームオーバー
			return false;
		}
		view.setDrawFlagTrue();
		return true;
	}

	/**
	 * cpu対戦のゲームプレイ
	 * @param cpu 対戦相手
	 * @param view		描画判定メソッド用
	 * @return ゲームオーバーの判定
	 */
	public synchronized boolean play(Cpu cpu,PuyoView view){
		calculatePlayTime();
		if(cpu.jugHandicap())setJamaPuyoNum(getInt(R.integer.drop_jama_puyo_max_num));
		if(controlPuyo()|downJamaPuyo(time))view.setDrawFlagTrue();
		if(time!=0)return true;
		//ゲームプレイ
		if(downPuyo(false)){				//移動ぷよ確認
			addJamaPuyo();
		} else if(diePuyo()){				//消せるぷよ確認
			searchDownPuyo();
			cpu.addJamaPuyoNum(sendJamaPuyoNum());
		} else if(jugDropJamaPuyo(cpu)){	//じゃまぷよ確認
			createJamaPuyo();
		} else if(jugGameOver()){			//新規ぷよ作成
			setPuyo();
			cpu.startCpu();
		} else { 							//ゲームオーバー
			return false;
		}
		view.setDrawFlagTrue();
		return true;
	}

	/**
	 * 1プレイヤーの通信対戦のゲームプレイ
	 * @param two_p 対戦相手
	 * @param view		描画判定メソッド用
	 * @return ゲームオーバーの判定
	 */
	public synchronized boolean play(Player two_p,PuyoView view){
		calculatePlayTime();
		if(controlPuyo()|downJamaPuyo(time))view.setDrawFlagTrue();
		if(two_p.downJamaPuyo(time))view.setDrawFlagTrue();
		if(time!=0){
			sendStr=getControlPuyoString();
			return true;
		}
		//ゲームプレイ
		if(downPuyo(true)){				//移動ぷよ確認
			addJamaPuyo();
		} else if(diePuyo()){				//消せるぷよ確認
			searchDownPuyo();
			two_p.addJamaPuyoNum(sendJamaPuyoNum());
		} else if(super.jugDropJamaPuyo(two_p)){//じゃまぷよ確認
			sendStr+=getStr(R.string.drop_jama)+getDropJamaPuyoFlag()+getStr(R.string.colon);
			createJamaPuyo();
		} else if(jugGameOver()){			//新規ぷよ作成
			sendStr+=getStr(R.string.drop_jama)+getDropJamaPuyoFlag()+getStr(R.string.colon);
			setPuyo();
		} else { 							//ゲームオーバー
			return false;
		}
		view.setDrawFlagTrue();
		return true;
	}
	/**
	 * 2プレイヤーの通信対戦のゲームプレイ
	 * @param one_p	対戦相手
	 * @param time		経過時間
	 * @param view		描画判定メソッド用
	 * @return ゲームオーバーの判定
	 */
	public synchronized boolean play(Player one_p,int time,PuyoView view){
		this.time=time;
		if(time!=0)return true;
		//ゲームプレイ
		if(downPuyo()){						//移動ぷよ確認
			addJamaPuyo();
		} else if(diePuyo()){				//消せるぷよ確認
			searchDownPuyo();
			one_p.addJamaPuyoNum(sendJamaPuyoNum());
		} else if(jugDropJamaPuyo(one_p)){//じゃまぷよ確認
			createJamaPuyo();
		} else if(jugGameOver()){			//新規ぷよ作成
			setPuyo();
		} else { 							//ゲームオーバー
			return false;
		}
		view.setDrawFlagTrue();
		return true;
	}



	/**
	 * ぷよが落ちているときの処理
	 * @param send 通信対戦かどうかの判定
	 * @return 下に落ちるぷよがあったかの判定
	 */
	public synchronized boolean downPuyo(boolean send){
		if(getControlLock()&&(touchState==getInt(R.integer.long_press)|movePuyo(0, 1))){
			if(send)sendStr=getControlPuyoString();
			return true;
		}else{
			setControlLock(false);
			endTouch();
			if(send)sendStr=getControlPuyoString();
			return super.downPuyo();
		}
	}
	/**
	 *
	 * @param one_p
	 * @return
	 */
	private boolean jugDropJamaPuyo(Player one_p){
		one_p.setJamaPuyoNum();
		return getDropJamaPuyoFlag();
	}

	/**
	 * ぷよのコントロール
	 * @param controlState
	 * @return
	 */
	public synchronized boolean controlPuyo(int controlState){
		if(!getControlLock())return false;
		boolean  rtn=super.controlPuyo(controlState);
		if(rtn){
			this.controlState=controlState;
			touchState=getInt(R.integer.start_touch);
		} else{
			endTouch();
		}
		return rtn;
	}
	/**
	 * 長押し時のぷよのコントロール
	 * @return
	 */
	public synchronized boolean controlPuyo(){
		if(!getControlLock())return false;
		if(touchState==getInt(R.integer.end_touch)){
			return false;
		}else if(touchState>getInt(R.integer.end_touch)&&touchState<getInt(R.integer.long_press)){
			touchState++;
			return false;
		}else if(touchState==getInt(R.integer.long_press)){
			boolean rtn=super.controlPuyo(controlState);
			if(!rtn)endTouch();
			return rtn;
		}else
			return false;
	}
	/**
	 *
	 */
	public synchronized void endTouch(){
		controlState=R.id.move_none;
		touchState=getInt(R.integer.end_touch);
	}
	/**
	 *
	 * @return
	 */
	public String getSendString(){
		String tmp=sendStr;
		sendStr="";
		return (tmp+getStr(R.string.time)+time+getStr(R.string.colon));
	}

	/**
	 *
	 * @return
	 */
	private String getControlPuyoString(){
		for(Puyo p:getControlPuyo()){if(p==null)return "";}
		String str=getStr(R.string.control);
		for(Puyo p:getControlPuyo()){str+=p.getImage()+getStr(R.string.dot);}
		for(Puyo p:getControlPuyo()){str+=p.getX()+getStr(R.string.dot);}
		for(Puyo p:getControlPuyo()){str+=p.getY()+getStr(R.string.dot);}
		str+=getControlLock()+getStr(R.string.dot)+getStr(R.string.colon);
		return str;
	}

	/**
	 *
	 * @param str
	 */
	public boolean setControlPuyoString(String str) throws Exception{
		for(Puyo p:getControlPuyo()){if(p==null)return false;}
		boolean rtn=false;
		int size=getControlPuyo().length;
		int[] image=new int[size],x=new int[size],y=new int[size];

		int i=0,j=0,k=0;
		while(str.length()>1){
			int len=str.indexOf(getStr(R.string.dot));
			if(i!=size){
				image[i]=Integer.parseInt(str.substring(0, len));
				i++;
			}else if(j!=size){
				x[j]=Integer.parseInt(str.substring(0, len));
				j++;
			}else if(k!=size){
				y[k]=Integer.parseInt(str.substring(0, len));
				k++;
			}else{
				setControlLock(Boolean.parseBoolean(str.substring(0, len)));
			}
			str=str.substring(len+1, str.length());
		}
		i=0;
		for(Puyo p:getControlPuyo()){
			if(p.getImage()!=image[i]) throw new Exception("Image");
			int w=x[i]-p.getX();
			int h=y[i]-p.getY();
			if(w!=0||h!=0){
				p.update(w, h);
				rtn=true;
			}
			i++;
		}
		return rtn;
	}

}
