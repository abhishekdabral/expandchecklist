package example.com.expandablechecklist;

import android.content.Context;
import android.util.Log;
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
    ArrayList<ArrayList<Boolean>> selectedChildCheckBoxStates = new ArrayList<>();
    ArrayList<Boolean> selectedParentCheckBoxesState = new ArrayList<>();
    TotalListener mListener;

    public void setmListener(TotalListener mListener) {
        this.mListener = mListener;
    }

    public void setmGroupList(ArrayList<ArrayList<String>> mGroupList) {
        this.mGroupList = mGroupList;
    }

    class ViewHolder {
        public CheckBox groupName;
        public TextView dummyTextView; // View to expand or shrink the list
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

        //initialize default check states of checkboxes
        initCheckStates(false);
    }

    /**
     * Called to initialize the default check states of items
     * @param defaultState : false
     */
    private void initCheckStates(boolean defaultState) {
        for(int i = 0 ; i < mGroupList.size(); i++){
            selectedParentCheckBoxesState.add(i, defaultState);
            ArrayList<Boolean> childStates = new ArrayList<>();
            for(int j = 0; j < mGroupList.get(i).size(); j++){
                childStates.add(defaultState);
            }

            selectedChildCheckBoxStates.add(i, childStates);
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
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_layout, null);
            holder = new ViewHolder();
            holder.groupName = (CheckBox) convertView.findViewById(R.id.group_chk_box);
            holder.dummyTextView = (TextView) convertView.findViewById(R.id.dummy_txt_view);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.groupName.setText(testgroupData[groupPosition]);
        if(selectedParentCheckBoxesState.size() <= groupPosition){
            selectedParentCheckBoxesState.add(groupPosition, false);
        }else {
            holder.groupName.setChecked(selectedParentCheckBoxesState.get(groupPosition));
        }



        holder.groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //Callback to expansion of group item
                if(!isExpanded)
                mListener.expandGroupEvent(groupPosition, isExpanded);

                boolean state = selectedParentCheckBoxesState.get(groupPosition);
                Log.d("TAG", "STATE = " + state);
                selectedParentCheckBoxesState.remove(groupPosition);
                selectedParentCheckBoxesState.add(groupPosition, state ? false : true);

                    for (int i = 0; i < mGroupList.get(groupPosition).size(); i++) {

                            selectedChildCheckBoxStates.get(groupPosition).remove(i);
                            selectedChildCheckBoxStates.get(groupPosition).add(i, state ? false : true);
                    }
                notifyDataSetChanged();
                showTotal(groupPosition);
            }
        });


        //callback to expand or shrink list view from dummy text click
        holder.dummyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //Callback to expansion of group item
                mListener.expandGroupEvent(groupPosition, isExpanded);
            }
        });

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
                if(selectedChildCheckBoxStates.size() <= groupPosition) {
                    ArrayList<Boolean> childState = new ArrayList<>();
                    for(int i= 0; i < mGroupList.get(groupPosition).size(); i++){
                        if(childState.size() > childPosition)
                        childState.add(childPosition, false);
                        else
                            childState.add(false);
                    }
                    if(selectedChildCheckBoxStates.size() > groupPosition) {
                        selectedChildCheckBoxStates.add(groupPosition, childState);
                    }else
                        selectedChildCheckBoxStates.add(childState);
                }else{
                    holder.childCheckBox.setChecked(selectedChildCheckBoxStates.get(groupPosition).get(childPosition));
                }
            holder.childCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean state = selectedChildCheckBoxStates.get(groupPosition).get(childPosition);
                selectedChildCheckBoxStates.get(groupPosition).remove(childPosition);
                selectedChildCheckBoxStates.get(groupPosition).add(childPosition, state ? false : true);

                showTotal(groupPosition);

            }
        });

        return convertView;
    }

    /**
     * Called to reflect the sum of checked prices
     * @param groupPosition : group position of list
     */
    private void showTotal(int groupPosition) {
        //Below code is to get the sum of checked prices
        int sum = 0;
        for(int j = 0 ; j < selectedChildCheckBoxStates.size(); j++) {
            Log.d("TAG", "J = " + j);
                for (int i = 0; i < selectedChildCheckBoxStates.get(groupPosition).size(); i++) {
                    Log.d("TAG", "I = " + i);

                    if (selectedChildCheckBoxStates.get(j).get(i)) {
                        sum += Integer.parseInt(mGroupList.get(j).get(i));
                    }
                }
            }
        mListener.onTotalChanged(sum);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
