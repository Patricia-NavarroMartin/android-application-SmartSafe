package com.example.patrinavarro.smartsafe;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Created by Patri Navarro on 06/05/2018.
 */

public class CustomAdapter extends BaseAdapter{
    private Context context;
    private int num_rows;
    private ArrayList<String> title_listV;
    private ArrayList<String> category_listV;
    private ArrayList<Float> amount_listV;
    private ArrayList<String> date_listV;

    public CustomAdapter(Context context, int num_rows, ArrayList<String> title_listV,ArrayList<String> category_listV,ArrayList<Float> amount_listV, ArrayList<String> date_listV) {
        this.context=context;
        this.num_rows = num_rows;
        this.title_listV=title_listV;
        this.category_listV=category_listV;
        this.amount_listV=amount_listV;
        this.date_listV=date_listV;
    }

    @Override
    public int getCount() {
        return num_rows;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    //Responsible for rendering each row
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d("HELP","Inflating listview");
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //Inflates using the specified layout
        View row = layoutInflater.inflate(R.layout.expenses_list,viewGroup,false);
        TextView title_textView = row.findViewById(R.id.title);
        TextView amount_textView = row.findViewById(R.id.amount);
        TextView date_textView = row.findViewById(R.id.date);
        ImageView category_imageView = row.findViewById(R.id.category_logo);

        if(num_rows!=0)
        {
            try {
                title_textView.setText(URLDecoder.decode(title_listV.get(i), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            amount_textView.setText(String.format("%.2f", amount_listV.get(i)) + "€");
            date_textView.setText(date_listV.get(i));
            CategoryLogoSelector cls = new CategoryLogoSelector(context);
            //LOADING IMAGES WITH GLIDE
            Glide.with(context).load(cls.getImageId(category_listV.get(i))).into(category_imageView);
        }
        else
        {
            title_textView.setText(R.string.no_data);
            amount_textView.setText("");
            date_textView.setText("");
        }

        return row;
    }

}
