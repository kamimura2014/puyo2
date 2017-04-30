package y.k.app.puyo2;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Image extends Constant{
	private static List<Integer>	images = new ArrayList<Integer>();	/**次に落ちてくるぷよのイメージ**/
	private static int[]			imageNum=null;					/**表示したイメージの数**/
	private static int			width;								/** ぷよの拡大縮小後の横**/
	private static int			height;							/** ぷよの拡大縮小後の縦**/
	private static Bitmap[]		bitmap=null;						/**表示する画像のビットマップ**/



	/**
	 *
	 * @param width
	 * @param height
	 * @param resource
	 */
	public static void initialize(int width, int height, Resources resource) {
		setResource(resource);
		Image.height = (height * 3) / (4 * (getInt(R.integer.stage_height_num) + 1));
		Image.width = width / (getInt(R.integer.stage_width_num) * 2) - 1;
		bitmap=new Bitmap[11];
		bitmap[0]=BitmapFactory.decodeResource(resource, R.drawable.jyama_puyo);
		bitmap[1]=BitmapFactory.decodeResource(resource, R.drawable.puyo1);
		bitmap[2]=BitmapFactory.decodeResource(resource, R.drawable.puyo2);
		bitmap[3]=BitmapFactory.decodeResource(resource, R.drawable.puyo3);
		bitmap[4]=BitmapFactory.decodeResource(resource, R.drawable.puyo4);
		bitmap[5]=BitmapFactory.decodeResource(resource, R.drawable.puyo5);
		bitmap[6]=BitmapFactory.decodeResource(resource, R.drawable.a_button);
		bitmap[7]=BitmapFactory.decodeResource(resource, R.drawable.b_button);
		bitmap[8]=BitmapFactory.decodeResource(resource, R.drawable.down_button);
		bitmap[9]=BitmapFactory.decodeResource(resource, R.drawable.left_button);
		bitmap[10]=BitmapFactory.decodeResource(resource, R.drawable.right_button);
	}

	/**
	 *
	 * @param image
	 * @return
	 */
	public static Bitmap getBitmap(int image){
		switch (image) {
			case R.drawable.jyama_puyo:
				return bitmap[0];
			case R.drawable.puyo1:
				return bitmap[1];
			case R.drawable.puyo2:
				return bitmap[2];
			case R.drawable.puyo3:
				return bitmap[3];
			case R.drawable.puyo4:
				return bitmap[4];
			case R.drawable.puyo5:
				return bitmap[5];
			case R.drawable.a_button:
				return bitmap[6];
			case R.drawable.b_button:
				return bitmap[7];
			case R.drawable.down_button:
				return bitmap[8];
			case R.drawable.left_button:
				return bitmap[9];
			case R.drawable.right_button:
				return bitmap[10];
			default:
				return null;
		}
	}

	/**
	 *
	 */
	public static void recycleBitmap(){
		if(bitmap==null)return;
		for(int i=0;i<bitmap.length;i++){
			if(bitmap[i]!=null){bitmap[i].recycle();}
		}
	}


	/**
	 *
	 * @return
	 */
	public static int getwidth() {
		return width;
	}

	/**
	 *
	 * @return
	 */
	public static int getheight() {
		return height;
	}

	/**
	 * 次に落ちてくるぷよの初期化
	 * @param num
	 */
	public static void setImageNum(int num) {
		imageNum = new int[num];
		for (int i = 0; i < imageNum.length; i++)imageNum[i] = 0;
	}
	/**
	 *
	 * @return
	 */
	public static int getImageNumSize(){
		return imageNum.length;
	}
	/**
	 *
	 */
	public static void createImage() {
		for(int i=0;i<imageNum.length;i++){
			while(images.size() < imageNum[i]+4){
				// 乱数を発生させ、その乱数を5で割った余り（1～5まで）でぷよの種類を決定
				switch ((int) (Math.random() * 10) % 5 + 1) {
				case 1:
					images.add(R.drawable.puyo1);
					break;
				case 2:
					images.add(R.drawable.puyo2);
					break;
				case 3:
					images.add(R.drawable.puyo3);
					break;
				case 4:
					images.add(R.drawable.puyo4);
					break;
				case 5:
					images.add(R.drawable.puyo5);
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 *
	 * @param str
	 * @return
	 */
	public static String createImage(String str){
		int image=0;
		for(int i=0;i<imageNum.length;i++){
			while(images.size() < imageNum[i]+8){
				// 乱数を発生させ、その乱数を5で割った余り（1～5まで）でぷよの種類を決定
				switch ((int) (Math.random() * 10) % 5 + 1) {
					case 1:
						image=R.drawable.puyo1;
						break;
					case 2:
						image=R.drawable.puyo2;
						break;
					case 3:
						image=R.drawable.puyo3;
						break;
					case 4:
						image=R.drawable.puyo4;
						break;
					case 5:
						image=R.drawable.puyo5;
						break;
					default:
						break;
				}
				images.add(image);
				str+=image+getStr(R.string.dot);
			}
		}
		return str;
	}
	/**
	 *
	 * @param playerType
	 * @return
	 */
	public static int getImage(int playerType) {
		if(images.size()==0)return 0;
		int re = images.get(imageNum[playerType]);
		imageNum[playerType]++;
		removeImage();
		return re;
	}

	/**
	 *
	 * @param str
	 */
	public static void setImage(String str){
		while(str.length()>1){
			int end=str.indexOf(getStr(R.string.dot));
			String tmp=str.substring(0, end);
			images.add(Integer.parseInt(tmp));
			str=str.substring(end+1, str.length());
		}
	}

	/**
	 *  いらなくなった次に表示するぷよを消す
	 */
	private static void removeImage() {
		int tmp;
		if(imageNum.length==1||imageNum[getInt(R.integer.one_player)] < imageNum[getInt(R.integer.two_player)])
			tmp=getInt(R.integer.one_player);
		else
			tmp=getInt(R.integer.two_player);
		while (imageNum[tmp] != 0) {
			images.remove(0);
			for(int i=0;i<imageNum.length;i++)imageNum[i]--;
		}
	}
	/**
	 *
	 */
	public static void clear() {
		if(imageNum!=null){
			for(int i=0;i<imageNum.length;i++)imageNum[i]=0;
		}
		images.clear();
	}
}
