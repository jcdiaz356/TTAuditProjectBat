package com.dataservicios.ttauditprojectbat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.model.Media;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jaime Eduardo on 15/10/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Activity activity;
    ArrayList<ViewHolder> holders = new ArrayList<ViewHolder>();
//    ArrayList<String> f ;
    ArrayList<Media> medias;

    public ImageAdapter(Activity activity, ArrayList<Media> medias) {
        this.activity = activity;
        this.medias = medias;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
   // public ImageAdapter() {

   // }

    public int getCount() {
        return medias.size();
    }

    public ViewHolder getItem(int position) {
        return holders.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.galleryitem, null);
            holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
            convertView.setTag(holder);
        }  else {
            holder = (ViewHolder) convertView.getTag();
        }


        //String pathFile = f.get(position) ;
        String pathFile = medias.get(position).getPathFile().toString() ;
        File imgFile = new File(pathFile);

        Picasso.get()
                .load(imgFile)
                .resize(100, 100)
                .centerCrop()
                .error(R.drawable.ic_operation_photo)
                .into(holder.imageview);

        holder.checkbox.setChecked(medias.get(position).getSelectedFile());
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCheckBox(position, !medias.get(position).getSelectedFile());
                //Toast.makeText(activity,String.valueOf(position),Toast.LENGTH_SHORT).show();
            }
        });

//        holder.imageview.setImageBitmap(myBitmap1);
        holders.add(holder);
        return convertView;
    }

    /**
     * Remove all checkbox Selection
     **/
    public void removeSelection() {
        //mSelectedItemsIds = new SparseBooleanArray();
        //ArrayList<Media> mediaAll;
        //mediaAll =  imageAdapter.getAllMedias();
        int count = medias.size();
        for (int i=0 ; i < count; i++) {
            if(medias.get(i).getSelectedFile()){
                File fileSelected;
                fileSelected = new File(medias.get(i).getPathFile().toString());
                fileSelected.delete();
                //medias.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Check the Checkbox if not checked
     **/
    public void checkCheckBox(int position, Boolean value) {
        if (value)
            //mSelectedItemsIds.put(position, true);
            medias.get(position).setSelectedFile(value);
        else
            //mSelectedItemsIds.delete(position);
            medias.get(position).setSelectedFile(value);

        notifyDataSetChanged();
    }

    /**
     * TOtal de elementos marcados con check
     * @return int
     */
    public int countCheckedItems(){

        int countChecked = 0;
        int count = medias.size();
        for (int i=0 ; i < count; i++) {
            if(medias.get(i).getSelectedFile()){
                countChecked ++;
            }
        }
        return countChecked;
    }

    /**
     * Return the selected Checkbox IDs
     **/
//    public SparseBooleanArray getSelectedIds() {
//        return mSelectedItemsIds;
//    }

    public ArrayList<Media> getAllMedias() {
        return medias;
    }



    public class  ViewHolder {
        public ImageView imageview;
        public CheckBox checkbox;

    }

}
