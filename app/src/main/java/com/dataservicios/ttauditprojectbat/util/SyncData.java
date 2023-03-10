package com.dataservicios.ttauditprojectbat.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Audit;
import com.dataservicios.ttauditprojectbat.model.AuditRoadStore;
import com.dataservicios.ttauditprojectbat.model.Product;
import com.dataservicios.ttauditprojectbat.model.Route;
import com.dataservicios.ttauditprojectbat.model.Store;
import com.dataservicios.ttauditprojectbat.repo.AuditRepo;
import com.dataservicios.ttauditprojectbat.repo.AuditRoadStoreRepo;
import com.dataservicios.ttauditprojectbat.repo.ProductRepo;
import com.dataservicios.ttauditprojectbat.repo.RouteRepo;
import com.dataservicios.ttauditprojectbat.repo.StoreRepo;

import java.util.ArrayList;

/**
 * Created by jcdia on 18/05/2017.
 */

/**
 * Obtiene datos del servido y actualiza la base de datos local
 */
public class SyncData extends AsyncTask<Void, String, Boolean> {
    public static final String LOG_TAG = SyncData.class.getSimpleName();
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

    private ProgressDialog pDialog;
    private Context context;
    private int                 user_id;
    private int                 company_id;
    private RouteRepo           routeRepo;
    private StoreRepo           storeRepo;
    private AuditRoadStoreRepo  auditRoadStoreRepo;
    private ProductRepo         productRepo;

    public AsyncResponse        delegate = null;

    public SyncData(Context context, int user_id, int company_id, AsyncResponse asyncResponse) {

        this.context    =   context;
        this.user_id    =   user_id;
        this.company_id =   company_id;

        DatabaseManager.init(context);
        routeRepo           = new RouteRepo(context);
        storeRepo           = new StoreRepo(context);
        auditRoadStoreRepo  = new AuditRoadStoreRepo(context);
        productRepo         = new ProductRepo(context);

        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.text_download_init));
        pDialog.setIndeterminate(true);
       // pDialog.setMax(100);
       // pDialog.setProgress(0);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);

        this.delegate = asyncResponse;

    }
    public interface AsyncResponse {
        void processFinish(Boolean output);
    }
    @Override
    protected Boolean doInBackground(Void... params) {

        ArrayList<Route> routes;
        ArrayList<Store> stores;
        ArrayList<AuditRoadStore> auditRoadStores;
        ArrayList<Product> products;




        routeRepo.deleteAll();
        storeRepo.deleteAll();
        auditRoadStoreRepo.deleteAll();
        //productRepo.deleteAll();


        routes = (ArrayList<Route>) routeRepo.findAll();
        stores = (ArrayList<Store>) storeRepo.findAll();
        auditRoadStores = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findAll();
        //products = (ArrayList<Product>) productRepo.findAll();

        routes.clear();
        stores.clear();
        auditRoadStores.clear();


        publishProgress(context.getString(R.string.text_download_routes));
        routes = AuditUtil.getListRoutes(user_id,company_id);
        for(Route r: routes){
            routeRepo.create(r);
            Log.d(LOG_TAG,r.toString());
        }
        publishProgress(context.getString(R.string.text_download_store));
        //for(Route r: routes){
            stores = AuditUtil.getListStores(user_id,company_id);
            for (Store s: stores) {
                storeRepo.create(s);
                Log.d(LOG_TAG,s.toString());
            }
        //}
        publishProgress(context.getString(R.string.text_download_audits));
        auditRoadStores = AuditUtil.getAuditRoadStores(company_id,user_id);
        AuditRepo auditRepo = new AuditRepo(context);
        for (AuditRoadStore rs: auditRoadStores){
            Audit audit = (Audit) auditRepo.findById(rs.getAudit_id());
            rs.setList(audit);
            auditRoadStoreRepo.create(rs);
        }
        routes = (ArrayList<Route>) routeRepo.findAll();
        stores = (ArrayList<Store>) storeRepo.findAll();
        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);
        pDialog.dismiss();
        delegate.processFinish(s);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        pDialog.setMessage(values[0].toString());

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
