package test.lygzb.com.pressure.chain;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.ChainSectionsPagerAdapter;
import test.lygzb.com.pressure.application.BackListener;

public class ChainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chain2);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));

		ChainSectionsPagerAdapter mSectionsPagerAdapter = new ChainSectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(mViewPager);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//DeviceChainHelper.getIns().getListDeviceChain().clear();
	}
}
