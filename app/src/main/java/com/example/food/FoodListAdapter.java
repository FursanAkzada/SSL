package com.example.food;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.zolad.zoominimageview.ZoomInImageView;

import java.util.ArrayList;
import java.util.List;

public class FoodListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private int layout;
    private ArrayList<Food> foodsList, foodsListFiltered;
    public static boolean pilter = false;

    public FoodListAdapter(Context context, int layout, ArrayList<Food> foodsList) {
        this.context = context;
        this.layout = layout;
        this.foodsList = foodsList;
        this.foodsListFiltered = foodsList;
    }

    @Override
    public int getCount() {
        return foodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ZoomInImageView imageView;
        TextView txtPname, txtPic, txtPanel, txtLamp, txtPole, txtDate, txtDevice, txtLatitude, txtLongitude, txtLocation, txtOthers;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtPname = (TextView) row.findViewById(R.id.txtPname);
            holder.txtPic = (TextView) row.findViewById(R.id.txtPic);
            holder.txtPanel = (TextView) row.findViewById(R.id.txtPanel);
            holder.txtLamp = (TextView) row.findViewById(R.id.txtLamp);
            holder.txtPole = (TextView) row.findViewById(R.id.txtPole);
            holder.txtDate = (TextView) row.findViewById(R.id.txtDate);
            holder.txtDevice = (TextView) row.findViewById(R.id.txtDevice);
            holder.txtLatitude = (TextView) row.findViewById(R.id.txtLatitude);
            holder.txtLongitude = (TextView) row.findViewById(R.id.txtLongitude);
            holder.txtLocation = (TextView) row.findViewById(R.id.txtLocation);
            holder.txtOthers = (TextView) row.findViewById(R.id.txtOthers);
            holder.imageView = (ZoomInImageView) row.findViewById(R.id.imgFood);
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder) row.getTag();
        }

        Food food = foodsList.get(position);

        holder.txtPname.setText("Project Name : " + food.getPname());
        holder.txtPic.setText("PIC : " + food.getPic());
        holder.txtPanel.setText("Panel Name : " + food.getPanel());
        holder.txtLamp.setText("Lamp Code : " + food.getLamp());
        holder.txtPole.setText("Pole Code : " + food.getPole());
        holder.txtDate.setText("Date : " + food.getDate());
        holder.txtDevice.setText("Device : " + food.getDevice());
        holder.txtLatitude.setText("Latitude : " + food.getLatitude());
        holder.txtLongitude.setText("Longitude : " + food.getLongitude());
        holder.txtLocation.setText("Location : " + food.getLocation());
        holder.txtOthers.setText("Others : " + food.getOthers());

        byte[] foodImage = food.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage,0,foodImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0){
                    pilter = false;
                    filterResults.count = foodsListFiltered.size();
                    filterResults.values = foodsListFiltered;
                }else {
                    pilter = true;
                    String searchStr = constraint.toString().toLowerCase();
                    ArrayList<Food> resultData = new ArrayList<>();

                    for (Food food:foodsListFiltered){
                        if (food.getLamp().contains(searchStr)||food.getPole().contains(searchStr)){
                            resultData.add(food);
                        }
                        filterResults.count = resultData.size();
                        filterResults.values = resultData;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                foodsList = (ArrayList<Food>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
