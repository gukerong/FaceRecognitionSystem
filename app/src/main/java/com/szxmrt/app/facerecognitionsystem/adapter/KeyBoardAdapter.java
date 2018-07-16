package com.szxmrt.app.facerecognitionsystem.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.szxmrt.app.facerecognitionsystem.R;

import java.util.List;
import java.util.zip.Inflater;

public class KeyBoardAdapter extends RecyclerView.Adapter<KeyBoardAdapter.ViewHolder> {
	private List<String> list;
	private ItemOnclickListener listener;
	public KeyBoardAdapter(List<String> list){
		this.list = list;
	}

	@Override
	public KeyBoardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyboard,null);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(KeyBoardAdapter.ViewHolder holder, int position) {
		holder.button.setText(list.get(position));
		holder.button.setTag(holder.getItemViewType());
		holder.button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onclick(String.valueOf(v.getTag()));
			}
		});
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	static class ViewHolder extends RecyclerView.ViewHolder{
		private Button button;
		private ViewHolder(View itemView) {
			super(itemView);
			button = itemView.findViewById(R.id.keyboard_num);
		}
	}
	public void setItemOnclickListener(ItemOnclickListener listener){
		this.listener = listener;
	}
	public interface ItemOnclickListener{
		void onclick(String s);
	}
}
