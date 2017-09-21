package sul.sul_protocol_1.Fragment1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import sul.sul_protocol_1.R;

/**
 * Created by 은혜 on 2016-06-03.
 */
public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();
    //private ArrayList<String> arrayname = new ArrayList<String>();

    public ListViewAdapter(){}

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
        //return listViewItemList;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView tvname = (TextView) convertView.findViewById(R.id.nameTextView);
        Button btn = (Button) convertView.findViewById(R.id.addBtn);
        btn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(context, DBActivity_Friend.class);
                context.startActivity(dbIntent);
            }
        });

        ListViewItem listViewItem = listViewItemList.get(position);

        tvname.setText(listViewItem.getName());

        return convertView;
    }



    public void addItem(String id, String name){
        ListViewItem item = new ListViewItem();

        item.setId(id);
        item.setName(name);

        listViewItemList.add(item);
    }

}
