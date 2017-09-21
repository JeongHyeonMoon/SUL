package sul.sul_protocol_1.Fragment3;

/**
 * Created by user on 2016-06-06.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import java.util.Calendar;

import sul.sul_protocol_1.R;

public class DateTimePicker extends LinearLayout {
    /**
     * �좎쭨�� �쒓컙�� 諛붾�� �� �몄텧�섎뒗 由ъ뒪�� �덈줈 �뺤쓽
     */
    public interface OnDateTimeChangedListener {
        void onDateTimeChanged(DateTimePicker view, int year, int monthOfYear, int dayOfYear, int hourOfDay, int minute);
    }

    /**
     * 由ъ뒪�� 媛앹껜
     */
    private OnDateTimeChangedListener onDateTimeChangedListener;

    /**
     * �좎쭨�좏깮 �꾩젽
     */
    private final DatePicker datePicker;

    /**
     * 泥댄겕諛뺤뒪
     */
    //  private final CheckBox enableTimeCheckBox;

    /**
     * �쒓컙�좏깮 �꾩젽
     */
    private final TimePicker timePicker;

    /**
     * �앹꽦��
     *
     * @param context
     */
    public DateTimePicker(final Context context){
        this(context, null);
    }

    /**
     * �앹꽦��
     *
     * @param context
     * @param attrs
     */
    public DateTimePicker(Context context, AttributeSet attrs){
        super(context, attrs);

        // XML �덉씠�꾩썐�� �명뵆�덉씠�섑븿
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.datetimepicker, this, true);

        // �쒓컙 �뺣낫 李몄“
        Calendar calendar = Calendar.getInstance();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DataTimePicker);
        final int _currentYear = a.getInt(R.styleable.DataTimePicker_year, calendar.get(Calendar.YEAR));
        final int _currentMonth = a.getInt(R.styleable.DataTimePicker_month, calendar.get(Calendar.MONTH));
        final int _currentDay = a.getInt(R.styleable.DataTimePicker_day, calendar.get(Calendar.DAY_OF_MONTH));
        final int _currentHour = a.getInt(R.styleable.DataTimePicker_hour, calendar.get(Calendar.HOUR_OF_DAY));
        final int _currentMinute = a.getInt(R.styleable.DataTimePicker_minute, calendar.get(Calendar.MINUTE));

        // �좎쭨�좏깮 �꾩젽 珥덇린��
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        datePicker.init(_currentYear, _currentMonth, _currentDay, new OnDateChangedListener() {
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // �덈줈 �뺤쓽�� 由ъ뒪�덈줈 �대깽�� �꾨떖
                if(onDateTimeChangedListener != null){
                    onDateTimeChangedListener.onDateTimeChanged(
                            DateTimePicker.this, year, monthOfYear, dayOfMonth,
                            timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                }
            }
        });

        // �쒓컙�좏깮 �꾩젽 �대깽�� 泥섎━
        timePicker = (TimePicker)findViewById(R.id.timePicker);
        timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(onDateTimeChangedListener != null) {
                    onDateTimeChangedListener.onDateTimeChanged(
                            DateTimePicker.this, datePicker.getYear(),
                            datePicker.getMonth(), datePicker.getDayOfMonth(), hourOfDay, minute);
                }
            }
        });

        timePicker.setCurrentHour(_currentHour);
        timePicker.setCurrentMinute(_currentMinute);
    }

    public void setOnDateTimeChangedListener(OnDateTimeChangedListener onDateTimeChangedListener){
        this.onDateTimeChangedListener = onDateTimeChangedListener;
    }

    public void updateDateTime(int year, int monthOfYear, int dayOfMonth, int currentHour, int currentMinute){
        datePicker.updateDate(year, monthOfYear, dayOfMonth);
        timePicker.setCurrentHour(currentHour);
        timePicker.setCurrentMinute(currentMinute);
    }

    public void updateDate(int year, int monthOfYear, int dayOfMonth){
        datePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    public void setIs24HourView(final boolean is24HourView){
        timePicker.setIs24HourView(is24HourView);
    }

    public int getYear() {
        return datePicker.getYear();
    }

    public int getMonth() {
        return datePicker.getMonth();
    }

    public int getDayOfMonth() {
        return datePicker.getDayOfMonth();
    }

    public int getCurrentHour() {
        return timePicker.getCurrentHour();
    }

    public int getCurrentMinute() {
        return timePicker.getCurrentMinute();
    }

}