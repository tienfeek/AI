package com.tien.ai;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tien.ai.demain.Ai;

public class AiListFragment extends Fragment {
	
	private ListView mListView;
	private ArrayList<Ai> mAiList = new ArrayList<Ai>();
	private AiAdapter mAiAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ai_list_view, null);
		
		this.findView(view);
		return view;
	}
	
	private void findView(View view){
		this.mListView = (ListView) view.findViewById(R.id.ai_listview);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		this.mAiAdapter = new AiAdapter();
		this.mListView.setAdapter(mAiAdapter);
	}
	
	
	
	
	class AiAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mAiList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView==null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.ai_list_view, null);
				holder.aiTV = (TextView) convertView.findViewById(R.id.ai_tv);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			
			return convertView;
		}
		
	}
	
	static class ViewHolder{
		TextView aiTV;
	}

}
