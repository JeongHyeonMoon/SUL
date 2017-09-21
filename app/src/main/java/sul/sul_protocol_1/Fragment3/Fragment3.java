package sul.sul_protocol_1.Fragment3;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import sul.sul_protocol_1.Fragment0.UserBean;
import sul.sul_protocol_1.R;

public class Fragment3 extends Fragment implements Observer {

    public static Fragment3 FFragment3;
    SelectFriendActivity fragment;

    ViewGroup rootView;

    // 내 정보 상위에 띄워주기 위해 필요한 변수 선언
    GroupDBHelper myDb;
    ListView GroupView;
    ArrayAdapter adapter;
    ArrayList<String> nametasks;
    ArrayList<String> idtasks;
    ArrayList<String> tasks;
    ArrayList<String> friend_id;
    ArrayList<String> friend_phone;
    ArrayList<Integer> friend_arrival;
    ArrayList<LatLng> friend_home;
    ArrayList<String> friend_info;

    // 약속 도착
    String glat, glon;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i("onCreate test", "*");

        FFragment3 = new Fragment3();
        fragment = new SelectFriendActivity();

        myDb = new GroupDBHelper(this.getContext());
        myDb.registerObserver(this);

        tasks = new ArrayList<>();
        nametasks = new ArrayList<>();
        idtasks = new ArrayList<>();
        friend_id = new ArrayList<>();
        friend_arrival = new ArrayList<Integer>();
        friend_phone = new  ArrayList<String>();
        friend_home = new ArrayList<LatLng>();
        friend_info = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_single_choice);

    }


    //@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("onCreateView test", "*");
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);
        GroupView = (ListView) rootView.findViewById(R.id.flist);


        // 우측 하단 친구 추가 버튼
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gdbIntent = new Intent(getActivity(), SelectFriendActivity.class);
                startActivity(gdbIntent);
            }
        });

        // oncreate생성 시 sqlite에서 본인 정보 가져오기
        viewAll();

        return rootView;
    }


    /**
     *  정보 가져올 함수
     */
    public void viewAll() {
        //ArrayAdapter adapter = adapter;
        //ListView list = GroupView;

        Cursor res = myDb.getgidData();
        if (res.getCount() == 0) {
            // 친구가 한명도 없을 때 보여줄 화면 꾸미면 됨!!
            return;
        }

        if(tasks.size()!=0){
            tasks.clear();
        }
        if(adapter.getCount()!= 0){
            adapter.clear();
        }
        while (res.moveToNext()) {
            // 0: id, 1: name
            if (res.getString(0) != null) {
                adapter.add(res.getString(0));
                tasks.add(res.getString(0)); // tasks에 id저장
            }
        } // end while

        showList();

    } // end viewMyInfo

    /**
     * 동적 Listview 생성
     */
    protected void showList() {
        GroupView.setAdapter(adapter);
        GroupView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 이게 있어야 이벤트가 발생함..!!ㅠㅠ

        // 그룹 이름을 누를 때 지도보여주기
        GroupView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i("groupview click","yes");

                SparseBooleanArray booleans = GroupView.getCheckedItemPositions();
                String s = null;
                String select_gid = (String) adapter.getItem(position);

                for (int i = 0; i < adapter.getCount(); i++) {
                    if (booleans.get(i)) {
                        //sb.append(adapter.getItem(i));
                        s = adapter.getItem(i).toString();
                    }
                }

                //  sqlite에서 gid 일치하는 튜플들 검색해욤
                Cursor res = myDb.getgroupData(select_gid);

                if (res.getCount() == 0) {
                    return;
                }

                if(friend_id.size() != 0){
                    friend_id.clear();
                }
                if(friend_info.size() != 0){
                    friend_info.clear();
                }
                if(friend_phone.size() != 0){
                    friend_phone.clear();
                }
                if(friend_arrival.size() != 0){
                    friend_arrival.clear();
                }

                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("GID :" + res.getString(0) + "\n");
                    buffer.append("FNAME :" + res.getString(1) + "\n");
                    buffer.append("FID :" + res.getString(2) + "\n");
                    buffer.append("GTIME: " + res.getString(3) + "\n");
                    buffer.append("GPISITION: " + res.getString(4) + "\n");
                    buffer.append("GX: " + res.getString(5) + "\n");
                    buffer.append("GY: " + res.getString(6) + "\n");

                    System.out.println("get group data test");
                    System.out.println("get group data test" + res.getString(0));
                    System.out.println("get group data test" + res.getString(1));
                    System.out.println("get group data test" + res.getString(2)); // id
                    System.out.println("get group data test" + res.getString(3));
                    System.out.println("get group data test" + res.getString(4));
                    System.out.println("get group data test" + res.getString(5));
                    System.out.println("get group data test" + res.getString(6));

                    // 약속 장소 정보 저장
                    glat = res.getString(5);
                    glon = res.getString(6);

                    friend_id.add(res.getString(2)); // 선택한 그룹에 있는 친구들의 id
                    friend_info.add(res.getString(0));
                    friend_info.add(res.getString(3));
                    friend_info.add(res.getString(4));
                }

                // sqlite에서 gid 일치하는 튜플들 검색해옴 도착정보
                Cursor res2 = myDb.getfarrival(select_gid);
                if (res2.getCount() == 0) {
                    return;
                }
                while (res2.moveToNext()) {
                    System.out.println("get friend data test " + res2.getInt(1));
                    friend_arrival.add(res2.getInt(1)); // 도착 정보 가져오기
                    Log.i("도착 정보", String.valueOf(friend_arrival));
                }

                Cursor res3 = myDb.getphone(select_gid);
                if(res3.getCount() == 0){
                    return;
                }
                while (res3.moveToNext()){
                    friend_phone.add(res3.getString(0));
                    System.out.print("test phone"+ res3.getString(0));
                }

                Intent onemapIntent = new Intent(getActivity(), DrawerPage_Expand.class);

                String select_id = tasks.get(position);
                //String select_id = (String) adapter.getItem(position);
                onemapIntent.putExtra("select_id",select_id); // 선택된 그룹id
                Log.i("friend_id Fragment3", String.valueOf(friend_id.size()));
                onemapIntent.putExtra("friend_id", friend_id);
                onemapIntent.putExtra("friend_arrival", friend_arrival);
                onemapIntent.putExtra("friend_home", friend_home);
                Log.i("pass_fragment","now");
                onemapIntent.putExtra("friend_info", friend_info);
                onemapIntent.putExtra("friend_phone",friend_phone);
                onemapIntent.putExtra("glat", glat);
                onemapIntent.putExtra("glon", glon);
                onemapIntent.putExtra("select_friend", UserBean.getId()); // Use
                startActivity(onemapIntent);
            }
        });

        GroupView.setLongClickable(true);
        GroupView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //list.setClickable(false);
                Log.i("position test", String.valueOf(position));
                // task랑 adapter에서 지우기 아니면 다시 viewmyInfo
                deleteAsk(position);
                return true;
            }
        });
    } // end showList

    public void deleteAsk(final int position) {
        final String id = (String) adapter.getItem(position);
        //final String id = (String) tasks.get(position);

        // 친구 삭제하기전에 한번 물어봄
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // 확인 누르면
                Log.i("삭제 확인", "test");
                GroupDBHelper db = new GroupDBHelper(getContext());
                db.delete(id);

                viewAll();

            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("삭제 취소", "test");
                dialog.dismiss(); //닫기
            }
        });
        alert.setMessage(id + " 그룹을 삭제하시겠습니까?");
        alert.show();
    }

    @Override
    public void update(String gId) {
        Log.i("update", "yes");
        adapter.add(gId);
        tasks.add(gId);
        viewAll();
    }

}