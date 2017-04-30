package y.k.app.puyo2;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;


@SuppressLint("InflateParams")
public class GameActivityDialog extends CustomDialog implements Runnable,DialogInterface.OnClickListener{
	private GameActivity	activity;
	private int 			state;
	private Handler 		handler;
	private int			count;
	private String			deviceName;
	private boolean		finish;
	private String[]		deviceNames;

	public GameActivityDialog(GameActivity activity) {
		super(activity);
		state=R.id.dialog_none;
		this.activity=activity;
		this.handler = new Handler();
	}
	/**
	 *
	 * @param resId
	 * @return
	 */
	private String getStr(int resId){
		return activity.getString(resId);
	}
	/**
	 *
	 * @param id
	 * @return
	 */
	private int getInt(int id){
		return activity.getResources().getInteger(id);
	}
	/**
	 *
	 * @return
	 */
	public boolean isFinish() {
		return finish;
	}

	/**
	 *
	 * @param finish
	 */
	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	/**
	 *
	 * @param resId
	 * @return
	 */
	private String getStr(int resId,String str){
		return activity.getString(resId,str);
	}

	/**
	 *
	 * @param state
	 */
	private void create(int state){
		if(this.state!=R.id.dialog_none)return;
		this.state=state;
		handler.post(this);
	}
	/**
	 * 通信端末選択ダイアログ表示
	 */
	public void showSelectDevceDialog(String[] deviceNames){
		//String[] abc={"aaa","bbb"};
		this.deviceNames=deviceNames;
		Log.d("debag","dialog"+this.deviceNames[0]);
		create(R.id.dialog_device_list);
	}

	/**
	 * 通信端末選択ダイアログ破棄
	 */
	public void dismissSelectDevceDialog(){
		if(state==R.id.dialog_device_list){
			dismiss();
			state=R.id.dialog_none;
		}
	}
	/**
	 * カウントダウンダイアログ表示
	 */
	public void showCountDownDialog(int count){
		this.count=count;
		create(R.id.dialog_countdown);
	}

	/**
	 *カウントダウンダイアログ破棄
	 */
	public void dismissCountDownDialog(){
		if(state==R.id.dialog_countdown){
			dismiss();
			state=R.id.dialog_none;
		}
	}

	/**
	 * ゲームオーバダイアログ表示
	 */
	public void showGameOverDialog(){
		create(R.id.dialog_game_over);
	}

	/**
	 * 勝利宣言ダイアログ
	 */
	public void showWinnerDialog(){
		create(R.id.dialog_winner);
	}

	/**
	 * 引き分け宣言ダイアログ
	 */
	public void showDrawDialog(){
		create(R.id.dialog_draw);
	}

	/**
	 * 敗北宣言ダイアログ
	 */
	public void showLoserDialog(){
		create(R.id.dialog_loser);
	}

	/**
	 * 一時停止用ダイアログ表示
	 */
	public void showSuspendDialog(){
		create(R.id.dialog_game_stop);
	}


	/**
	 *エラーwifi設定
	 */
	public void showErrorWiFiDirectDialog(){
		dismiss();
		state=R.id.dialog_err_wifi_direct;
		create(false, null, getStr(R.string.err), getStr(R.string.err_wifi),0,null,null, R.string.ok, -1, this);
		show();
	}

	/**
	 *端末検索失敗表示用ダイアログ
	 */
	public void showErrorSerchDeviceDialog(){
		dismiss();
		state=R.id.dialog_err_serch_device;
		create(false, null, getStr(R.string.search_device), getStr(R.string.err_search_device),0,null,null, R.string.ok, -1, this);
		show();
	}
	/**
	 *端末接続失敗表示用ダイアログ
	 */
	public void showErrorConnectDeviceDialog(){
		dismiss();
		state=R.id.dialog_err_connect_device;
		create(false, null, getStr(R.string.err), getStr(R.string.err_conect_device),0,null,null, R.string.ok, -1, this);
		show();
	}

	/**
	 *端末切断失敗表示用ダイアログ
	 */
	public void showErrorDisconnectDeviceDialog(){
		dismiss();
		state=R.id.dialog_err_disconnect_device;
		create(false, null, getStr(R.string.err), getStr(R.string.err_disconnect_device),0,null,null, R.string.disconnect, -1, this);
		show();
	}
	/**
	 * 通信エラー表示用ダイアログ
	 */
	public void showErrorCommunicateDialog(){
		dismiss();
		state=R.id.dialog_err_communicate;
		handler.post(this);
	}

	/**
	 *端末検索中表示用ダイアログ
	 */
	public void showSearchingDeviceDialog(){
		create(R.id.dialog_searching_device);
	}

	/**
	 * 端末検索中表示用ダイアログ破棄
	 */
	public void dismissSearchingDeviceDialog(){
		if(state==R.id.dialog_searching_device){
			state=R.id.dialog_none;
			dismiss();
		}
	}

	/**
	 *接続中表示用ダイアログ
	 */
	public void showConnectingDialog(){
		create(R.id.dialog_connecting_device);
	}

	/**
	 * 接続中表示用ダイアログ破棄
	 */
	public void dismissConnectingDialog(){
		if(state==R.id.dialog_connecting_device){
			state=R.id.dialog_none;
			dismiss();
		}
	}

	/**
	 *通信対戦確認用ダイアログ
	 */
	public void showPlayOfferDialog(String device_name){
		this.deviceName=device_name;
		create(R.id.dialog_play_offer);
	}

	/**
	 * 通信対戦確認用ダイアログ破棄
	 */
	public void dismissPlayOfferDialog(){
		if(state==R.id.dialog_play_offer){
			state=R.id.dialog_none;
			dismiss();
		}
	}

	/**
	 * 通信対戦確認用ダイアログを表示しているか判定
	 */
	public boolean isShowingPlayOfferDialog(){
		if(state==R.id.dialog_play_offer&&super.isShowing())
			return true;
		else
			return false;
	}

	/**
	 *通信中表示用ダイアログ
	 */
	public void showCommunicationDialog(){
		create(R.id.dialog_communication);
	}

	/**
	 * 通信中表示用ダイアログ破棄
	 */
	public void dismissCommunicationDialog(){
		if(state==R.id.dialog_communication){
			state=R.id.dialog_none;
			dismiss();
		}
	}

	/**
	 * 通信中表示用ダイアログ表示中判定
	 * @return
	 */
	public boolean isShowingCommunicationDialog(){
		if(state==R.id.dialog_communication&&super.isShowing())
			return true;
		else
			return false;
	}

	/**
	 *通信対戦キャンセル時表示用ダイアログ
	 */
	public void showPlayOfferCancelDialog(){
		create(R.id.dialog_play_offer_cancel);
	}

	/**
	 * CPU対戦のCPUの難易度確認ダイアログ
	 */
	public void showCPUDifficultyDialog(){
		create(R.id.dialog_cpu_difficulty);
	}

	@Override
	public void run() {
		switch (state) {
		//カウントダウンダイアログ
		case R.id.dialog_countdown:
			TextView view = new TextView(activity);
			view.setTextSize(40.0f);
			view.setGravity(Gravity.CENTER_HORIZONTAL);
			if(count!=0){
				view.setText(String.valueOf(count));
			}else{
				view.setText(R.string.start);
			}
			create(false, view, null, null,0,null,null, -1, -1,null);
			show();
			break;
		//端末検索中表示用ダイアログ
		case R.id.dialog_searching_device:
			create(false, null, getStr(R.string.search_device), getStr(R.string.searching_device),0,null,null, -1, R.string.cancel,this);
			break;
		//接続中表示用ダイアログ
		case R.id.dialog_connecting_device:
			create(false, null, getStr(R.string.connect), getStr(R.string.connecting),0,null,null, -1,  -1, null);
			break;
		//通信中表示用ダイアログ
		case R.id.dialog_communication:
			create(false, null, getStr(R.string.communicate), getStr(R.string.communication),0,null,null, -1, -1, null);
			break;
		//通信対戦確認用ダイアログ
		case R.id.dialog_play_offer:
			create(false, null,getStr(R.string.play_offer), getStr(R.string.playing_offer,deviceName),0,null,null, R.string.yes, R.string.no,this);
			break;
		//通信端末選択ダイアログ
		case R.id.dialog_device_list:
			Log.d("debag","dialog"+deviceNames[0]);
			create(false, null, getStr(R.string.select_device), null, 0, deviceNames, activity.getWiFiDirect(), -1, R.string.cancel,this);
			break;
		//ゲームオーバダイアログ表示
		case R.id.dialog_game_over:
			create(false, null, getStr(R.string.game_over), getStr(R.string.continue_game),0,null,null, R.string.yes, R.string.no,this);
			break;
		//勝利宣言ダイアログ
		case R.id.dialog_winner:
			create(false, null, getStr(R.string.you_win), getStr(R.string.continue_game), 0, null, null, R.string.yes, R.string.no, this);
			break;
		//引き分け宣言ダイアログ
		case R.id.dialog_draw:
			create(false, null, getStr(R.string.draw), getStr(R.string.continue_game), 0, null, null, R.string.yes, R.string.no, this);
			break;
		//敗北宣言ダイアログ
		case R.id.dialog_loser:
			create(false, null, getStr(R.string.you_lose), getStr(R.string.continue_game), 0, null, null, R.string.yes, R.string.no, this);
			break;
		//一時停止用ダイアログ
		case R.id.dialog_game_stop:
			create(false, null, getStr(R.string.stop_game), getStr(R.string.restart_game),0,null,null, R.string.yes, R.string.no,this);
			break;
		//通信対戦キャンセル時表示用ダイアログ
		case R.id.dialog_play_offer_cancel:
			create(false, null, getStr(R.string.play_offer), getStr(R.string.play_offer_cancel),0,null,null, R.string.ok, -1, this);
			break;
		//CPU対戦のCPUの難易度確認ダイアログ
		case R.id.dialog_cpu_difficulty:
			String[] items={getStr(R.string.easy),getStr(R.string.normal),getStr(R.string.hard)};
			create(false, null, getStr(R.string.cpu_difficulty), null, getInt(R.integer.cpu_normal),items,activity.getCpu(), R.string.ok,R.string.cancel,this);
			break;
		//通信エラー表示用ダイアログ
		case R.id.dialog_err_communicate:
			create(false, null, getStr(R.string.err), getStr(R.string.err_communicate), 0, null, null, R.string.ok, -1, this);
			break;
		default:
			break;
		}
		show();
	}
	/**
	 *
	 */
	private void onClickPositive(int state){
		switch (state) {
		//カウントダウンダイアログ
//		case R.id.dialog_countdown:
//			break;
		//端末検索中表示用ダイアログ
//		case R.id.dialog_searching_device:
//			break;
		//接続中表示用ダイアログ
//		case R.id.dialog_connecting_device:
//			break;
		//通信中表示用ダイアログ
//		case R.id.dialog_communication:
//			break;
		//通信対戦確認用ダイアログ
		case R.id.dialog_play_offer:
			activity.onClickPlayOfferPosi();
			break;
		//ゲームオーバダイアログ表示
		case R.id.dialog_game_over:
			activity.onClickReStart();
			break;
		//勝利宣言ダイアログ
		case R.id.dialog_winner:
			activity.onClickReStart();
			break;
		//引き分け宣言ダイアログ
		case R.id.dialog_draw:
			activity.onClickReStart();
			break;
		//敗北宣言ダイアログ
		case R.id.dialog_loser:
			activity.onClickReStart();
			break;
		//一時停止用ダイアログ
		case R.id.dialog_game_stop:
			activity.onClickGameStart();
			break;
		//通信対戦キャンセル時表示用ダイアログ
		case R.id.dialog_play_offer_cancel:
			activity.onClickReStart();
			break;
		//CPU対戦のCPUの難易度確認ダイアログ
		case R.id.dialog_cpu_difficulty:
			activity.onClickStartCPUPlay();
			break;
		//エラーwifi設定
		case R.id.dialog_err_wifi_direct:
			activity.finishGame();
			break;
		//端末検索失敗表示用ダイアログ
		case R.id.dialog_err_serch_device:
			activity.finishGame();
			break;
		//端末接続失敗表示用ダイアログ
//		case DIALOG_STATE_ERR_CONNECT:
//			break;
		//通信切断時の表示用ダイアログ
		case R.id.dialog_err_disconnect_device:
			if(isFinish()){
				activity.finishGame();
			}else{
				activity.onClickReStart();
			}
			break;
		//通信エラー表示用ダイアログ
		case R.id.dialog_err_communicate:
			activity.finishGame();
			break;
		default:
			break;
		}
	}
	/**
	 *
	 */
	private void onClickNegative(int state){
		switch (state) {
		//カウントダウンダイアログ
//		case R.id.dialog_countdown:
//			break;
		//端末検索中表示用ダイアログ
		case R.id.dialog_searching_device:
			activity.finishGame();
			break;
		//接続中表示用ダイアログ
//		case R.id.dialog_connecting_device:
//			break;
		//通信中表示用ダイアログ
//		case R.id.dialog_communication:
//			break;
		//通信対戦確認用ダイアログ
		case R.id.dialog_play_offer:
			activity.onClickPlayOfferNega();
			break;
		//通信端末選択ダイアログ
		case R.id.dialog_device_list:
			activity.finishGame();
			break;
		//ゲームオーバダイアログ表示
		case R.id.dialog_game_over:
			activity.finishGame();
			break;
		//勝利宣言ダイアログ
		case R.id.dialog_winner:
			activity.finishGame();
			break;
		//引き分け宣言ダイアログ
		case R.id.dialog_draw:
			activity.finishGame();
			break;
		//敗北宣言ダイアログ
		case R.id.dialog_loser:
			activity.finishGame();
			break;
		//一時停止用ダイアログ
		case R.id.dialog_game_stop:
			activity.finishGame();
			break;
		//通信対戦キャンセル時表示用ダイアログ
//		case R.id.dialog_play_offer_cancel:
//			break;
		//CPU対戦のCPUの難易度確認ダイアログ
		case R.id.dialog_cpu_difficulty:
			activity.finishGame();
			break;
		//エラーwifi設定
//		case R.id.dialog_err_wifi_direct:
//			break;
		//端末検索失敗表示用ダイアログ
//		case R.id.dialog_err_serch_device:
//			break;
		//端末接続失敗表示用ダイアログ
//		case DIALOG_STATE_ERR_CONNECT:
//			break;
		//通信切断時の表示用ダイアログ
//		case DIALOG_STATE_ERR_DISCONNECTING:
//			break;
		//通信エラー表示用ダイアログ
		case R.id.dialog_err_communicate:
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		int tmp=state;
		state=R.id.dialog_none;
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			onClickPositive(tmp);
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			onClickNegative(tmp);
			break;
		default:
			break;
		}
	}



}
