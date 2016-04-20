package sdpd.com.blereminderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Akul on 19-04-2016.
 */
public class ReminderListAdapter extends BaseAdapter {

    private ArrayList<Reminder> remList;
    private LayoutInflater inflater;
    private Context context;

    public ReminderListAdapter(Context context,ArrayList<Reminder> remList)
    {
        this.remList=remList;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(remList!=null)
            return remList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(remList.get(position)!=null)
            return remList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReminderViewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder=new ReminderViewHolder();
            convertView = inflater.inflate(R.layout.row_reminder, parent, false);
            viewHolder.remTitle=(TextView)convertView.findViewById(R.id.remTitleText);
            viewHolder.remLocation=(TextView)convertView.findViewById(R.id.remLocationText);
            viewHolder.doneLayout=(LinearLayout)convertView.findViewById(R.id.doneLayout);
            viewHolder.deleteLayout=(LinearLayout)convertView.findViewById(R.id.delLayout);
            viewHolder.remTimeText=(TextView)convertView.findViewById(R.id.remTimeText);
            convertView.setTag(viewHolder);
        }
        viewHolder=(ReminderViewHolder)convertView.getTag();
        Reminder rem=remList.get(position);
        viewHolder.remTitle.setText(rem.title);
        viewHolder.remLocation.setText(AppConstants.getLocationName(rem.locationId));
        viewHolder.remTimeText.setText(rem.startTime+" to "+rem.endTime);
        return convertView;
    }


    private static class ReminderViewHolder
    {
        TextView remTitle;
        TextView remLocation;
        LinearLayout doneLayout;
        LinearLayout deleteLayout;
        TextView remTimeText;
    }
}
