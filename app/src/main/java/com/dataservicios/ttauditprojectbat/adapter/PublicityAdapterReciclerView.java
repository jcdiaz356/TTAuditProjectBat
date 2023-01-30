package com.dataservicios.ttauditprojectbat.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.model.Publicity;
import com.dataservicios.ttauditprojectbat.view.PollPublicityActivity;

import java.util.ArrayList;

/**
 * Created by jcdia on 3/06/2017.
 */

public class PublicityAdapterReciclerView extends RecyclerView.Adapter<PublicityAdapterReciclerView.PublicityViewHolder> {
    private ArrayList<Publicity> publicities;
    private int                         resource;
    private Activity activity;
    private int                         store_id;
    private int                         audit_id;

    public PublicityAdapterReciclerView(ArrayList<Publicity> publicities, int resource, Activity activity, int store_id, int audit_id) {
        this.publicities    = publicities;
        this.resource       = resource;
        this.activity       = activity;
        this.store_id       = store_id;
        this.audit_id       = audit_id;

    }

    @Override
    public PublicityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new PublicityViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(PublicityViewHolder holder, int position) {
        final Publicity publicity = publicities.get(position);


        holder.tvFullName.setText(publicity.getFullname());
        //holder.tvDescription.setText(publicity.getDescription());
//        Picasso.with(activity)
//                .load(publicity.getImagen())
//                .placeholder(R.drawable.loading_image)
//                .error(R.drawable.thumbs_ttaudit)
//                .into(holder.imgPhoto);
        if(publicity.getStatus() == 0){
            holder.imgStatus.setVisibility(View.INVISIBLE);
            holder.btAudit.setVisibility(View.VISIBLE);
        } else {
            holder.imgStatus.setVisibility(View.VISIBLE);
            holder.btAudit.setVisibility(View.INVISIBLE);
        }
        holder.btAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Poll poll = new Poll();
//                    poll.setPublicity_id(publicity.getId());
//                    poll.setOrder(7);
//                    PollPublicityActivity.createInstance((Activity) activity, store_id,audit_id,poll);

                Intent intent = new Intent(activity, PollPublicityActivity.class);
                intent.putExtra("store_id"              , store_id);
                intent.putExtra("audit_id"              , audit_id);
                intent.putExtra("orderPoll"             , 4);
                intent.putExtra("category_product_id"   , 0);
                intent.putExtra("publicity_id"          , publicity.getId());
                intent.putExtra("product_id"            , 0);
                activity.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return publicities.size();
    }

    public class PublicityViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFullName;
        //private TextView    tvDescription;
        private Button btAudit;
        private ImageView imgPhoto;
        private ImageView imgStatus;

        public PublicityViewHolder(View itemView) {
            super(itemView);
            tvFullName      = (TextView) itemView.findViewById(R.id.tvFullName);
           // tvDescription   = (TextView) itemView.findViewById(R.id.tvDescription);
            btAudit         = (Button)  itemView.findViewById(R.id.btAudit);
            imgPhoto        = (ImageView)  itemView.findViewById(R.id.imgPhoto);
            imgStatus       = (ImageView)  itemView.findViewById(R.id.imgStatus);
        }
    }
}
