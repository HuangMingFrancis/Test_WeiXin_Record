package com.example.test_weixin_record.view;

import com.example.test_weixin_record.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogManager {
	private Dialog mDialog;
	
	private ImageView ivIcon, ivVoice;
	private TextView tvLable;
	private Context mContext;

	public DialogManager(Context mContext) {
		this.mContext = mContext;
	}
	//显示dialog
	public void showRecordingDialog() {
		mDialog = new Dialog(mContext,R.style.Theme_audioDialog);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_recorder, null);
		mDialog.setContentView(view);
		
		
		ivIcon = (ImageView) mDialog.findViewById(R.id.iv_recorder_dialog_icon);
		ivVoice = (ImageView) mDialog.findViewById(R.id.iv_recorder_dialog_voice);
		tvLable = (TextView) mDialog.findViewById(R.id.tv_recorder_dialog);
		mDialog.show();
	}
	//录音时
	public void recording() {
		if (mDialog != null && mDialog.isShowing()) {
			ivIcon.setVisibility(View.VISIBLE);
			ivVoice.setVisibility(View.VISIBLE);
			tvLable.setVisibility(View.VISIBLE);

			ivIcon.setImageResource(R.drawable.recorder);
			tvLable.setText("手指上滑,取消发送");
		}
	}
	//取消录音时
	public void wantToCancle() {
		if (mDialog != null && mDialog.isShowing()) {
			ivIcon.setVisibility(View.VISIBLE);
			ivVoice.setVisibility(View.GONE);
			tvLable.setVisibility(View.VISIBLE);

			ivIcon.setImageResource(R.drawable.cancel);
			tvLable.setText("松开手指,取消发送");
		}
	}
	//录音时间太短时
	public void tooShort() {
		if (mDialog != null && mDialog.isShowing()) {
			ivIcon.setVisibility(View.VISIBLE);
			ivVoice.setVisibility(View.GONE);
			tvLable.setVisibility(View.VISIBLE);

			ivIcon.setImageResource(R.drawable.voice_to_short);
			tvLable.setText("录音时间过短");
		}
	}
	//让dialog消失
	public void dimissDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}
	//根据声音大小显示图片
	public void updateVoiceLevel(int level) {
		if (mDialog != null && mDialog.isShowing()) {
//			ivIcon.setVisibility(View.VISIBLE);
//			ivVoice.setVisibility(View.VISIBLE);
//			tvLable.setVisibility(View.VISIBLE);

			int resid = mContext.getResources().getIdentifier("v" + level,
					"drawable", mContext.getPackageName());
			ivVoice.setImageResource(resid);
		}
	}

}
