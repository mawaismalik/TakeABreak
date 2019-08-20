package com.example.malix.takeabreak.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.malix.takeabreak.R;

import java.util.List;

public class AppAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<AppList> listStorage;
   // boolean[] checkBoxState;

    public AppAdapter() {

    }

    public AppAdapter(Context context, List<AppList> customizedListView) {
        layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
      //  checkBoxState=new boolean[customizedListView.size()];
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.installed_apps_list, parent, false);

            listViewHolder.textInListView = convertView.findViewById(R.id.list_app_name);
            listViewHolder.imageInListView = convertView.findViewById(R.id.app_icon);
            listViewHolder.checkBoxInListView = convertView.findViewById(R.id.list_app_checkbox);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
            //for check boxes

            AppList userModel = listStorage.get(position);
            if(userModel.isSelected){
                listViewHolder.checkBoxInListView.setChecked(true);
            }else{
                listViewHolder.checkBoxInListView.setChecked(false);
            }
        }
        listViewHolder.textInListView.setText(listStorage.get(position).getName());
        listViewHolder.imageInListView.setImageDrawable(listStorage.get(position).getIcon());



        return convertView;
    }

    void updateView(){
        notifyDataSetChanged();
    }
    static class ViewHolder{

        TextView textInListView;
        ImageView imageInListView;
        CheckBox checkBoxInListView;
    }

}

