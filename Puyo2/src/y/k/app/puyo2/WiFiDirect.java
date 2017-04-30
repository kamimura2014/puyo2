package y.k.app.puyo2;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Build;




@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class WiFiDirect implements PeerListListener,ConnectionInfoListener,WifiP2pManager.ActionListener,DialogInterface.OnClickListener {

	//Wi-Fi Direct
	private GameActivityDialog			gameDialog;
	private WifiP2pManager				manager;		//Wi-Fi P2Pマネージャ
	private Channel					channel;		//チャンネル
	private BroadcastReceiver			receiver;		//ブロードキャストレシーバー
	private IntentFilter				filter;
	private WifiP2pDevice				device;
	private List<WifiP2pDevice>		devices = new ArrayList<WifiP2pDevice>();
	private boolean					enabled;		//Wi-Fi Directの有効・無効
	private ServerSocket				serverSocket;
	private String						host;
	private Game						game;
	private ExecutorService 			executor;
	private boolean[]					playOfferFlags=new boolean[2];
	private Socket						socket;
	private InputStream				in;
	private OutputStream				out;
	private boolean					groupOwner;
	private int 						actDevice;
	private GameActivity				activity;

    public WiFiDirect(GameActivityDialog gameDialog,GameActivity activity,Game game){
    	//Wi-Fi P2Pマネージャの初期化(1)
    	this.manager=(WifiP2pManager)activity.getSystemService(Context.WIFI_P2P_SERVICE);
    	this.channel=this.manager.initialize(activity, activity.getMainLooper(), null);
		filter=new IntentFilter();
		filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		this.gameDialog=gameDialog;
		this.activity=activity;
		this.game=game;
		initPlayOfferFlag();
		game.setWiFiDirect(this);
    }
    /**
     *
     * @param i
     * @return
     */
    private String getStr(int resId){
    	return activity.getString(resId);
    }

    /**
     *
     * @param resId
     * @return
     */
    private int getInt(int resId){
    	return activity.getResources().getInteger(resId);
    }


    /**
     *
     */
    public void initPlayOfferFlag(){
    	for(int i=0;i<playOfferFlags.length;i++)
    		playOfferFlags[i]=false;
    }

    /**
     *
     * @param activity
     */
    public void registerReceiver(Activity activity){
    	this.receiver=new WiFiDirectReceiver(manager, channel, this);
    	activity.registerReceiver(receiver,filter);
    }

    /**
     *
     * @param activity
     */
    public void unregisterReceiver(Activity activity){
    	activity.unregisterReceiver(receiver);
    }

    /**
     *
     * @param device
     */
    public void setDevice(WifiP2pDevice device){
    	int status=-1;
    	if(this.device!=null) status=this.device.status;
    	this.device=device;
    	if(status!=device.status){
    		if(device.status==WifiP2pDevice.AVAILABLE)searchDevice();
    		if(device.status==WifiP2pDevice.CONNECTED||device.status==WifiP2pDevice.INVITED){
    			gameDialog.dismissSearchingDeviceDialog();
    			gameDialog.dismissSelectDevceDialog();
    			if(socket==null||!socket.isBound())gameDialog.showConnectingDialog();
    		}
    	}
    	android.util.Log.d("debag",device.deviceName+":"+device.deviceAddress);

//    	if (device.status==WifiP2pDevice.AVAILABLE)	Log.d("debag", "AVAILABLE");	//利用可能
//		if (device.status==WifiP2pDevice.INVITED)		Log.d("debag", "INVITED");		//招待中
//		if (device.status==WifiP2pDevice.CONNECTED)	Log.d("debag", "CONNECTED");	//接続中
//		if (device.status==WifiP2pDevice.FAILED)		Log.d("debag", "FAILED");		//失敗
//		if (device.status==WifiP2pDevice.UNAVAILABLE)	Log.d("debag", "UNAVAILABLE");	//利用不可
    }
    /**
     *
     * @return
     */
    public boolean isGroupOwer(){
    	return groupOwner;
    }

	/**
	 * 端末検索
	 */
	private void searchDevice(){
		if(game.getState()==R.id.game_none&&enabled&&device.status==WifiP2pDevice.AVAILABLE){
			gameDialog.dismissConnectingDialog();
			actDevice=R.id.search;
			manager.discoverPeers(channel,this);
		}
	}
	/**
	 * 端末との接続
	 * @param position
	 */
	private void connectDevice(int position) {
		WifiP2pDevice device=devices.get(position);
		//接続設定の生成
		WifiP2pConfig config=new WifiP2pConfig();
		config.deviceAddress=device.deviceAddress;
		config.wps.setup=WpsInfo.PBC;
		actDevice=R.id.connect;
		manager.connect(channel,config,this);
	}

	/**
	 * 端末との切断
	 */
	public void disconnectDevice(boolean finish) {
		actDevice=R.id.disconnect;
		gameDialog.setFinish(finish);
		if(device.status==WifiP2pDevice.CONNECTED){
			manager.removeGroup(channel, this);
		} else if(device.status==WifiP2pDevice.INVITED){
			manager.cancelConnect(channel,this);
		} else if(finish) {
			activity.finish();
		} else {
			searchDevice();
		}
	}

	/**
	 *
	 * @param enabled
	 */
	public void setEnabled(boolean enabled){
		this.enabled=enabled;
		if(!enabled)
			gameDialog.showErrorWiFiDirectDialog();
	}

	/**
	 *
	 */
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		host=info.groupOwnerAddress.getHostAddress();
		if (info.groupFormed&&(groupOwner=info.isGroupOwner)){
			accept();
		} else if(info.groupFormed){
			connect();
		}
	}
	/**
	 *
	 */
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		if(!enabled)return;
		gameDialog.dismissSearchingDeviceDialog();
		gameDialog.dismissSelectDevceDialog();
		devices.clear();
		devices.addAll(peers.getDeviceList());
		if(devices.size()!=0) {
			String[] deviceNames = new String[devices.size()];
			int i=0;
			for (WifiP2pDevice device:devices){
				//端末名と端末アドレスの取得(9)
				deviceNames[i]=device.deviceName+getStr(R.string.indent)+getStr(R.string.left_parenthesis)+device.deviceAddress+getStr(R.string.right_parenthesis);
				i++;
				//Log.d("debag",deviceNames[i]);
			}
			gameDialog.showSelectDevceDialog(deviceNames);
		} else {
			searchDevice();
		}
	}


	/**
	 *
	 * @return
	 */
	private Runnable getAcceptRun(){
		return new Runnable() {
			@Override
			public void run() {
				socket=null;
				try {
					serverSocket=new ServerSocket(getInt(R.integer.port));
					socket=serverSocket.accept();
					connected();
				} catch (Exception e) {
					android.util.Log.e("debag","AcceptRun:"+e.toString());
					errorCommunicate();
				}
			}
		};
	}

	/**
	 *
	 * @return
	 */
	private Runnable getConnectRun(){
		return new Runnable() {
			@Override
			public void run() {
				socket=null;
				try {
					socket=new Socket(host,getInt(R.integer.port));
					connected();
				} catch (Exception e) {
					android.util.Log.e("debag","ConnectRun"+e.toString());
					errorCommunicate();
				}
			}
		};
	}
	/**
	 *
	 */
	public void errorCommunicate(){
		game.clear();
		stop();
		disconnectDevice(false);
		gameDialog.showErrorCommunicateDialog();
	}

	/**
	 *
	 * @return
	 */
	public void setPlayOfferFlg(int i){
		playOfferFlags[i]=true;
		if(playOfferFlags[0]&&playOfferFlags[1]){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						errorCommunicate();
					}
					send(getStr(R.string.start_game));
				}
			});
		}
	}
	/**
	 *対戦申し込みの確認のスレッド
	 * @return
	 */
	private Runnable getPlayOfferRun(){
		return new Runnable() {
			@Override
			public void run() {
				while(true){
					String str;
					try {
						str = receive();
						if(str.startsWith(getStr(R.string.device_name))){
							gameDialog.showPlayOfferDialog(str.substring(getStr(R.string.device_name).length()));
							if(groupOwner)send(game.createNextPuyo());
						}else if(str.startsWith(getStr(R.string.nextpuyo))){
							if(!groupOwner)game.setNextPuyo(str);
						} else if(str.startsWith(getStr(R.string.playoffer_posi))){
							setPlayOfferFlg(1);
						}else if(str.equals(getStr(R.string.playoffer_nega))){
							if(gameDialog.isShowingPlayOfferDialog()||gameDialog.isShowingCommunicationDialog()){
								gameDialog.dismissPlayOfferDialog();
								gameDialog.dismissCommunicationDialog();
								send(getStr(R.string.playoffer_nega));
							}
							gameDialog.showPlayOfferCancelDialog();
							initPlayOfferFlag();
							break;
						}else if(str.equals(getStr(R.string.start_game))){
							gameDialog.dismissCommunicationDialog();
							game.countDown();
							break;
						}
					} catch (Exception e) {
						errorCommunicate();
						break;
					}
				}
			}
		};
	}

	/**
	 *
	 */
	private synchronized void accept(){
		stop();
		executor=Executors.newScheduledThreadPool(3);
		executor.execute(getAcceptRun());
	}
	/**
	 *
	 */
	private synchronized void connect(){
		stop();
		executor=Executors.newScheduledThreadPool(3);
		executor.execute(getConnectRun());
	}

	/**
	 *
	 * @throws Exception
	 */
	public synchronized void connected() throws Exception {
		gameDialog.dismissConnectingDialog();
		this.in    =socket.getInputStream();
		this.out   =socket.getOutputStream();
		executor.execute(getPlayOfferRun());
		send(getStr(R.string.device_name)+device.deviceName+getStr(R.string.left_parenthesis)+device.deviceAddress+getStr(R.string.right_parenthesis));
	}
	/**
	 *
	 * @param str
	 */
	public void send(String str) {
		if(str.isEmpty())return;
		byte[] buf=str.getBytes();
		try {
			out.write(buf);
			android.util.Log.e("debag","write:"+str);
		} catch (Exception e) {
			android.util.Log.e("debag","write:"+e.toString());
			errorCommunicate();
		}
	}

	/**
	 *
	 * @return
	 */
	public String receive() throws Exception{
		byte[] buf=new byte[256];
		String str =null;
		int len;
		len = in.read(buf);
		if(len>0) str=new String(Arrays.copyOfRange(buf,0,len));
		else new Exception();
		return str;
	}

	/**
	 * キャンセル
	 */
	public void stop(){
		try {
			if(socket!=null&&!socket.isClosed())socket.close();
			if(serverSocket!=null&&!serverSocket.isClosed())serverSocket.close();
		} catch (IOException e) {
			android.util.Log.e("debag","stop:"+e.toString());
			errorCommunicate();
		}finally{
			if(executor!=null&&!executor.isShutdown())executor.shutdown();
			initPlayOfferFlag();
			game.clear();;
		}
	}

	@Override
	public void onSuccess() {
		switch (actDevice) {
		case R.id.search:
			gameDialog.showSearchingDeviceDialog();
			break;
		case R.id.connect:
			gameDialog.showConnectingDialog();
			break;
		case R.id.disconnect:
			if(gameDialog.isFinish()){
				activity.finish();
			}else{
				searchDevice();
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onFailure(int reason) {
		switch (actDevice) {
		case R.id.search:
			gameDialog.showErrorSerchDeviceDialog();
			break;
		case R.id.connect:
			gameDialog.showErrorConnectDeviceDialog();
			break;
		case R.id.disconnect:
			gameDialog.showErrorDisconnectDeviceDialog();
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		connectDevice(which);
		gameDialog.dismissSelectDevceDialog();
	}
}
