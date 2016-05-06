package com.example.test_weixin_record;

import java.util.List;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.test_weixin_record.MainActivity.Recorder;

public class RecorderAdapter extends ArrayAdapter<Recorder> {
	
	private List<Recorder> mDatas;
	private Context mContext;
	private LayoutInflater inflater;
	
	//消息框随时间的长度而变长
	private int mMinItemWidth;
	private int mMaxItemWidth;
	
	public RecorderAdapter(Context context, List<Recorder> resource) {
		super(context,-1, resource);
		mContext=context;
		mDatas=resource;
		inflater=LayoutInflater.from(context);
		
		//获得当前item的最小和最大宽度
		WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics=new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		
		mMinItemWidth=(int) (outMetrics.widthPixels*0.15f);
		mMaxItemWidth=(int) (outMetrics.widthPixels*0.7f);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if (convertView==null) {
			convertView=inflater.inflate(R.layout.item_recorder, parent, false);
			viewHolder=new ViewHolder();
			viewHolder.tv_time=(TextView)convertView.findViewById(R.id.tv_recorder_time);
			viewHolder.view_length=convertView.findViewById(R.id.fl_recorder);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		viewHolder.tv_time.setText(Math.round(getItem(position).time)+"\"");
		ViewGroup.LayoutParams lp=viewHolder.view_length.getLayoutParams();
		lp.width=(int) (mMinItemWidth+(mMaxItemWidth/60f*getItem(position).time));
		
		return convertView;
	}
	private class ViewHolder{
		TextView tv_time;
		View view_length;
		
	}

}
