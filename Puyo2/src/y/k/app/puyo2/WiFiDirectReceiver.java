package y.k.app.puyo2;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Build;

//Wi-Fi Directのブロードキャストレシーバー
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class WiFiDirectReceiver extends BroadcastReceiver {
	private WifiP2pManager	manager;
	private Channel		channel;
	private WiFiDirect		wifiDirect;
	private int			state;

	//コンストラクタ
	public WiFiDirectReceiver(WifiP2pManager manager,Channel channel,WiFiDirect wifiDirect) {
		this.manager =manager;
		this.channel =channel;
		this.wifiDirect=wifiDirect;
		this.state=-1;
	}

	//受信時に呼ばれる
	@Override
	public void onReceive(Context context,Intent intent) {
		String action=intent.getAction();
		//端末本体のWi-Fi Directの有効・無効の変更通知(3)
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			state=intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
			if(state==WifiP2pManager.WIFI_P2P_STATE_DISABLED){
				wifiDirect.setEnabled(false);
			}
			if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
				wifiDirect.setEnabled(true);
			}
		}
		//Wi-Fi Direct端末一覧の変更通知(4)
		else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			if (manager==null) return;
			manager.requestPeers(channel,wifiDirect);
		}
		//Wi-Fi Direct通信状態の変更通知(5)
		else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			if (manager==null) return;
			NetworkInfo networkInfo=(NetworkInfo)intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			//接続
			if (networkInfo.isConnected()) {
				manager.requestConnectionInfo(channel,wifiDirect);
			} else {
				wifiDirect.stop();
			}
		}
		//端末本体の状態の変更通知(6)
		else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
			wifiDirect.setDevice((WifiP2pDevice)intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }
}

