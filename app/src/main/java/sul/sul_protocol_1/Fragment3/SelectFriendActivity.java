package sul.sul_protocol_1.Fragment3;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import sul.sul_protocol_1.Fragment2.FriendDBHelper;
import sul.sul_protocol_1.R;

// 내 친구 목록 보여주는 화면
public class SelectFriendActivity extends Activity {

    // 화면 닫기
    public static Activity SSelectFriendActivity;

    FriendDBHelper myDb;

    ListView friendView;
    Button creategroupbtn;
    ArrayAdapter adapter; // iD를 담음
    ArrayList<String> tasks; // 이름을 담음

    // HashMap<String, String> task = new HashMap<String, String>();

    // 서버에서 받아온 변수 값을 sqjite에 전달해줄 자바빈 객체
    //SelectFriendBean selectfriendBean = new SelectFriendBean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_fragment3_flist);

        SSelectFriendActivity = SelectFriendActivity.this;

        myDb = new FriendDBHelper(this);
        System.out.println("db test success");

        tasks = new ArrayList<>();
        friendView = (ListView) findViewById(R.id.flist);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice);

        creategroupbtn =  (Button) findViewById(R.id.createGroupBtn);
        creategroupbtn.setOnClickListener(btnClickListener);

        viewAll();
    }

    public void viewAll() {
        //DatabaseHelper인 myDb객체를 가져와 DBActivity의 getAlldata 함수를 호출
        System.out.println("view All test success");
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            //show message
            return;
        }
        System.out.println("res test");

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("ID :" + res.getString(0) + "\n");
            buffer.append("NAME :" + res.getString(1) + "\n");
            buffer.append("ADRSS :" + res.getString(2) + "\n");
            buffer.append("X :" + res.getString(3) + "\n");
            buffer.append("Y :" + res.getString(4) + "\n");
            buffer.append("PIC :" + res.getString(5) + "\n\n");


            if (res.getString(1) != null) {
                //System.out.println("test res.getString(0) : " + res.getString(0));
                //System.out.println("test res.getString(1) : " + res.getString(1));
                //System.out.println("test res.getString(2) : " + res.getString(2));
                //System.out.println("test res.getString(3) : " + res.getString(3));
                //System.out.println("test res.getString(4) : " + res.getString(4));
                //System.out.println("test res.getString(5) : " + res.getString(5));

                //System.out.println("test tasks: " + res.getString(1));

                adapter.add(res.getString(1)); //adapter에 이름 저장
                tasks.add(res.getString(0)); // tasks에 id저장
            }

        }

        //show all data
        showList();

    }

    /**
     * 친구 리스트를 보여줄 함수
     */
    protected void showList() {

        friendView.setAdapter(adapter);
        friendView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        friendView.setOnItemClickListener(new ListView.OnItemClickListener(){
            // 리스트를 클릭했을 때 나타나는 이벤트
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 리스트에서 선택한 아이디 값
                //String select_id = tasks.get(position);
                //Intent onemapIntent = new Intent(SelectFriendActivity.this, OneFriendMaps2Activity.class);
                //onemapIntent.putExtra("select_id",select_id);
                //startActivity(onemapIntent);
                confirm(friendView);
            }
        });
    } // end showList

    /**
     * 선택된 아이디들 방만들기
     * @param v
     */
    public void confirm(View v){
        SparseBooleanArray booleans = friendView.getCheckedItemPositions();
        StringBuilder sb = new StringBuilder();

        // FriendForGroupbean이 비어있지 않다면 clear
        if(GroupBean.getlengh()>0){
            GroupBean.clearid();
            GroupBean.clearname();
        }

        for (int i = 0; i < adapter.getCount(); i++) {
            if (booleans.get(i)) {
                sb.append(adapter.getItem(i));

                // 여기서 FriendForGroupBean에 이름, 아이디 추가
                GroupBean.setname(adapter.getItem(i).toString());
                GroupBean.setid(tasks.get(i)); //id는 임의 설정
            }
        }

        //Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 방만들기 버튼이 눌리면 sqlite에 저장
     */
    public Button.OnClickListener btnClickListener = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent gdbIntent = new Intent(getApplicationContext(), GetGroupIdActivity.class);
            startActivity(gdbIntent);
        }
    }; //end button click listener

}
