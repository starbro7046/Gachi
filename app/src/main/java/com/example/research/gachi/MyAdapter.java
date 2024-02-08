package com.example.research.gachi;
    import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

    public class MyAdapter extends BaseAdapter{

        /* 아이템을 세트로 담기 위한 어레이 */
        private ArrayList<Data> mItems = new ArrayList<>();

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Data getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item, parent, false);
            }

        /* 'item'에 정의된 위젯에 대한 참조 획득 */
            TextView tv_Title = (TextView) convertView.findViewById(R.id.textTitle) ;
            TextView tv_Address = (TextView) convertView.findViewById(R.id.textAddress) ;
            TextView tv_Id = (TextView) convertView.findViewById(R.id.textId) ;
            TextView tv_Date = (TextView) convertView.findViewById(R.id.textDate) ;

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 Data 재활용 */
            Data myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
            tv_Title.setText(myItem.getTitle());
            tv_Address.setText(myItem.getLoc());
            tv_Id.setText(""+myItem.getId());
            tv_Date.setText(myItem.getDate());


        return convertView;
        }


        /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
        public void addItem( String name, String date,String loc, int id) {

            Data mItem = new Data();

        /* MyItem에 아이템을 setting한다. */
            mItem.setTitle(name);
            mItem.setDate(date);
            mItem.setLoc(loc);
            mItem.setId(id);

        /* mItems에 MyItem을 추가한다. */
            mItems.add(mItem);
        }
    }
