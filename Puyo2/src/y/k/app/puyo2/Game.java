package y.k.app.puyo2;

import static y.k.app.puyo2.Constant.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.os.Build;
import android.view.MotionEvent;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Game implements Runnable {

	private int 						width,height;
	private PuyoView					puyoView;
	private Stage[]					stages;
	private Player						one_p = null,two_p=null;
	private Cpu 						cpu;
	private int 						gameType;
	private ScheduledExecutorService	countdown_executor;
	private ScheduledExecutorService 	executor;	//ぷよの落下処理用
	private int 						count;		//ゲーム開始カウント用
	private GameActivityDialog			gameDialog;
	private int						state;
	private Controller					controller;
	private WiFiDirect					wifiDirect;
	private boolean[] 				plays;

	/**
	 *
	 * @param game_dialog
	 * @param resource
	 * @param game_type
	 */
	public Game(GameActivityDialog game_dialog,int game_type){
		this.gameDialog=game_dialog;
        this.gameType=game_type;
        controller=new Controller();
        state=R.id.game_none;
	}

	/**
	 *
	 * @param activity
	 */
	public void onClickBackKey(){
		switch (gameType) {
		case R.id.score_attack:
			if(state==R.id.game_start)stop();
			else;
			break;
		case R.id.cpu_play:
			if(state==R.id.game_start)stop();
			else;
			break;
		case R.id.two_play:
			if(state==R.id.game_none){
				wifiDirect.stop();
				wifiDirect.disconnectDevice(true);
			}else;
			break;
		default:
			break;
		}
	}

	/**
	 *
	 * @param wifidirect
	 */
	public void setWiFiDirect(WiFiDirect wifiDirect){
		this.wifiDirect=wifiDirect;
	}
	/**
	 *
	 * @return
	 */
	public int getState(){
		return state;
	}

	/**
	 * @param width
	 * @param height
	 */
	public void setWindowsSize(int width,int height){
		this.width=width;
		this.height=height;
	}

	/**
	 *ゲーム初期化設定
	 * @param puyoview
	 */
	public void initialization(PuyoView puyoview){
		this.puyoView=puyoview;
		switch (state) {
			case R.id.game_none:
				//ぷよの大きさ設定
				Image.initialize(width, height, puyoview.getResources());
				//十字キー設定
				controller.initialization(width, height);
				switch (gameType) {
				case R.id.score_attack:
					Image.setImageNum(1);
					stages=new Stage[Image.getImageNumSize()];
					stages[getInt(R.integer.one_player)] = one_p = new Player(width, height);
					break;
				case R.id.cpu_play:
					Image.setImageNum(2);
					stages=new Stage[Image.getImageNumSize()];
					stages[getInt(R.integer.one_player)] = one_p = new Player(width, height,R.integer.one_player);
					stages[getInt(R.integer.two_player)] = cpu = new Cpu(width,height);
					break;
				case R.id.two_play:
					Image.setImageNum(2);
					stages=new Stage[Image.getImageNumSize()];
					stages[getInt(R.integer.one_player)] = one_p = new Player(width, height,R.integer.one_player);
					stages[getInt(R.integer.two_player)] = two_p = new Player(width, height,R.integer.two_player);
					break;
				default:
					break;
				}
				initialization();
				break;
			case R.id.game_start:
				if(gameType==R.id.two_play)
					puyoview.draw();
				else{
					puyoview.draw();
					stop();
				}
				break;
			case R.id.count_down:
				if(gameType==R.id.two_play)
					puyoview.draw();
				else
					countDown();
				break;
			default:
				break;
		}
	}

	/**
	 * cpuを渡す
	 * @return
	 */
	public Cpu getCpu(){
		return cpu;
	}

	/**
	 *ゲーム開始
	 */
	public void start(){
		state=R.id.game_start;
		switch (gameType) {
			case R.id.score_attack:
				executor=Executors.newScheduledThreadPool(1);
				break;
			case R.id.cpu_play:
				executor=Executors.newScheduledThreadPool(1);
				break;
			case R.id.two_play:
				executor=Executors.newScheduledThreadPool(2);
				executor.execute(receiveGame());
				break;
			default:
				break;
		}
		executor.scheduleAtFixedRate(this, 0, getInt(R.integer.basic_time), TimeUnit.MILLISECONDS);
	}

	/**
	 * ゲーム終了
	 */
	public void clear(){
		state=R.id.game_none;
		Image.clear();
		if(stages!=null){for(Stage s:stages){if(s!=null){s.clear();}}}
		if(countdown_executor!=null&&!countdown_executor.isShutdown())countdown_executor.shutdown();
		if(executor!=null&&!executor.isShutdown())executor.shutdown();
	}


	/**
	 * ゲームプレイ
	 * @return
	 */
	private synchronized void play(){
		switch (gameType) {
			case R.id.score_attack:
				Image.createImage();
				plays[getInt(R.integer.one_player)]=one_p.play(puyoView);
				break;
			case R.id.cpu_play:
				Image.createImage();
				plays[getInt(R.integer.one_player)]=one_p.play(cpu,puyoView);
				plays[getInt(R.integer.two_player)]=cpu.play(one_p,puyoView);
				break;
			case R.id.two_play:
				plays[getInt(R.integer.one_player)]=one_p.play(two_p,puyoView);
				sendGame();
				jugTime();
				break;
			default:
				break;
		}
	}

	/**
	 *
	 */
	private synchronized void jugTime(){
		if(one_p.getTime()!=two_p.getTime()){
			try {
				wait();
			} catch (InterruptedException e) {
				wifiDirect.errorCommunicate();
			}
		} else {
			notifyAll();
		}
	}

	/**
	 *ゲーム初期化設定
	 */
	public void initialization(){
		one_p.init();
		if(cpu!=null)cpu.init();
		if(two_p!=null)two_p.init();
		plays=new boolean[stages.length];
		puyoView.setDrawFlagTrue();
		puyoView.draw();
		switch (gameType) {
			case R.id.score_attack:
				setNextPuyo();
				countDown();
				break;
			case R.id.cpu_play:
				setNextPuyo();
				gameDialog.showCPUDifficultyDialog();
				break;
			case R.id.two_play:
				break;
			default:
				break;
		}
	}

	/**
	 *ゲーム一時停止
	 */
	public void stop(){
		state=R.id.game_stop;
		if(executor!=null&&!executor.isShutdown())executor.shutdown();
		gameDialog.showSuspendDialog();
	}

	/**
	 *surfaceDestroyedが呼ばれたとき
	 */
	public void viewDestroyed(){
		if(gameType!=R.id.two_play){
			if(state==R.id.game_start){
				executor.shutdown();
			}else if(state==R.id.count_down){
				gameDialog.dismissCountDownDialog();
				if(countdown_executor!=null&&!countdown_executor.isShutdown())countdown_executor.shutdown();
			}
		}
	}

	/**
	 * 描画
	 */
	public void draw(Canvas canvas){
		for(int i=0;i<stages.length;i++)stages[i].draw(canvas);
		controller.draw(canvas);
	}


	/**
	 * カウントダウン
	 */
	public void countDown(){
		state=R.id.count_down;
		count=5;
		countdown_executor= Executors.newSingleThreadScheduledExecutor();
		countdown_executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				if(count!=5)gameDialog.dismissCountDownDialog();
				if(count==-1){
					countdown_executor.shutdown();
					start();
					return;
				}
				gameDialog.showCountDownDialog(count);
				count--;
			}
		},0,1,TimeUnit.SECONDS);
	}

	/**
	 * ゲームオーバー処理
	 */
	private void gameOver(boolean[] plays){
		boolean jug=true;
		for(boolean play:plays)if(!play)jug=false;
		if(jug)return;
		state=R.id.game_exit;
		executor.shutdown();
		if(gameType==R.id.two_play)wifiDirect.send(getStr(R.string.game_end)+getStr(R.string.colon));
		if(plays.length==1)
			gameDialog.showGameOverDialog();
		else if(plays[getInt(R.integer.one_player)]&&!plays[getInt(R.integer.two_player)])
			gameDialog.showWinnerDialog();
		else if(!plays[getInt(R.integer.one_player)]&&!plays[getInt(R.integer.two_player)])
			gameDialog.showDrawDialog();
		else if(!plays[getInt(R.integer.one_player)]&&plays[getInt(R.integer.two_player)])
			gameDialog.showLoserDialog();
	}

	/**
	 * タッチイベント
	 * @param x
	 * @param y
	 * @param event
	 */
	public boolean onTouchEventAction(float x,float y,int event){
		if(!one_p.getControlLock())return false;
		switch (event) {
			case MotionEvent.ACTION_DOWN:
				if(one_p.controlPuyo(controller.jugControl(x, y))){
					return true;
				}else{
					return false;
				}
			case MotionEvent.ACTION_UP:
				one_p.endTouch();
				return false;
			case MotionEvent.ACTION_MOVE:
				int controlState=controller.jugControl(x, y);
				if(one_p.getControlState()!=controlState){
					boolean rtn=one_p.controlPuyo(controlState);
					return rtn;
				}else{
					return false;
				}
			default:
				return false;
		}
	}
	/**
	 *
	 * @return
	 */
	private Runnable receiveGame(){
		return new Runnable() {
			@Override
			public void run() {
				while(true){
					String recStr;
					try {
						recStr = wifiDirect.receive();
						List<String> strs=new ArrayList<String>();
						while(recStr.length()>1){
							int len=recStr.indexOf(getStr(R.string.colon));
							strs.add(recStr.substring(0, len));
							recStr=recStr.substring(len+1, recStr.length());
						}
						if(playTwoPlayer(strs))break;
					} catch (Exception e) {
						wifiDirect.errorCommunicate();
						break;
					}
				}
			}
		};
	}

	/**
	 * @throws Exception
	 *
	 */
	private synchronized boolean playTwoPlayer(List<String> strs) throws Exception{
		boolean rtn =false;
		for(String str:strs){
			if(str.startsWith(getStr(R.string.time))){
				plays[getInt(R.integer.two_player)]=two_p.play(one_p,Integer.parseInt(str.substring(getStr(R.string.time).length())),puyoView);
			} else if(str.startsWith(getStr(R.string.nextpuyo))){
				Image.setImage(str.substring(getStr(R.string.nextpuyo).length()));
			}else if(str.startsWith(getStr(R.string.control))){
				if(two_p.setControlPuyoString(str.substring(getStr(R.string.control).length())))puyoView.setDrawFlagTrue();
			}else if(str.startsWith(getStr(R.string.drop_jama))){
				two_p.setDropJamaPuyoFlag(Boolean.parseBoolean(str.substring(getStr(R.string.drop_jama).length())));
			}else if(str.equals(getStr(R.string.game_end)))rtn=true;
		}
		jugTime();
		return rtn;
	}

	/**
	 *
	 */
	private void sendGame(){
		String sendStr="",tmp;
		if(wifiDirect.isGroupOwer()){
			tmp=Image.createImage(getStr(R.string.nextpuyo));
			if(!getStr(R.string.nextpuyo).equals(tmp))sendStr+=tmp+getStr(R.string.colon);
		}
		sendStr+=one_p.getSendString();
		if(!sendStr.equals(""))wifiDirect.send(sendStr);
	}
	/**
	 *
	 * @return
	 */
	public String createNextPuyo(){
		String str=Image.createImage(getStr(R.string.nextpuyo));
		for(Stage s:stages)s.setNextPuyo();
		return str;
	}

	/**
	 *
	 * @param str
	 */
	private void setNextPuyo(){
		Image.createImage();;
		for(Stage s:stages)s.setNextPuyo();
	}

	/**
	 *
	 * @param str
	 */
	public void setNextPuyo(String str){
		Image.setImage(str.substring(getStr(R.string.nextpuyo).length()));
		for(Stage s:stages)s.setNextPuyo();
	}

	@Override
	public void run() {
		play();
		puyoView.draw();
		gameOver(plays);
	}
}
