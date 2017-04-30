package y.k.app.puyo2;

import android.content.res.Resources;

public class Constant{
	private static Resources resource;

	/**
	 *
	 * @return
	 */
	public static Resources getResource() {
		return resource;
	}

	/**
	 *
	 * @param resource
	 */
	public static void setResource(Resources resource) {
		Constant.resource = resource;
	}
	/**
	 *
	 * @param id
	 * @return
	 */
	public static int getInt(int id){
		return resource.getInteger(id);
	}
	/**
	 *
	 * @param id
	 * @return
	 */
	public static String getStr(int id){
		return resource.getString(id);
	}
	/**
	 *
	 * @param id
	 * @param str
	 * @return
	 */
	public static String getStr(int id,String str){
		return resource.getString(id,str);
	}

}
