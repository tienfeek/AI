/**
 * 
 */
package com.tien.ai.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tien.ai.R;
import com.tien.ai.demain.Suggest;
import com.tien.ai.utils.ViewHolder;

/**
 * TODO
 * 
 * @author wangtianfei01
 * 
 */
public class SuggestAdapter extends BaseAdapter {
    
    private Context context;
    private List<Suggest> suggests = new ArrayList<Suggest>();
    
    public SuggestAdapter(Context context){
        this.context = context;
    }
    
    public void setData(List<Suggest> suggests){
        this.suggests.clear();
        this.suggests.addAll(suggests);
    }
    
    @Override
    public int getCount() {
        return suggests.size();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.suggest_listview_item, null);
        }
        
        TextView contextTV = ViewHolder.get(convertView, R.id.content_tv);
        
        Suggest suggest = suggests.get(position);
        contextTV.setText("反馈"+(position+1) + ":" + suggest.getContent());
        
        return convertView;
    }
    
}
