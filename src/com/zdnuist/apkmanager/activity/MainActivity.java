package com.zdnuist.apkmanager.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.zdnuist.apkmanager.R;
import com.zdnuist.apkmanager.Fragment.LoadingDialogFragment;
import com.zdnuist.apkmanager.adapter.ListViewAdapter;
import com.zdnuist.apkmanager.entity.ApkInfo;
import com.zdnuist.apkmanager.utils.AppUtil;
import com.zdnuist.apkmanager.view.RefreshListView;
import com.zdnuist.apkmanager.view.RefreshListView.IRefreshListener;

public class MainActivity extends Activity implements IRefreshListener {

	private RefreshListView listView;

	private ListViewAdapter adapter;

	List<ApkInfo> datas;

	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		listView = (RefreshListView) findViewById(R.id.refresh_listview);
		context = this;
		listView.setListener(this);
		
		new QueryDataAsnycTask().execute();
	}

	@Override
	public void onRefresh() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				datas = AppUtil.queryAllAppInfo(context,2);
				adapter.onDataChange(datas);
				listView.refreshComplete();
			}
		}, 2000);
		
	}
	
	LoadingDialogFragment loading;

	class QueryDataAsnycTask extends AsyncTask<Void,Integer,Boolean>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loading = new LoadingDialogFragment();
			loading.show(((Activity)context).getFragmentManager(), "loadingF");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			datas = AppUtil.queryAllAppInfo(context,2);
			if(datas.size() > 0){
				return true;
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if(result){
				Toast.makeText(context, datas.size()+"", Toast.LENGTH_LONG).show();
				adapter = new ListViewAdapter(context, datas);
				listView.setAdapter(adapter);
			}else{
				Toast.makeText(context, "刷新失败！", Toast.LENGTH_LONG).show();
			}
			
			loading.dismiss();
			
		}
		
	}
	
}
