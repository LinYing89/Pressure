package test.lygzb.com.pressure.esptouch;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.esptouch.task.EsptouchTask;
import test.lygzb.com.pressure.esptouch.task.IEsptouchResult;
import test.lygzb.com.pressure.esptouch.task.IEsptouchTask;
import test.lygzb.com.pressure.esptouch.task.__IEsptouchTask;

public class EsptouchActivity extends AppCompatActivity {

	private TextView text_ssid;
	private EditText edit_psd;
	private Button btnConfig;
	private boolean configOk;

	private EspWifiAdminSimple mWifiAdmin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_esptouch);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		mWifiAdmin = new EspWifiAdminSimple(this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));

		findViews();
		setListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// display the connected ap's ssid
		String apSsid = mWifiAdmin.getWifiConnectedSsid();
		if (apSsid != null) {
			text_ssid.setText(apSsid);
		} else {
			text_ssid.setText("");
		}
		// check whether the wifi is connected
		boolean isApSsidEmpty = TextUtils.isEmpty(apSsid);
		btnConfig.setEnabled(!isApSsidEmpty);
	}

	private void findViews(){
		text_ssid = (TextView)findViewById(R.id.tvApSssidConnected);
		edit_psd = (EditText)findViewById(R.id.edit_psd);
		btnConfig = (Button)findViewById(R.id.btn_config);
	}

	private void setListener(){
		btnConfig.setOnClickListener(onClickListener);
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == btnConfig) {
				String apSsid = text_ssid.getText().toString();
				String apPassword = edit_psd.getText().toString();
				String apBssid = mWifiAdmin.getWifiConnectedBssid();
				Boolean isSsidHidden = false;
				String isSsidHiddenStr = "NO";
				String taskResultCountStr = Integer.toString(1);
				if (isSsidHidden)
				{
					isSsidHiddenStr = "YES";
				}
				if (__IEsptouchTask.DEBUG) {
					Log.d("EsptouchActivity", "mBtnConfirm is clicked, mEdtApSsid = " + apSsid
							+ ", " + " mEdtApPassword = " + apPassword);
				}
				new EsptouchAsyncTask3().execute(apSsid, apBssid, apPassword,
						isSsidHiddenStr, taskResultCountStr);
			}
		}
	};

	private class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {

		private ProgressDialog mProgressDialog;

		private IEsptouchTask mEsptouchTask;
		// without the lock, if the user tap confirm and cancel quickly enough,
		// the bug will arise. the reason is follows:
		// 0. task is starting created, but not finished
		// 1. the task is cancel for the task hasn't been created, it do nothing
		// 2. task is created
		// 3. Oops, the task should be cancelled, but it is running
		private final Object mLock = new Object();

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(EsptouchActivity.this);
			mProgressDialog
					.setMessage("正在配置，请稍等...");
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					synchronized (mLock) {
						if (__IEsptouchTask.DEBUG) {
							Log.i("EsptouchActivity", "progress dialog is canceled");
						}
						if (mEsptouchTask != null) {
							mEsptouchTask.interrupt();
						}
					}
				}
			});
			mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
					"稍等...", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(configOk) {
								finish();
							}
							mProgressDialog.dismiss();
						}
					});
			mProgressDialog.show();
			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
					.setEnabled(false);
		}

		@Override
		protected List<IEsptouchResult> doInBackground(String... params) {
			int taskResultCount = -1;
			synchronized (mLock) {
				String apSsid = params[0];
				String apBssid = params[1];
				String apPassword = params[2];
				String isSsidHiddenStr = params[3];
				String taskResultCountStr = params[4];
				boolean isSsidHidden = false;
				if (isSsidHiddenStr.equals("YES")) {
					isSsidHidden = true;
				}
				taskResultCount = Integer.parseInt(taskResultCountStr);
				mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword,
						isSsidHidden, EsptouchActivity.this);
			}
			List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
			return resultList;
		}

		@Override
		protected void onPostExecute(List<IEsptouchResult> result) {
			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
					.setEnabled(true);
			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
					"确定");
			IEsptouchResult firstResult = result.get(0);
			// check whether the task is cancelled and no results received
			if (!firstResult.isCancelled()) {
				int count = 0;
				// max results to be displayed, if it is more than maxDisplayCount,
				// just show the count of redundant ones
				final int maxDisplayCount = 5;
				// the task received some results including cancelled while
				// executing before receiving enough results
				if (firstResult.isSuc()) {
					StringBuilder sb = new StringBuilder();
					for (IEsptouchResult resultInList : result) {
						String ip = resultInList.getInetAddress().getHostAddress();
						if(!TextUtils.isEmpty(ip)){
							mProgressDialog.setMessage("配置成功");
							configOk = true;
						}
					}
				} else {
					mProgressDialog.setMessage("配置失败");
					configOk = false;
				}
			}
		}
	}



}
