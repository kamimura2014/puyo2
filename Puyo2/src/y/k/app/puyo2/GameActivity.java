package y.k.app.puyo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

public class GameActivity extends Activity{
	private PuyoView				puyoView=null;
	private Game					game;
	private WiFiDirect				wifiDirect;
	private GameActivityDialog 	gameDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		int gameType = intent.getIntExtra(getString(R.string.game_type),-1);
		gameDialog=new GameActivityDialog(this);
		game=new Game(gameDialog, gameType);
		if(gameType==R.id.two_play)wifiDirect=new WiFiDirect(gameDialog,this,game);
		setPuyoView();
	}

	@Override
	public void onStart(){
		super.onStart();
		Log.d("debag", "start");
	}

	@Override
	public void onStop(){
		super.onStop();
		Log.d("debag", "stop");
	}
	@Override
	public void onRestart(){
		super.onRestart();
		Log.d("debag", "restart");
	}
	@Override
	public void onResume(){
		super.onResume();
		if(wifiDirect!=null)wifiDirect.registerReceiver(this);
		Log.d("debag", "Resume");
	}
	@Override
    public void onPause() {
        super.onPause();
        if(wifiDirect!=null)wifiDirect.unregisterReceiver(this);
        Log.d("debag", "Pause");
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d("debag", "Destroy");
		Image.recycleBitmap();
	}

	/**
	 * cpuを渡す
	 * @return
	 */
	public Cpu getCpu(){
		return game.getCpu();
	}

	/**
	 * wifiDirectをわたす
	 * @return
	 */
	public WiFiDirect getWiFiDirect(){
		return wifiDirect;
	}
	/**
	 * ボタン制御
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event){
		if(event.getAction()==KeyEvent.ACTION_DOWN&&event.getKeyCode()==KeyEvent.KEYCODE_BACK){
			game.onClickBackKey();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	/**
	 *ゲーム画面に遷移する
	 */
	private void setPuyoView(){
		puyoView = new PuyoView(this,game);
		setContentView(puyoView);
	}

	/**
	 * クリック時に新規ゲームが始まるクリックリスナー
	 * @return
	 */
	public void onClickReStart(){
		game.clear();
		if(wifiDirect!=null){
			wifiDirect.stop();
			wifiDirect.disconnectDevice(false);
		}
		game.initialization();
	}

	/**
	 * クリック時にゲームがスタートされるクリックリスナー
	 * @return
	 */
	public void onClickGameStart(){
		game.start();
	}

	/**
	 *対戦申し込みを受けた場合の処理
	 * @return
	 */
	public void onClickPlayOfferPosi(){
		wifiDirect.send(getString(R.string.playoffer_posi));
		setPuyoView();
		wifiDirect.setPlayOfferFlg(0);
		gameDialog.dismissPlayOfferDialog();
		gameDialog.showCommunicationDialog();
	}

	/**
	 *対戦申し込みを拒否した場合の処理
	 * @return
	 */
	public void onClickPlayOfferNega(){
		wifiDirect.send(getString(R.string.playoffer_nega));
	}

	/**
	 * アクティビティ終了
	 */
	public void finishGame(){
		game.clear();
		if(wifiDirect!=null){
			wifiDirect.stop();
			wifiDirect.disconnectDevice(true);
		} else {
			finish();
		}
	}

	/**
	 * cpuプレイ開始
	 */
	public void onClickStartCPUPlay(){
		//game.setCPUHard();
		game.countDown();
	}

}