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
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.model.Poll;
import com.dataservicios.ttauditprojectbat.model.Product;
import com.dataservicios.ttauditprojectbat.view.PollProductActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jcdia on 7/06/2017.
 */

public class ProductAdapterRecyclerView extends RecyclerView.Adapter<ProductAdapterRecyclerView.ProductViewHolder> {
    private ArrayList<Product> products;
    private int                         resource;
    private Activity                    activity;
    private int                         store_id;
    private int                         route_id;
    private int                         audit_id;
    private Company                     company;

    public ProductAdapterRecyclerView(ArrayList<Product> products, int resource, Activity activity, int store_id, int audit_id, Company company,int route_id) {
        this.products = products;
        this.resource       = resource;
        this.activity       = activity;
        this.store_id       = store_id;
        this.route_id       = route_id;
        this.audit_id       = audit_id;
        this.company        = company;

    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ProductViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        final Product product = products.get(position);

        holder.tvFullName.setText(product.getFullname());
        holder.tvComposicion.setText(String.valueOf(product.getComposicion().toString()));
        if(product.getStock() != null)
            holder.tvUnidad.setText(activity.getString(R.string.text_stock) + ": " + product.getStock().toString());

        Picasso.get()
                .load(product.getImagen())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.thumbs_ttaudit)
                .into(holder.imgPhoto);
//        Picasso.with(activity)
//                .load(product.getImagen())
//                .placeholder(R.drawable.loading_image)
//                .error(R.drawable.thumbs_ttaudit)
//                .into(holder.imgPhoto);
        if(product.getStatus() == 0){
            holder.imgStatus.setVisibility(View.INVISIBLE);
            holder.btAudit.setVisibility(View.VISIBLE);
        } else {
            holder.imgStatus.setVisibility(View.VISIBLE);
            holder.btAudit.setVisibility(View.INVISIBLE);
        }
        holder.btAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Poll poll = new Poll();
                if(company.getApp_id().equals("promotoria")) {
//                    if(audit_id == 100){
//                        poll.setOrder(4);
//                    } else
                    if (audit_id == 101){
                        poll.setOrder(15);
                    } else if (audit_id == 102){
                        poll.setOrder(9);
                    } else if (audit_id == 103){
                        poll.setOrder(10);
                    } else if (audit_id == 104){
                        poll.setOrder(14);
                    }  else if (audit_id == 133){
                        poll.setOrder(80);
                    } else if (audit_id == 130){
                        poll.setOrder(70);
                    } else if (audit_id == 134){
                        poll.setOrder(90);
                    }
                }
                if(company.getApp_id().equals("bodega")) {
                    if(audit_id == 116){
                        poll.setOrder(4);
                    } else if (audit_id == 101){
                        poll.setOrder(15);
                    } else if (audit_id == 104){
                        poll.setOrder(14);
                    } else if (audit_id == 102){
                        poll.setOrder(8);
                    }else if (audit_id == 117){
                        poll.setOrder(40);
                    }
                }

                if(company.getApp_id().equals("mantenimiento")) {
                    if(audit_id == 118){
                        poll.setOrder(4);
                    }
                }


                poll.setProduct_id(product.getId());
                poll.setCategory_product_id(product.getCategory_product_id());
                //PollProductActivity.createInstance((Activity) activity, store_id,audit_id,poll);

                Intent intent = new Intent(activity, PollProductActivity.class);
                intent.putExtra("store_id"              , store_id);
                intent.putExtra("route_id"              , route_id);
                intent.putExtra("audit_id"              , audit_id);
                intent.putExtra("orderPoll"             , poll.getOrder());
                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                intent.putExtra("publicity_id"          , poll.getPublicity_id());
                intent.putExtra("product_id"            , poll.getProduct_id());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFullName;
        private TextView tvUnidad;
        private TextView tvComposicion;
        private Button btAudit;
        private ImageView imgPhoto;
        private ImageView imgStatus;

        public ProductViewHolder(View itemView) {
            super(itemView);
            tvFullName      = (TextView) itemView.findViewById(R.id.tvFullName);
            tvComposicion   = (TextView) itemView.findViewById(R.id.tvComposicion);
            tvUnidad        = (TextView) itemView.findViewById(R.id.tvUnidad);
            btAudit         = (Button)  itemView.findViewById(R.id.btAudit);
            imgPhoto        = (ImageView)  itemView.findViewById(R.id.imgPhoto);
            imgStatus       = (ImageView)  itemView.findViewById(R.id.imgStatus);
        }
    }

    public void setFilter(ArrayList<Product> products){
        this.products = new ArrayList<>();
        this.products.addAll(products);
        notifyDataSetChanged();
    }
}
