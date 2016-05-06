package com.example.test_weixin_record.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import javax.crypto.NullCipher;

import android.media.MediaRecorder;
import android.util.Log;

public class AudioManager {
	private MediaRecorder mMediaRecorder;
	private String mDir;
	private String mCurrentFilePath;
	private static AudioManager mInstance;
	private boolean isPrepared;
	
	//回调准备完毕
	public interface AudioStateListener{
		void wellPrepared();
	}
	
	public AudioStateListener mListener;
	
	public void setOnAudioStateListener(AudioStateListener listener){
		mListener=listener;
	}
	
	private AudioManager(String dir){
		mDir=dir;
	}
	
	public static AudioManager getInstance(String dir){
		if (mInstance==null) {
			synchronized (AudioManager.class) {
				if (mInstance==null) {
					mInstance=new AudioManager(dir);
				}
			}
		}
		return mInstance;
	}
	
	public void prepareAudio(){
		try {
			isPrepared=false;
			Log.i("ming", "mDir:  "+mDir);
			File dir=new File(mDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			String fileName=generateFileName();
			File file=new File(dir,fileName);
			
			mCurrentFilePath=file.getAbsolutePath();
			
			mMediaRecorder=new MediaRecorder();
			//设置输出文件
			mMediaRecorder.setOutputFile(file.getAbsolutePath());
			//设置MediaRecorder的音频源为麦克风
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			//设置音频的格式
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			//设置音频的编码为amr
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			
			//准备结束
			isPrepared=true;
			if (mListener!=null) {
				mListener.wellPrepared();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private String generateFileName() {
		
		return UUID.randomUUID().toString()+".amr";
	}

	public int getVoiceLevel(int maxLevel){
		if (isPrepared) {
			try {
				//mMediaRecorder.getMaxAmplitude()范围是1-32767   因为有7张图片所以实际要1-7
				if (mMediaRecorder!=null) {
					return maxLevel*mMediaRecorder.getMaxAmplitude()/32768+1;
				}
				return 1;
			} catch (IllegalStateException e) {
			}
		}
		return 1;
	}
	public void release(){
		mMediaRecorder.stop();
		mMediaRecorder.release();
		mMediaRecorder=null;
	}
	public void cancel(){
		release();
		if (mCurrentFilePath!=null) {
			File file=new File(mCurrentFilePath);
			file.delete();
			mCurrentFilePath=null;
		}
	}

	public String getCurrentFilePath() {
		return mCurrentFilePath;
	}
}
