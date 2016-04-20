package sdpd.com.blereminderapp;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Akul on 13-04-2016.
 */
public class AddReminderFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private View mView;
    private Spinner mLocSpinner;
    private RadioButton dateRB1, dateRB2, dateRB3;
    private RadioButton alertRB1, alertRB2, alertRB3;
    private TextView startTimeTV, endTimeTV;
    private Context context;
    private Calendar cal;
    private TimePickerDialog starttimePickerDialog, endtimePickerDialog;
    private Button addButton;
    private Reminder rem;
    private AddReminderListener addReminderListener;
    private EditText noteText;
    private EditText titleText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_reminder, container, false);
        context = getActivity();

        rem = new Reminder();
        cal = Calendar.getInstance();
        rem.date = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
        rem.startTime = "20:00";
        rem.endTime = "21:00";
        rem.alertTime = 15;
        initialize();

        return mView;
    }

    public void setAddReminderListener(AddReminderListener addReminderListener) {
        this.addReminderListener = addReminderListener;
    }

    private void initialize() {
        addButton = (Button) mView.findViewById(R.id.addButton);
        mLocSpinner = (Spinner) mView.findViewById(R.id.locSpinner);
        dateRB1 = (RadioButton) mView.findViewById(R.id.radioButton);
        dateRB1.setTextColor(getResources().getColorStateList(R.color.radio_button_selector));
        dateRB2 = (RadioButton) mView.findViewById(R.id.radioButton2);
        dateRB2.setTextColor(getResources().getColorStateList(R.color.radio_button_selector));
        dateRB3 = (RadioButton) mView.findViewById(R.id.radioButton3);
        dateRB3.setTextColor(getResources().getColorStateList(R.color.radio_button_selector));
        alertRB1 = (RadioButton) mView.findViewById(R.id.radioButton4);
        alertRB1.setTextColor(getResources().getColorStateList(R.color.radio_button_selector));
        alertRB2 = (RadioButton) mView.findViewById(R.id.radioButton5);
        alertRB2.setTextColor(getResources().getColorStateList(R.color.radio_button_selector));
        alertRB3 = (RadioButton) mView.findViewById(R.id.radioButton6);
        alertRB3.setTextColor(getResources().getColorStateList(R.color.radio_button_selector));
        startTimeTV = (TextView) mView.findViewById(R.id.startTextView);
        endTimeTV = (TextView) mView.findViewById(R.id.endTextView);
        noteText = (EditText) mView.findViewById(R.id.notesText);
        titleText = (EditText) mView.findViewById(R.id.reminderTitleText);
        startTimeTV.setOnClickListener(this);
        endTimeTV.setOnClickListener(this);
        dateRB1.setOnClickListener(this);
        dateRB2.setOnClickListener(this);
        dateRB3.setOnClickListener(this);
        alertRB1.setOnClickListener(this);
        alertRB2.setOnClickListener(this);
        alertRB3.setOnClickListener(this);
        addButton.setOnClickListener(this);
        setTimeView(startTimeTV, rem.startTime);
        setTimeView(endTimeTV, rem.endTime);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.radioButton:
                rem.date = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                dateRB3.setText(rem.date);
                break;
            case R.id.radioButton2:
                Calendar tommCal = Calendar.getInstance();
                tommCal.add(tommCal.DATE, 1);
                rem.date = tommCal.get(Calendar.DAY_OF_MONTH) + "/" + (tommCal.get(Calendar.MONTH) + 1) + "/" + tommCal.get(Calendar.YEAR);
                dateRB3.setText(rem.date);
                break;
            case R.id.radioButton3:


                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        this,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
                break;
            case R.id.radioButton4:
                rem.alertTime = 15;
                break;
            case R.id.radioButton5:
                rem.alertTime = 30;
                break;
            case R.id.radioButton6:
                rem.alertTime = -1;
                break;

            case R.id.startTextView:
                starttimePickerDialog = TimePickerDialog.newInstance(this, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true);
                starttimePickerDialog.show(getActivity().getFragmentManager(), "Timepickerdialog");
                break;
            case R.id.endTextView:
                endtimePickerDialog = TimePickerDialog.newInstance(this, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true);
                endtimePickerDialog.show(getActivity().getFragmentManager(), "Timepickerdialog");
                break;
            case R.id.addButton:
                rem.locationId = (mLocSpinner.getSelectedItemId() - 1) + "";
                rem.notes = noteText.getText().toString();
                rem.title = titleText.getText().toString();
                if (rem.title == null || rem.title.length() < 1)
                    showError("Please enter a valid title!");
                else
                    addReminderListener.onReminderAdded(rem);
                break;

        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear += 1;
        rem.date = dayOfMonth + "/" + monthOfYear + "/" + year;
        dateRB3.setText(rem.date);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        TimePickerDialog tpd = (TimePickerDialog) getActivity().getFragmentManager().findFragmentByTag("Timepickerdialog");
        if (tpd == starttimePickerDialog) {
            rem.startTime = hourOfDay + ":" + minute;
            setTimeView(startTimeTV, rem.startTime);
        } else {
            rem.endTime = hourOfDay + ":" + minute;
            setTimeView(endTimeTV, rem.endTime);
        }
    }

    private void setTimeView(TextView timeView, String time) {
        SpannableString content = new SpannableString(time);
        content.setSpan(new UnderlineSpan(), 0, time.length(), 0);
        timeView.setText(content);
    }

    public void showError(String message) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(R.string.error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }
}
