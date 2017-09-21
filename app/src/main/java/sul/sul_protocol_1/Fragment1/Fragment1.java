package sul.sul_protocol_1.Fragment1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import sul.sul_protocol_1.Fragment0.UserBean;
import sul.sul_protocol_1.R;

/**
 * 친구 목록 보여주는 fragment
 * 사용할 DBHelper : UserDBHelper
 */
public class Fragment1 extends Fragment implements Observer {
    ViewGroup rootView;

    // 내 정보 상위에 띄워주기 위해 필요한 변수 선언
    DBHelper_User myDb;
    DBHelper_Friend friendDb;
    ListView myProfileList;
    ListView friendList;
    ArrayAdapter myAdapter;
    ArrayAdapter friendAdapter;
    //ArrayList<String> fnames;
    ArrayList<String> tasks; // 이름을 담음

    FindFriendActivity fragment;
    //GpsInfo gpsInfo;

    private Subject sub;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate test", "*");
        myDb = new DBHelper_User(this.getContext());
        friendDb = new DBHelper_Friend(this.getContext());
        //myAdapter = new ListViewAdapter(this.getContext(android.R.layout.));
        //friendAdapter = new ListViewAdapter(this.getContext());
        myAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_single_choice);
        friendAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_single_choice);
        //friendAdapter = new ListViewAdapter();
        fragment = new FindFriendActivity();
        // fnames = new ArrayList<String>();
        //gpsInfo = new GpsInfo(getContext());

        // Observer
        friendDb.registerObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("onCreateView test", "*");
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);
        myProfileList = (ListView) rootView.findViewById(R.id.mylist);
        friendList = (ListView) rootView.findViewById(R.id.flist);
        tasks = new ArrayList<>();


        // 우측 하단 친구 추가 버튼
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 친구 검색 Fragment로 이동
                Intent intent = new Intent(getActivity(), FindFriendActivity.class);
                startActivity(intent);
            }
        });

        // sqlite에 회원 정보 추가해야 할 때만 주석 풀어서 쓰고 나중에 지우기
        //insertInfo();

        // oncreate생성 시 sqlite에서 본인 정보 가져오기
        viewMyInfo(myDb);
        viewMyInfo(friendDb);

        return rootView;
    }


    /**
     * 정보 가져올 함수
     */
    public void viewMyInfo(DBHelper db) {
        ArrayAdapter adapter;
        ListView list;

        Cursor res = db.getAllData();
        if (res.getCount() == 0) {
            // 친구가 한명도 없을 때 보여줄 화면 꾸미면 됨!!

        }

        if (db.getName().equals("Friend")) {
            adapter = friendAdapter;
            list = friendList;
            tasks.clear();
        } else {
            adapter = myAdapter;
            list = myProfileList;
        }

        adapter.clear();
        while (res.moveToNext()) {
            // 0: id, 1: name

            Log.i("res.getString(0)", res.getString(0));
            Log.i("res.getString(1)", res.getString(1));
            if (db.getName().equals("Friend")) {
                adapter.add(res.getString(1));
                tasks.add(res.getString(0)); // tasks에 id저장
            } else{
                adapter.add(res.getString(0));
            }
        } // end while

        showList(list, adapter);


    } // end viewMyInfo


    /**
     * 동적 Listview 생성
     */
    protected void showList(final ListView list, ArrayAdapter adapter) {
        list.setAdapter(adapter);

        if (list == friendList) { // 친구 목록에 적용

            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            // 친구 이름을 누를 때 지도보여주기
            list.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("onClick",tasks.get(0));
                    // 리스트에서 선택한 아이디 값
                    String select_id = tasks.get(position);
                    Intent onemapIntent = new Intent(getActivity(), OneFriendMaps2Activity.class);
                    onemapIntent.putExtra("select_id",select_id);
                    startActivity(onemapIntent);

                }
            });

            // 길게 눌러서 친구 삭제
            list.setLongClickable(true);
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //list.setClickable(false);
                    Log.i("position test", String.valueOf(position));
                    // task랑 adapter에서 지우기 아니면 다시 viewmyInfo
                    deleteAsk(position);
                    return true;
                }
            });
        }
        else if(list == myProfileList){
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            // 친구 이름을 누를 때 지도보여주기
            list.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 리스트에서 선택한 아이디 값
                    String select_id = UserBean.getId();
                    Intent onemapIntent = new Intent(getActivity(), OneFriendMaps1Activity.class);
                    onemapIntent.putExtra("select_id",select_id);
                    startActivity(onemapIntent);

                }
            });
        }
    } // end showList

    public void deleteAsk(final int position) {
        String name = (String) friendAdapter.getItem(position);
        final String id = (String) tasks.get(position);

        // 친구 삭제하기전에 한번 물어봄
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // 확인 누르면
                Log.i("삭제 확인", "test");
                DBHelper_Friend db = new DBHelper_Friend(getContext());
                db.delete(id);

                //tasks.remove(position);

                viewMyInfo(friendDb);

                // friendAdapter.remove(position);
                //friendAdapter.
                //friendList.setAdapter(friendAdapter);

                // findFriendActivity.finish();
            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("삭제 취소", "test");
                dialog.dismiss(); //닫기
                //finish();
                // findFriendActivity.finish();
            }
        });
        alert.setMessage(name + "님을 삭제하시겠습니까?");
        alert.show();
    }

    // Observer를 이용해서 이 메소드를 실행시킴
    @Override
    public void update(String fId, String fName) {
        Log.i("Observer Test", "성공");
        friendAdapter.add(fName);
        showList(friendList, friendAdapter);

        viewMyInfo(friendDb);
    }

}