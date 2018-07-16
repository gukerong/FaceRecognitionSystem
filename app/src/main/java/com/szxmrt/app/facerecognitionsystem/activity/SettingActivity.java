package com.szxmrt.app.facerecognitionsystem.activity;

import android.app.PendingIntent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szxmrt.app.facerecognitionsystem.R;
import com.szxmrt.app.facerecognitionsystem.adapter.FaceInfoAdapter;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivity;
import com.szxmrt.app.facerecognitionsystem.entity.PLCOrder;
import com.szxmrt.app.facerecognitionsystem.entity.User;
import com.szxmrt.app.facerecognitionsystem.model.SettingActivityModel;
import com.szxmrt.app.facerecognitionsystem.presenter.SettingActivityPresenter;
import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;

import butterknife.BindView;

public class SettingActivity extends BaseActivity<SettingActivityPresenter,SettingActivityModel> {
	@BindView(R.id.setting_recyclerView)
	RecyclerView recyclerView;
	@BindView(R.id.setting_back)
	ImageView back;
	@BindView(R.id.setting_token)
	TextView updateToken;
	@BindView(R.id.closeDoor)
	Button closeDoor;
	@BindView(R.id.openDoor)
	Button openDoor;
	private SettingActivityModel model;
	private List<User> users = new ArrayList<>();
	private FaceInfoAdapter faceInfoAdapter;
	private static String TAG = SettingActivity.class.getSimpleName();
	@Override
	public int getLayoutId() {
		return R.layout.activity_setting;
	}

	@Override
	public SettingActivityPresenter initPresenter() {
		return new SettingActivityPresenter(this);
	}

	@Override
	public void initVariables() {
		model = getModel();
		faceInfoAdapter = new FaceInfoAdapter(users);
		GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
		recyclerView.setAdapter(faceInfoAdapter);
		recyclerView.setLayoutManager(gridLayoutManager);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPresenter().closeSerialPort();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GetUserAsyncTask getUserAsyncTask = new GetUserAsyncTask(this);
		getUserAsyncTask.execute();
		getPresenter().openUsbSerialPort(this);
	}

	@Override
	public void initListener() {
		faceInfoAdapter.setItemOnclickListener(new FaceInfoAdapter.ItemOnclickListener() {
			@Override
			public void itemOnclick(final TextView textView) {
				DeleteGroupUserAsyncTask deleteGroupUserAsyncTask = new DeleteGroupUserAsyncTask(SettingActivity.this);
				deleteGroupUserAsyncTask.execute(textView);
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		updateToken.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						getPresenter().updateToken();
					}
				}).start();
			}
		});
		closeDoor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						getPresenter().controlDoor(PLCOrder.WY05);
					}
				}).start();
			}
		});
		openDoor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						getPresenter().controlDoor(PLCOrder.Wy05);
					}
				}).start();
			}
		});
	}

	@Override
	public SettingActivityModel initModel() {
		return new SettingActivityModel(this);
	}
	static class DeleteGroupUserAsyncTask extends AsyncTask<TextView,Integer,Boolean>{
		private WeakReference<SettingActivity> weakReference;
		private DeleteGroupUserAsyncTask(SettingActivity activity){
			weakReference = new WeakReference<>(activity);
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(TextView... objects) {
			SettingActivity activity = weakReference.get();
			TextView textView = objects[0];
			Integer position = (Integer) textView.getTag();
			String uid = String.valueOf(textView.getText());
			boolean b = activity.getPresenter().deleteGroupUser("test",uid);
			if (b){
				publishProgress(position);
				return true;
			}else {
				return false;
			}
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			SettingActivity activity = weakReference.get();
			activity.faceInfoAdapter.deleteItem(values[0]);
		}
		@Override
		protected void onPostExecute(Boolean integer) {
			super.onPostExecute(integer);
		}
	}
	static class GetUserAsyncTask extends AsyncTask<Integer , Object , List<User>>{
		private WeakReference<SettingActivity> weakReference;
		private GetUserAsyncTask(SettingActivity activity){
			weakReference = new WeakReference<>(activity);
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected List<User> doInBackground(Integer... integers) {
			SettingActivity activity = weakReference.get();
			List<User> users = null;
			try {
				users = activity.model.getGroupUser();
			} catch (IOException e) {
				e.printStackTrace();
				LogUtil.e(activity.getPackageName(),"error");
			}
			return users;
		}
		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);
		}
		@Override
		protected void onPostExecute(List<User> users) {
			super.onPostExecute(users);
			SettingActivity activity = weakReference.get();
			if(users!=null){
				User user = new User("aaaa","aaaa",null);
				activity.faceInfoAdapter.updateItems(users);
			}
		}

	}

























}
