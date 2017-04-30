package y.k.app.puyo2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.LayoutInflater;


@SuppressLint("InflateParams")
public class PuyoActivityDialog extends CustomDialog implements DialogInterface.OnClickListener{
	private PuyoActivity activity;
	public PuyoActivityDialog(PuyoActivity activity) {
		super(activity);
		this.activity=activity;
	}
	 /**
     * 一人遊びゲーム内容選択ダイアログ
     */
    public void showDialogSelectGame(){
    	create(true, LayoutInflater.from(activity).inflate(R.layout.select_game_dialog, null), activity.getString(R.string.select_game), null, 0, null, null, -1, R.string.cancel, null);
		show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			break;
		case DialogInterface.BUTTON_NEGATIVE:

			break;
		case DialogInterface.BUTTON_NEUTRAL:
			break;
		default:
			break;
		}

	}

}
