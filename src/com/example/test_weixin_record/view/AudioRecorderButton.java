package com.example.test_weixin_record.view;

import com.example.test_weixin_record.view.AudioManager.AudioStateListener;

import android.R;
import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class AudioRecorderButton extends Button implements AudioStateListener {

	private static final int MSG_AUDIO_PREPARED = 0X110;
	private static final int MSG_VOICE_CHANGED = 0X111;
	private static final int MSG_DIALOG_DIMISS = 0X112;

	private static final int STATE_NORMAL = 1;// ����״̬
	private static final int STATE_RECORDING = 2;// ¼��״̬
	private static final int STATE_WANT_TO_CANCLE = 3;// ȡ��״̬
	private static final int DISTANCE_Y_CANCLE = 50;// y�����ľ��볬��50��ʱ�����Ϊ��ȡ��
	private int mCurState = STATE_NORMAL;// ��ǰ״̬
	private boolean isRecording;// �Ƿ���¼��
	private DialogManager mDialogManager;
	private AudioManager mAudioManager;
	private float mTime;//¼��ʱ��
	private boolean mReady;//���ʱ����̻�û����onLongClick�¼�
	

	public AudioRecorderButton(Context context) {
		this(context, null);
	}
	//¼��������Ļص�
	public interface AudioFinishRecorderListener{
		void onFinish(float seconds,String filePath);
	}
	private AudioFinishRecorderListener mListener;
	
	public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
		mListener=listener;
	}
	
	public AudioRecorderButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDialogManager = new DialogManager(getContext());

		String dir = Environment.getExternalStorageDirectory()
				+ "/francis_recorder_audios";
		mAudioManager = AudioManager.getInstance(dir);
		mAudioManager.setOnAudioStateListener(this);

		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				mReady=true;
				mAudioManager.prepareAudio();
				return false;
			}
		});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_AUDIO_PREPARED:
				mDialogManager.showRecordingDialog();
				isRecording = true;
				
				new Thread(mGetVoiceLevelRunnable).start();
				break;
			case MSG_VOICE_CHANGED:
				mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
				break;
			case MSG_DIALOG_DIMISS:
				mDialogManager.dimissDialog();
				break;
			

			}
		};
	};

	@Override
	public void wellPrepared() {
		mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
	}
	//��ȡ������С��Runable
	private Runnable mGetVoiceLevelRunnable=new Runnable() {
		
		@Override
		public void run() {
			while (isRecording) {
				try {
					Thread.sleep(100);
					mTime+=0.1f;
					mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			changeState(STATE_RECORDING);
			break;
		case MotionEvent.ACTION_MOVE:

			if (isRecording) {
				if (wantToCancle(x, y)) {
					changeState(STATE_WANT_TO_CANCLE);
				} else {
					changeState(STATE_RECORDING);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (!mReady) {
				reset();
				return super.onTouchEvent(event);
			}
			if (!isRecording||mTime<0.6f) {
				mDialogManager.tooShort();
				mAudioManager.cancel();
				mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
			}else if (mCurState == STATE_RECORDING) {
				mDialogManager.dimissDialog();
				mAudioManager.release();
				if (mListener!=null) {
					mListener.onFinish(mTime,mAudioManager.getCurrentFilePath());
					
				}
			} else if (mCurState == STATE_WANT_TO_CANCLE) {
				mDialogManager.dimissDialog();
				mAudioManager.cancel();
			}
			reset();
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	// �ָ���ʼ����
	private void reset() {
		isRecording = false;
		mReady=false;
		
		mTime=0;
		changeState(STATE_NORMAL);
	}

	// �ж�����ֵ�Ƿ�ȡ������
	private boolean wantToCancle(int x, int y) {
		// λ�ó���button�ؼ��Ŀ��
		if (x < 0 || x > getWidth()) {
			return true;
		}
		// λ�ó���button�ؼ��ĸ߶����Ƶ�ʱ��
		if (y < -DISTANCE_Y_CANCLE || y > getHeight() + DISTANCE_Y_CANCLE) {
			return true;
		}
		return false;
	}

	// �ı�״̬
	private void changeState(int state) {
		if (mCurState != state) {
			mCurState = state;
			switch (state) {
			case STATE_NORMAL:
				setBackgroundResource(com.example.test_weixin_record.R.drawable.btn_recorder_normal);
				setText(com.example.test_weixin_record.R.string.str_recorder_normal);

				break;
			case STATE_RECORDING:
				setBackgroundResource(com.example.test_weixin_record.R.drawable.btn_recorder_recording);
				setText(com.example.test_weixin_record.R.string.str_recorder_recording);
				if (isRecording) {
					mDialogManager.recording();
				}
				break;
			case STATE_WANT_TO_CANCLE:
				setBackgroundResource(com.example.test_weixin_record.R.drawable.btn_recorder_recording);
				setText(com.example.test_weixin_record.R.string.str_recorder_want_cancle);
				mDialogManager.wantToCancle();
				break;
			}
		}
	}

}
