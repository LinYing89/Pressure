/**
 * 
 */
package test.lygzb.com.pressure.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import test.lygzb.com.pressure.main.UserHelper;
import test.lygzb.com.pressure.main.WelcomeActivity;

public class SharedHelper {

	public final String USERNAME = "username";
	public final String PET_NAME = "petName";
	public final String PASSWORD = "password";
	public static final String DOWNLOAD_ID = "downloadId";

    /**
     * configuration file name of numbers
     */
	public static final String SHARED_NUMBER = "number";
    /**
     * configuration file name of activity eyes
     */
	public final String SHARED_ACTIVITY_EYES = "activity_eyes";

    /**
     * configuration of usual fort size
     */
	public final String MAIN_FORT_SIZE = "mainFortSize";
    /**
     * configuration of climate fort size
     */
	public final String CLIMATE_FORT_SIZE = "climateFortSize";

	private SharedPreferences shared;

	public SharedHelper() {
	}

	public SharedPreferences getSharedFile(String sharedName) {
		shared = WelcomeActivity.context.getSharedPreferences(sharedName,
                Context.MODE_PRIVATE);
		return shared;
	}

	public void setUser(){
		SharedPreferences shared = getSharedFile(SHARED_NUMBER);
		Editor editor = shared.edit();
		editor.putString(USERNAME, UserHelper.getUser().getName());
		editor.putString(PET_NAME, UserHelper.getUser().getPetName());
		editor.putString(PASSWORD, UserHelper.getUser().getPsd());
		editor.apply();
	}

	public void getUser(){
		SharedPreferences shared = getSharedFile(SHARED_NUMBER);
		UserHelper.getUser().setName(shared.getString(USERNAME, ""));
		UserHelper.getUser().setPetName(shared.getString(PET_NAME, ""));
		UserHelper.getUser().setPsd(shared.getString(PASSWORD, ""));
	}

	public void creatNumberShared(Context context) {
		SharedPreferences shared = context
				.getSharedPreferences(SHARED_NUMBER, Context.MODE_PRIVATE);
		Editor editor = shared.edit();

		editor.putInt(MAIN_FORT_SIZE, 15);
        editor.putInt(CLIMATE_FORT_SIZE, 20);

		editor.apply();
	}
}
