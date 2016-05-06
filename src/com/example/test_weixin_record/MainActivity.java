package com.example.test_weixin_record;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.test_weixin_record.view.AudioRecorderButton;
import com.example.test_weixin_record.view.AudioRecorderButton.AudioFinishRecorderListener;


public class MainActivity extends Activity {
	
	private ListView lv_recorder;
	private ArrayAdapter<Recorder> adapter;
	private List<Recorder> recorders=new ArrayList<MainActivity.Recorder>();
	private AudioRecorderButton audioRecorderButton;
    private View animView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        audioRecorderButton=(AudioRecorderButton) findViewById(R.id.btn_record);
        lv_recorder=(ListView) findViewById(R.id.lv_record);
        audioRecorderButton.setAudioFinishRecorderListener(new AudioFinishRecorderListener() {
			
			@Override
			public void onFinish(float seconds, String filePath) {
				Recorder recorder=new Recorder(seconds, filePath);
				recorders.add(recorder);
				adapter.notifyDataSetChanged();
				lv_recorder.setSelection(recorders.size()-1);
			}
		});
        adapter=new RecorderAdapter(this, recorders);
        lv_recorder.setAdapter(adapter);
        lv_recorder.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if (animView!=null) {
					animView.setBackgroundResource(R.drawable.adj);
					animView=null;
				}
				//≤•∑≈∂Øª≠
				animView=arg1.findViewById(R.id.view_recorder_anim);
				animView.setBackgroundResource(R.drawable.play_anim);
				AnimationDrawable anim=(AnimationDrawable) animView.getBackground();
				anim.start();
				//≤•∑≈“Ù∆µ
				MediaManager.playSound(recorders.get(arg2).filePath,new MediaPlayer.OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer arg0) {
						animView.setBackgroundResource(R.drawable.adj);
					}
				});
			}
		});
    }
    
    class Recorder{
    	float time;
    	String filePath;
    	public Recorder(float time,String filePath){
    		this.time=time;
    		this.filePath=filePath;
    	}
		public float getTime() {
			return time;
		}
		public void setTime(float time) {
			this.time = time;
		}
		public String getFilePath() {
			return filePath;
		}
		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
    	
    }
    
    @Override
    protected void onPause() {
    	MediaManager.pause();
    	super.onPause();
    }
    @Override
    protected void onResume() {
    	MediaManager.resume();
    	super.onResume();
    }
    @Override
    protected void onDestroy() {
    	MediaManager.release();
    	super.onDestroy();
    }
}
