package example.com.expandablechecklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ABHISHEK on 5/12/2015.
 */
public class ExpandListAdapter extends BaseExpandableListAdapter {

    private ArrayList<ArrayList<String>> mGroupList = new ArrayList<>();

    /*
     *  Raw Data
     */
    String[] testChildData =  {"10","20","30", "40", "50"};
    String[] testgroupData =  {"Apple","Banana","Mango", "Orange", "Pineapple", "Strawberry"};
    Context mContext;
    ArrayList<ArrayList<Boolean>> selectedCheckBox = new ArrayList<>();
    TotalListener mListener;

    public void setmListener(TotalListener mListener) {
        this.mListener = mListener;
    }

    public void setmGroupList(ArrayList<ArrayList<String>> mGroupList) {
        this.mGroupList = mGroupList;
    }

    class ViewHolder {
        public TextView groupName;
        public CheckBox childCheckBox;
    }

    public ExpandListAdapter(Context context) {
        mContext = context;

        //Add raw data into Group List Array
        for(int i = 0; i < testgroupData.length; i++){
            ArrayList<String> prices = new ArrayList<>();
            for(int j = 0; j < testChildData.length; j++) {
                prices.add(testChildData[j]);
        }
            mGroupList.add(i, prices);
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroupList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroupList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_layout, null);
            holder = new ViewHolder();
            holder.groupName = (TextView) convertView.findViewById(R.id.group_text_view);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //set your group item data
        holder.groupName.setText(testgroupData[groupPosition]);
            return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_layout, null);
            holder = new ViewHolder();
            holder.childCheckBox = (CheckBox) convertView.findViewById(R.id.child_check_box);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        holder.childCheckBox.setText(mGroupList.get(groupPosition).get(childPosition));
                if(selectedCheckBox.size() <= groupPosition) {
                    ArrayList<Boolean> childState = new ArrayList<>();
                    for(int i= 0; i < mGroupList.get(groupPosition).size(); i++){
                        if(childState.size() > childPosition)
                        childState.add(childPosition, false);
                        else
                            childState.add(false);
                    }
                    if(selectedCheckBox.size() > groupPosition) {
                        selectedCheckBox.add(groupPosition, childState);
                    }else
                        selectedCheckBox.add(childState);
                }else{
                    holder.childCheckBox.setChecked(selectedCheckBox.get(groupPosition).get(childPosition));
                }
            holder.childCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean state = selectedCheckBox.get(groupPosition).get(childPosition);
                selectedCheckBox.get(groupPosition).remove(childPosition);
                selectedCheckBox.get(groupPosition).add(childPosition,  state ? false : true);

                //Below code is to get the sum of checked prices
                int sum = 0;
                for(int j = 0 ; j < selectedCheckBox.size(); j++){
                for(int i = 0; i < selectedCheckBox.get(groupPosition).size(); i++) {
                    if (selectedCheckBox.get(j).get(i)) {
                        sum += Integer.parseInt(mGroupList.get(j).get(i));
                    }
                }
                    mListener.onTotalChanged(sum);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
