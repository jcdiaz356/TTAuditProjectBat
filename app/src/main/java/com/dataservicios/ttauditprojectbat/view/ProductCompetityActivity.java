package com.dataservicios.ttauditprojectbat.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.adapter.ProductAdapterRecyclerView;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Audit;
import com.dataservicios.ttauditprojectbat.model.AuditRoadStore;
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.model.Poll;
import com.dataservicios.ttauditprojectbat.model.Product;
import com.dataservicios.ttauditprojectbat.model.Route;
import com.dataservicios.ttauditprojectbat.model.Store;
import com.dataservicios.ttauditprojectbat.repo.AuditRepo;
import com.dataservicios.ttauditprojectbat.repo.AuditRoadStoreRepo;
import com.dataservicios.ttauditprojectbat.repo.CompanyRepo;
import com.dataservicios.ttauditprojectbat.repo.ProductRepo;
import com.dataservicios.ttauditprojectbat.repo.RouteRepo;
import com.dataservicios.ttauditprojectbat.repo.StoreRepo;
import com.dataservicios.ttauditprojectbat.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductCompetityActivity extends AppCompatActivity {

    private static final String LOG_TAG = ProductCompetityActivity.class.getSimpleName();
    private SessionManager session;
    private Activity activity =  this;
    private ProgressDialog pDialog;
    private int                                     user_id;
    private int                                     store_id;
    private int                                     route_id;
    private int                                     audit_id;
    private int                                     category_product_id;
    private TextView tvTotal;
    private Button btSave;
    private ProductRepo productRepo;
    private StoreRepo storeRepo ;
    private RouteRepo routeRepo ;
    private CompanyRepo companyRepo ;
    private AuditRepo auditRepo ;
    private AuditRoadStoreRepo auditRoadStoreRepo ;

   // private CategoryProductRepo categoryProductRepo;
    private ProductAdapterRecyclerView productAdapterRecyclerView;
    private RecyclerView productRecyclerView;
    private Audit audit ;
    private Product product;
    private Company company ;
    private Route route ;
    private Store store ;
    private ArrayList<Product> products;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_competity);
        tvTotal                 = (TextView) findViewById(R.id.tvTotal);
        btSave                  = (Button) findViewById(R.id.btSave);
        DatabaseManager.init(this);

        storeRepo           = new StoreRepo(activity);
        productRepo         = new ProductRepo(activity);
        auditRepo           = new AuditRepo(activity);
        companyRepo         = new CompanyRepo(activity);
        routeRepo           = new RouteRepo(activity);
        auditRoadStoreRepo  = new AuditRoadStoreRepo(activity);

       // categoryProductRepo = new CategoryProductRepo(activity);

        Bundle bundle = getIntent().getExtras();
        store_id            = bundle.getInt("store_id");
        route_id            = bundle.getInt("route_id");
        audit_id            = bundle.getInt("audit_id");
        category_product_id = bundle.getInt("category_product_id");

        company         = (Company)companyRepo.findFirstReg();
        store           = (Store) storeRepo.findByIdAndRouteId(store_id,route_id);
        route           = (Route) routeRepo.findById(route_id);
//        route           = (Route) routeRepo.findById(store.getRoute_id());
       // categoryProduct = (CategoryProduct) categoryProductRepo.findById(category_product_id);

        session = new SessionManager(activity);
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;

        audit = (Audit) auditRepo.findById(audit_id);
        showToolbar(audit.getFullname().toString(),false);

        productRecyclerView  = (RecyclerView) findViewById(R.id.product_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        productRecyclerView.setLayoutManager(linearLayoutManager);

        products = (ArrayList<Product>) productRepo.findByCategoryProductId(category_product_id);



//        products = (ArrayList<Product>) productRepo.findAll();



        productAdapterRecyclerView =  new ProductAdapterRecyclerView(products, R.layout.cardview_product, activity,store_id,audit_id,company,route_id);
        productRecyclerView.setAdapter(productAdapterRecyclerView);

        int total               = products.size();
        int productsAudits   = 0;

        for(Product p: products){
            if(p.getStatus()==1) productsAudits ++;
        }

        tvTotal.setText(String.valueOf(productsAudits) + " de " + String.valueOf(total));
        if(products.size() == 0) {
            btSave.setVisibility(View.INVISIBLE);
        }
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                for(Product p: products){
//                    if(p.getStatus()== 0) {
//                        Toast.makeText(activity,R.string.message_audit_all_product, Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.message_save);
                builder.setMessage(R.string.message_save_information);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // new savePoll().execute();
//                        Poll poll = new Poll();
//                        poll.setOrder(9);
//                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);

                        ProductRepo productRepo = new ProductRepo(activity);
                        ArrayList<Product> products = (ArrayList<Product>) productRepo.findAll();
                        for (Product p : products) {
                            p.setStatus(0);
                            productRepo.update(p);
                        }
                        new savePoll().execute();
                        //finish();
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                builder.setCancelable(false);
            }
        });


    }

    private ArrayList<Product> filter(ArrayList<Product> models, String query) {

        query = query.toLowerCase();
        final ArrayList<Product> filteredModelList = new ArrayList<>();
        for (Product s : models) {
            final String fullName = s.getFullname().toLowerCase().trim();
            if (fullName.contains(query) ) {
                filteredModelList.add(s);
            }

        }
        return filteredModelList;
    }

    public void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //storeAdapterRecyclerView.getFilter().filter(newText.toString());
                final ArrayList<Product> filteredMStoreList = filter(products, newText);
                //adapter.setFilter(filteredModelList);
                productAdapterRecyclerView.setFilter(filteredMStoreList);
                return false;
            }
        });
        return true;
    }

    class savePoll extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando Product...");
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getString(R.string.text_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();

        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

          // if (!AuditUtil.closeAuditStore(audit_id, store_id, company.getId(), route.getId())) return false;
           return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted
            if (result){




                AuditRoadStore auditRoadStore = (AuditRoadStore) auditRoadStoreRepo.findByStoreIdAndAuditIdAndRouteId(store_id,audit_id,route_id);
                auditRoadStore.setAuditStatus(1);
                auditRoadStoreRepo.update(auditRoadStore);

                if(company.getApp_id().equals("mantenimiento")) {
                    finish();

                }if(company.getApp_id().equals("promotoria")) {
                    finish();

                }

                else{

                    Poll poll = new Poll();
                    poll.setOrder(100);

                    Intent intent = new Intent(activity, PollActivity.class);
                    intent.putExtra("store_id"              , store_id);
                    intent.putExtra("route_id"              , route_id);
                    intent.putExtra("audit_id"              , audit_id);
                    intent.putExtra("orderPoll"             , poll.getOrder());
                    intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                    intent.putExtra("publicity_id"          , poll.getPublicity_id());
                    intent.putExtra("product_id"            , poll.getProduct_id());
                    startActivity(intent);
                    finish();
                }






            } else {
                Toast.makeText(activity , R.string.message_no_save_data , Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {

//        if(products.size() == 0 ) {
//            super.onBackPressed ();
//        } else {
//            alertDialogBasico(getString(R.string.message_save_audit_products));
//        }

        //super.onBackPressed ();

    }


    private void alertDialogBasico(String message) {

        // 1. Instancia de AlertDialog.Builder con este constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 2. Encadenar varios métodos setter para ajustar las características del diálogo
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        builder.show();

    }
}