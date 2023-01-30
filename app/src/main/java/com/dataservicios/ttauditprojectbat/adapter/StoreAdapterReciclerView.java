package com.dataservicios.ttauditprojectbat.adapter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.model.RouteStoreTime;
import com.dataservicios.ttauditprojectbat.model.Store;
import com.dataservicios.ttauditprojectbat.model.User;
import com.dataservicios.ttauditprojectbat.repo.CompanyRepo;
import com.dataservicios.ttauditprojectbat.repo.RouteStoreTimeRepo;
import com.dataservicios.ttauditprojectbat.repo.UserRepo;
import com.dataservicios.ttauditprojectbat.view.StoreAuditActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jcdia on 12/05/2017.
 */

public class StoreAdapterReciclerView extends RecyclerView.Adapter<StoreAdapterReciclerView.StoreViewHolder>  {

    private ArrayList<Store> stores;
    private int                 resource;
    private int                 route_id;
    private Activity activity;
    private Filter fRecords;
    private User                user;
    private Company             company;


    public StoreAdapterReciclerView(ArrayList<Store> stores,int route_id, int resource, Activity activity) {
        this.stores     = stores;
        this.route_id     = route_id;
        this.resource   = resource;
        this.activity   = activity;

//        gpsTracker = new GPSTracker(activity);
//        if(!gpsTracker.canGetLocation()){
//            gpsTracker.showSettingsAlert();
//        }

        DatabaseManager.init(activity);
        UserRepo    userRepo    = new UserRepo(activity);
        CompanyRepo company_repo= new CompanyRepo(activity);

        user    = (User)    userRepo.findFirstReg();
        company = (Company) company_repo.findFirstReg();

    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        view.setOnClickListener(new  View.OnClickListener(){
            public void onClick(View v)
            {
                //action
                //Toast.makeText(activity,"dfgdfg",Toast.LENGTH_SHORT).show();
            }
        });

        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoreViewHolder holder, int position) {
        final Store store = stores.get(position);

        holder.tvId.setText("ID : " + String.valueOf(store.getId()));
        holder.tvFullName.setText(store.getFullname());
        holder.tvAddress.setText(String.valueOf(store.getAddress()));
        holder.tvDistrict.setText(String.valueOf(store.getDistrict()));
        holder.tvType.setText(String.valueOf(store.getType()));
        //holder.tvOwner.setText(String.valueOf(store.getOwner()));
        holder.tvOwner.setText( String.valueOf(store.getComment()));

        if(store.getStatus() >= 1)  {
            holder.btAudit.setVisibility(View.INVISIBLE);
            holder.imgStatus.setVisibility(View.VISIBLE) ;
        } else {
            holder.btAudit.setVisibility(View.VISIBLE) ;
            holder.imgStatus.setVisibility(View.INVISIBLE);
        }

        holder.btShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "ID Store: " + store.getId() + " \nTienda: " + store.getFullname()  ;
                String shareSub = "Ruta";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_TITLE, shareBody);
                activity.startActivity(Intent.createChooser(sharingIntent, "Share using"));

            }
        });

        holder.btAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String created_at = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
                RouteStoreTime routeStoreTime = new RouteStoreTime();
                routeStoreTime.setCompany_id(company.getId());
                routeStoreTime.setStore_id(store.getId());
                routeStoreTime.setRoute_id(route_id);
                //routeStoreTime.setRoute_id(store.getRoute_id());
                routeStoreTime.setUser_id(user.getId());
                routeStoreTime.setLat_open(0);
//                routeStoreTime.setLat_open(lat);
                routeStoreTime.setLon_open(0);
//                routeStoreTime.setLon_open(lon);
                routeStoreTime.setTime_open(created_at);

                RouteStoreTimeRepo routeStoreTimeRepo = new RouteStoreTimeRepo(activity);
                routeStoreTimeRepo.deleteAll();
                routeStoreTimeRepo.create(routeStoreTime);

                int store_id = store.getId();
//              Toast.makeText(activity, String.valueOf(store.id), Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putInt("store_id", Integer.valueOf(store_id));
                bundle.putInt("route_id", Integer.valueOf(route_id));
                Intent intent = new Intent(activity,StoreAuditActivity.class);
                intent.putExtras(bundle);
                activity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {

        return stores.size();
    }

//    @Override
//    public Filter getFilter() {
//        if(fRecords == null) {
//            fRecords=new RecordFilter();
//        }
//        return fRecords;
//    }
//
    public class StoreViewHolder extends RecyclerView.ViewHolder {
        private TextView tvId;
        private TextView tvFullName;
        private TextView tvAddress;
        private TextView tvDistrict;
        private TextView tvType;
        private TextView tvOwner;
        private Button btShared;
        private Button btAudit;
        private ImageView imgStatus;

        public StoreViewHolder(View itemView) {
            super(itemView);
            tvId            = (TextView)    itemView.findViewById(R.id.tvId);
            tvFullName      = (TextView)    itemView.findViewById(R.id.tvFullName);
            tvAddress       = (TextView)    itemView.findViewById(R.id.tvAddress);
            tvDistrict      = (TextView)    itemView.findViewById(R.id.tvDistrict);
            tvType          = (TextView)    itemView.findViewById(R.id.tvType);
            tvOwner         = (TextView)    itemView.findViewById(R.id.tvOwner);
            btShared        = (Button)      itemView.findViewById(R.id.btShared);
            btAudit         = (Button)      itemView.findViewById(R.id.btAudit);
            imgStatus       = (ImageView)   itemView.findViewById(R.id.imgStatus);
        }
    }

    /**
     * Filtrado de tienda
     * @param stores
     */
    public void setFilter(ArrayList<Store> stores){
        this.stores = new ArrayList<>();
        this.stores.addAll(stores);
        notifyDataSetChanged();
    }
}
