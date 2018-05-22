package test.lygzb.com.pressure.application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import test.lygzb.com.pressure.R;

/**
 * Created by Administrator on 2016/4/10.
 */
public class Constant {
	/**
	 * screen width
	 */
	public static int displayWidth;
	/**
	 * screen height
	 */
	public static int displayHeight;

	/**
	 * title height
	 */
	public static int titleHeight;

	/**
	 * usual fort size
	 */
	public static int mainFortSize = 15;
	/**
	 * climate fort size
	 */
	public static int climateFortSize = 30;

	/**
	 * get switch width
	 * @return
	 */
	public static int getEleHolderWidth(){
		return (int) (Constant.displayWidth * 0.16f + 0.5f);
	}

	/**
	 * get switch height
	 * @return
	 */
	public static int getEleHolderHeight(){
		return getEleWidth() - 10;
	}

	public static int getEleItemHeight(){
		return (int) (Constant.displayHeight * 0.08f + 0.5f);
	}
	/**
	 * get electrical width
	 * @return
	 */
	public static int getEleWidth(){
		return (int) (Constant.displayWidth * 0.10f + 0.5f);
	}

	/**
	 * get electrical height
	 * @return
	 */
	public static int getEleHeight(){
		return getEleWidth();
	}

	public static int getCustomButtonWidth(){
		return (int) (Constant.displayWidth * 0.12f + 0.5f);
	}

	public static int getCustomButtonHeight(){
		return (int) (Constant.displayWidth * 0.08f + 0.5f);
	}

	/**
	 *
	 * @return
	 */
	public static int getClickItemWidth(){
		return (int) (Constant.displayWidth * 0.09f + 0.5f);
	}

	public static int getFontSize() {
		int rate = (int)( mainFortSize * (float) displayWidth / 1024);
//        Log.e("Constant font", String.valueOf(rate));
		return rate< mainFortSize ? mainFortSize : rate;
	}

	public static int getClimateFontSize() {
		int rate = (int)( climateFortSize * (float) displayWidth / 1024);
//        Log.e("Constant font", String.valueOf(rate));
		return rate< climateFortSize ? climateFortSize : rate;
	}

	public static int getNetFontSize() {
		int rate = (int)( mainFortSize * (float) displayWidth / 1024);
		return rate< mainFortSize ? mainFortSize : rate;
	}

	public static String getString(Context context, int id){
		return context.getString(id);
	}
	public static int getColor(Context context, int id){
		return context.getResources().getColor(id);
	}

	/**
	 * 显示消息对话框
	 * @param msg
	 */
	public static void showErrDialog(Context context, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg);
		builder.setPositiveButton(getString(context, R.string.ensure), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		builder.create().show();
	}
}
