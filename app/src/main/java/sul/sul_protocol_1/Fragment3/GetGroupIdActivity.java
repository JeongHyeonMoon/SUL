package sul.sul_protocol_1.Fragment3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import sul.sul_protocol_1.R;

// 사용자에게 그룹 ID 받기 위해 만듦.
public class GetGroupIdActivity extends AppCompatActivity {

    // 화면 닫기
    public static Activity GGetGroupIdActivity;
    private EditText editTextgid;
    //InputMethodManager imm;


    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
    TextView textView1;
    DateTimePicker dateTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getgroupid);

        GGetGroupIdActivity = GetGroupIdActivity.this;

        //변화되는 시간을 표시
        textView1 = (TextView)findViewById(R.id.textView1);
        dateTimePicker = (DateTimePicker)findViewById(R.id.dateTimePicker);
        Button btn = (Button) findViewById(R.id.createGroupBtn);
        // �대깽�� 泥섎━
        dateTimePicker.setOnDateTimeChangedListener(new DateTimePicker.OnDateTimeChangedListener() {
            public void onDateTimeChanged(DateTimePicker view, int year,
                                          int monthOfYear, int dayOfYear, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfYear, hourOfDay, minute);

                // 諛붾�� �쒓컙 �띿뒪�몃럭�� �쒖떆
                textView1.setText(dateFormat.format(calendar.getTime()));
            }
        });

        // �꾩옱 �쒓컙 �띿뒪�몃럭�� �쒖떆
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateTimePicker.getYear(), dateTimePicker.getMonth(), dateTimePicker.getDayOfMonth(), dateTimePicker.getCurrentHour(), dateTimePicker.getCurrentMinute());
        textView1.setText(dateFormat.format(calendar.getTime()));

        // 키보드 없애는 함수 수정 필요
        /*
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        editTextgid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if(keyCode == event.KEYCODE_ENTER)
                {
                    imm.hideSoftInputFromWindow(editTextgid.getWindowToken(),0);//키보드를 없앤다.
                    return true;
                }
                return false;
            }
        });
*/

        btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                editTextgid = (EditText) findViewById(R.id.g_id);
                String g_id = editTextgid.getText().toString();



                GroupBean.setgid(g_id);
                GroupBean.setYear(dateTimePicker.getYear());
                GroupBean.setMonth(dateTimePicker.getMonth());
                GroupBean.setDay(dateTimePicker.getDayOfMonth());
                GroupBean.setCurrentHour(dateTimePicker.getCurrentHour());
                GroupBean.setCurrentMinute(dateTimePicker.getCurrentMinute());


                //장소 추가
                //Intent gdbIntent = new Intent(getApplicationContext(), GroupDBActivity.class);
                Intent gdbIntent = new Intent(getApplicationContext(), SelectPlaceActivity.class);
                startActivity(gdbIntent);
            }
        }); //end OnclickListener
    }

}
/////////////////////////////////////