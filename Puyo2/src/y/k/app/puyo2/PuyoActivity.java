package y.k.app.puyo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;



public class PuyoActivity extends Activity{
	private PuyoActivityDialog puyoDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        puyoDialog=new PuyoActivityDialog(this);
    }

    /**
     * 一人遊び選択ボタンクリック処理
     * @param view
     */
    public void onClickOnePlayButtton(View view){
    	puyoDialog.showDialogSelectGame();
    }
    /**
     * 二人遊び選択ボタンクリック処理
     * @param view
     */
    public void onClickTwoPlayButtton(View view){
    	//端末検索(6)
    	Intent intent = new Intent(PuyoActivity.this,GameActivity.class);
    	intent.putExtra(getString(R.string.game_type), R.id.two_play);
    	startActivity(intent);
    }
    /**
     * スコアアタック選択ボタンクリック処理
     * @param v
     */
  	public void	onClickScoreAttackButton(View v) {
		puyoDialog.dismiss();
  		Intent intent = new Intent(PuyoActivity.this,GameActivity.class);
  		intent.putExtra(getString(R.string.game_type), R.id.score_attack);
   		startActivity(intent);
	}
  	/**
  	 * cpu対戦選択ボタンクリック処理
  	 * @param v
  	 */
  	public void onClickCpuButtotn(View v) {
  		puyoDialog.dismiss();
		Intent intent = new Intent(PuyoActivity.this,GameActivity.class);
		intent.putExtra(getString(R.string.game_type), R.id.cpu_play);
		startActivity(intent);
  	}

}