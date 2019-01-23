package com.sungbospot.lunch;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.Log;
import android.widget.*;
import java.util.*;

import static com.sungbospot.lunch.Tools.isNetwork;

public class ActivityMeal extends Activity
{
    BapListAdapter mAdapter;
    BapDownloadTask mProcessTask;
    Calendar mCalendar;
    ListView mListView;
    ProgressDialog mDialog;

    boolean isUpdating = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meal);
        if (!isNetwork(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "네트워크에 연결되지 않았습니다. 이전에 블러왔던 급식정보를 표시합니다.", Toast.LENGTH_SHORT).show();
        }
        mCalendar = Calendar.getInstance();
        mListView = findViewById(R.id.mListView);
        mAdapter = new BapListAdapter(getApplicationContext());
        mListView.setAdapter(mAdapter);
        getBapList(true);
	}

    private void getBapList(boolean isUpdate)
	{
        mAdapter.clearData();
        mAdapter.notifyDataSetChanged();
        if (mCalendar == null)
            mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.DATE, 2 - mCalendar.get(Calendar.DAY_OF_WEEK));
		for (int i = 0; i < 5; i++) {
            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH);
            int day = mCalendar.get(Calendar.DAY_OF_MONTH);
            BapTool.restoreBapDateClass mData =
				BapTool.restoreBapData(getApplicationContext(), year, month, day);
            if (mData.isBlankDay) {
                if (isNetwork(getApplicationContext())) {
                    if (!isUpdating && isUpdate) {
                        mDialog = new ProgressDialog(this);
                        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mDialog.setMax(100);
                        mDialog.setTitle(R.string.loading_title);
                        mDialog.setCancelable(false);
                        mDialog.show();
                        mProcessTask = new BapDownloadTask(getApplicationContext());
                        mProcessTask.execute(year, month, day);
                    }
                }
                return;
            }
            mAdapter.addItem(mData.Calender, mData.DayOfTheWeek, mData.Morning, mData.Lunch, mData.Dinner);
            mCalendar.add(Calendar.DATE, 1);
        }
        mAdapter.notifyDataSetChanged();
        setCurrentItem();
    }
    private void setCurrentItem() {
        int DAY_OF_WEEK = mCalendar.get(Calendar.DAY_OF_WEEK);
        if (DAY_OF_WEEK > 1 && DAY_OF_WEEK < 7) {
            mListView.setSelection(DAY_OF_WEEK - 2);
        } else {
            mListView.setSelection(0);
        }
    }
    public class BapDownloadTask extends ProcessTask {
        public BapDownloadTask(Context mContext) {
            super(mContext);
        }

        @Override
        public void onPreDownload() {
            isUpdating = true;
        }

        @Override
        public void onUpdate(int progress) {
            mDialog.setProgress(progress);
        }

        @Override
        public void onFinish(long result) {
            if (mDialog != null)
                mDialog.dismiss();
            isUpdating = false;
            if (result == -1) {
                return;
            }
            getBapList(false);
        }
    }

	@Override
	public void onPause()
	{
		super.onPause();
		if (mDialog != null)
            mDialog.dismiss();
        mCalendar = null;
	}
	
}
