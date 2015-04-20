package com.zdnuist.apkmanager.adapter;

import java.util.List;

import com.zdnuist.apkmanager.R;
import com.zdnuist.apkmanager.entity.ApkInfo;
import com.zdnuist.apkmanager.utils.AppUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter{
	
	private List<ApkInfo> itemList;
	
	private LayoutInflater layoutInflater;
	
	private Context context;
	
	public ListViewAdapter(Context context,List<ApkInfo> itemList){
		this.itemList = itemList;
		this.layoutInflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.item_layout, null);
			
			holder.apkName = (TextView) convertView.findViewById(R.id.item_apkname);
			holder.pkgName = (TextView) convertView.findViewById(R.id.item_pkgname);
			holder.icon = (ImageView) convertView.findViewById(R.id.item_icon);
			holder.intent = (TextView) convertView.findViewById(R.id.item_intent);
			holder.del = (Button)convertView.findViewById(R.id.item_del);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.apkName.setText(itemList.get(position).apkName);
		holder.pkgName.setText(itemList.get(position).packageName);
		holder.icon.setImageDrawable(itemList.get(position).icon);
		holder.intent.setText(itemList.get(position).intent==null?"":itemList.get(position).intent.toString());
		final String pkgname = itemList.get(position).packageName;
		final int location = position;
		holder.del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppUtil.uninstallApk(context, pkgname);
				itemList.remove(location);
				onDataChange(itemList);
				
			}
		});
		return convertView;
	}
	
	class ViewHolder{
		TextView apkName;
		TextView pkgName;
		ImageView icon;
		TextView intent;
		Button del;
	}
	
	public void onDataChange(List<ApkInfo> itemList) {
		this.itemList = itemList;
		this.notifyDataSetChanged();
	}

}
