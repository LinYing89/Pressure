package test.lygzb.com.pressure.loop;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.ref.WeakReference;

import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.AdapterDurationList;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.main.Main3Activity;

public class DurationListActivity extends AppCompatActivity {

	public static final int REFRESH_DURATION_LIST = 1;
	public static MyHandler handler;
	private ListView listViewDuration;
	private AdapterDurationList adapterDurationList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_duration_list);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));
		findViews();
		setListener();
		setListViewDuration();

		handler = new MyHandler(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_duration, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.act_add) {
			startActivity(new Intent(DurationListActivity.this, DurationActivity.class));
		}

		return super.onOptionsItemSelected(item);
	}

	private void findViews(){
		listViewDuration = (ListView)findViewById(R.id.list_duration);
	}

	private void setListener(){
		listViewDuration.setOnItemClickListener(onItemClickListener);
		listViewDuration.setOnItemLongClickListener(onItemLongClickListener);
	}

	private void setListViewDuration(){
		adapterDurationList = new AdapterDurationList(this, LoopHandler.getIns().getSelectedLoop().getListDuration());
		listViewDuration.setAdapter(adapterDurationList);
	}

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			DurationActivity.duration = LoopHandler.getIns().getSelectedLoop().getListDuration().get(position);
			startActivity(new Intent(DurationListActivity.this, DurationActivity.class));
		}
	};

	private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
			new AlertDialog.Builder(DurationListActivity.this).setTitle("警告")
					.setMessage("确定删除该循环时间吗？")
					.setPositiveButton(Main3Activity.strEnsure, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							LoopHandler.getIns().getSelectedLoop().getListDuration().remove(position);
							adapterDurationList.notifyDataSetChanged();
						}
					})
					.setNegativeButton(Main3Activity.strCancel, null)
					.show();
			return true;
		}
	};

	public static class MyHandler extends Handler {
		WeakReference<DurationListActivity> mActivity;

		MyHandler(DurationListActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO handler
			final DurationListActivity theActivity = mActivity.get();
			switch (msg.arg1) {
				case REFRESH_DURATION_LIST:
					theActivity.adapterDurationList.notifyDataSetChanged();
					break;
			}

		}
	};

}
