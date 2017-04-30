package y.k.app.puyo2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

public class CustomDialog {
	private Dialog dialog;
	private Context context;
	int i=0;
	protected CustomDialog(Context context){
		this.context=context;
	}
	/**
	 * ダイアログが表示されているか判定
	 * @return
	 */
	public boolean isShowing(){
		if(dialog!=null&&dialog.isShowing())
			return true;
		else
			return false;
	}
	/**
	 * ダイアログ表示
	 */
	protected void show(){
		if(dialog!=null&&!dialog.isShowing())dialog.show();
	}

	/**
	 * ダイアログ閉じる
	 */
	protected void dismiss(){
		if(dialog!=null&&dialog.isShowing())
			dialog.dismiss();
	}

	/**
	 *ダイアログ作成
	 * @param cancelable
	 * @param view
	 * @param title
	 * @param message
	 * @param itemNum
	 * @param items
	 * @param selectItem
	 * @param posi
	 * @param nega
	 * @param clickPosi
	 * @param clickNega
	 */
	protected void create(boolean cancelable,View view,String title,String message,int itemNum,String[] items,DialogInterface.OnClickListener selectItem,int posi,int nega,DialogInterface.OnClickListener click){
		if(dialog!=null&&dialog.isShowing())return;
		i++;
		AlertDialog.Builder dialogbuilder= new AlertDialog.Builder(context);
		dialogbuilder.setCancelable(cancelable);
		if(title!=null){
			dialogbuilder.setTitle(title);
		}
		if(message!=null){
			dialogbuilder.setMessage(message);
		}
		if(view!=null){
			dialogbuilder.setView(view);
		}
		if(itemNum>0){
			dialogbuilder.setSingleChoiceItems(items, itemNum, selectItem);
		}
		if(itemNum==0&&items!=null){
			for(String str:items)Log.d("debag","dialog2:"+str);
			dialogbuilder.setItems(items, selectItem);
		}
		if(posi!=-1){
			dialogbuilder.setPositiveButton(posi, click);
		}
		if(nega!=-1){
			dialogbuilder.setNegativeButton(nega, click);
		}
		Log.d("debag","dialog3:"+i);
		dialog=dialogbuilder.create();
	}

}
