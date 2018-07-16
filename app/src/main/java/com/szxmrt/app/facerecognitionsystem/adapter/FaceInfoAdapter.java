package com.szxmrt.app.facerecognitionsystem.adapter;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.szxmrt.app.facerecognitionsystem.R;
import com.szxmrt.app.facerecognitionsystem.entity.User;
import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018-06-23
 */

public class FaceInfoAdapter extends RecyclerView.Adapter<FaceInfoAdapter.FaceInfoViewHolder> {
	private List<User> users = new ArrayList<>();
	private static String TAG = FaceInfoAdapter.class.getSimpleName();
	private ItemOnclickListener listener;
	public FaceInfoAdapter(){

	}
	public FaceInfoAdapter(List<User> users){
		this.users = users;
	}
	public void updateItems(List<User> users){
		if(users!=null && users.size()>0){
			int itemCount = users.size();
			int positionStart = getItemCount();
			this.users.addAll(users);
			/*for(int i=itemCount-1;i<users.size()-1;i++){
				notifyItemInserted(i);
			}*/
			if(positionStart>0) notifyItemRangeInserted(positionStart-1,itemCount);
			else notifyItemRangeInserted(positionStart,itemCount);
		}
	}
	public void deleteItem(int position){
		users.remove(position);
		notifyItemRemoved(position);
	}
	@Override
	public FaceInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LogUtil.e(TAG,viewType+"");
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyboard,parent,false);
		return new FaceInfoViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final FaceInfoViewHolder holder, int position) {
		final Integer p = position;
		holder.uid.setText(users.get(position).getUid());
		holder.info.setText(users.get(position).getUser_info());
		holder.delete.setText(R.string.delete);
		holder.uid.setTag(position);
		if (listener!=null){
			holder.delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.itemOnclick(holder.uid);
					LogUtil.e(TAG,"position : "+p+"");
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		LogUtil.e(TAG,"itemCount : "+users.size()+"");
		return users.size();
	}
	static class FaceInfoViewHolder extends RecyclerView.ViewHolder{
		//@BindView(R.id.recycler_uid)
		TextView uid;
		//@BindView(R.id.recycler_info)
		TextView info;
		//@BindView(R.id.register_back)
		TextView delete;
		private FaceInfoViewHolder(View itemView) {
			super(itemView);
			//Unbinder unbinder = ButterKnife.bind(itemView);
			uid = itemView.findViewById(R.id.recycler_uid);
			info = itemView.findViewById(R.id.recycler_info);
			delete = itemView.findViewById(R.id.recycler_remove);
		}
	}

	public void setItemOnclickListener(ItemOnclickListener listener){
		this.listener = listener;
	}
	public interface ItemOnclickListener{
		void itemOnclick(TextView textView);
	}
}
