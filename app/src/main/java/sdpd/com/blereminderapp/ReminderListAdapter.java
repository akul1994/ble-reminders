package sdpd.com.blereminderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Akul on 19-04-2016.
 */
public class ReminderListAdapter extends BaseAdapter {

    private ArrayList<Reminder> remList;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Integer> changeView;
    private AddReminderListener addReminderListener;

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

    public void setAddReminderListener(AddReminderListener addReminderListener)
    {
        this.addReminderListener=addReminderListener;
    }
    public void setRemList(ArrayList<Reminder> remList)
    {
        this.remList.clear();
        this.remList=remList;
        notifyDataSetChanged();
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
            viewHolder.dateText=(TextView)convertView.findViewById(R.id.dateText);
            viewHolder.monthText=(TextView)convertView.findViewById(R.id.monthText);
            viewHolder.reminderRow=(LinearLayout)convertView.findViewById(R.id.reminderRowLayout);
            viewHolder.reminderRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addReminderListener.onReminderSelected((Integer)v.getTag());
                }
            });
            viewHolder.doneLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addReminderListener.onReminderDone((Integer) v.getTag());
                }
            });
            viewHolder.deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addReminderListener.onReminderDone((Integer)v.getTag());
                }
            });
            convertView.setTag(viewHolder);
        }
        viewHolder=(ReminderViewHolder)convertView.getTag();
        viewHolder.reminderRow.setTag(position);
        viewHolder.doneLayout.setTag(position);
        viewHolder.deleteLayout.setTag(position);
        Reminder rem=remList.get(position);
        viewHolder.remTitle.setText(rem.title);
        viewHolder.remLocation.setText(Utils.getLocationName(rem.locationId));
        viewHolder.remTimeText.setText(rem.startTime+" to "+rem.endTime);
        viewHolder.dateText.setText(getDateAndMonth(rem.date,1));
        viewHolder.monthText.setText(getDateAndMonth(rem.date,0));
        return convertView;
    }


    private static class ReminderViewHolder
    {
        TextView remTitle;
        TextView remLocation;
        LinearLayout doneLayout;
        LinearLayout deleteLayout;
        TextView remTimeText;
        TextView dateText;
        TextView monthText;
        LinearLayout reminderRow;
    }
    public String getDateAndMonth(String tempdate,int flag)
    {
        String format1;
        Date date=null;
        SimpleDateFormat defFormat=new SimpleDateFormat("dd/MM/yyyy");
        if(flag==0)
            format1="MMM";
        else
        format1="dd";
        SimpleDateFormat format = new SimpleDateFormat(format1);
        try {
            date=defFormat.parse(tempdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String formattedDate=format.format(date);
        return formattedDate;

    }
}
