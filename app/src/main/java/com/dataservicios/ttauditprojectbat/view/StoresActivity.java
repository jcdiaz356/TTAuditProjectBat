package com.dataservicios.ttauditprojectbat.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.adapter.StoreAdapterReciclerView;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.model.Route;
import com.dataservicios.ttauditprojectbat.model.Store;
import com.dataservicios.ttauditprojectbat.repo.CompanyRepo;
import com.dataservicios.ttauditprojectbat.repo.MediaRepo;
import com.dataservicios.ttauditprojectbat.repo.RouteRepo;
import com.dataservicios.ttauditprojectbat.repo.StoreRepo;
import com.dataservicios.ttauditprojectbat.util.SessionManager;
import com.google.android.material.internal.NavigationMenu;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class StoresActivity extends AppCompatActivity {
    private static final String LOG_TAG = StoresActivity.class.getSimpleName();

    private SessionManager              session;
    private Activity                    activity =  this;
    private TextView                    tvFullName;
    private TextView                    tvId;
    private TextView                    tvTotalStores;
    private TextView                    tvAudits;
    private ProgressDialog              pDialog;
    private int                         user_id;
    private int                         route_id;
    private RouteRepo                   routeRepo ;
    private StoreRepo                   storeRepo ;
    private MediaRepo                   mediaRepo;
    private CompanyRepo                 companyRepo ;
    private StoreAdapterReciclerView    storeAdapterRecyclerView;
    private RecyclerView                storeRecycler;
    private Route                       route ;
    private ArrayList<Store>            stores;
    private Company                     company ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

        showToolbar(getString(R.string.title_activity_Stores),true);

        DatabaseManager.init(this);

        Bundle bundle = getIntent().getExtras();
        route_id = bundle.getInt("route_id");

        session = new SessionManager(activity);
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;

        tvFullName = (TextView) findViewById(R.id.tvFullName);
        tvId = (TextView) findViewById(R.id.tvId);
        tvTotalStores = (TextView) findViewById(R.id.tvTotalStores);
        tvAudits = (TextView) findViewById(R.id.tvAudits);


        routeRepo           = new RouteRepo(activity);
        companyRepo         = new CompanyRepo(activity);
        mediaRepo           = new MediaRepo(activity);

        ArrayList<Company> companies = (ArrayList<Company>) companyRepo.findAll();
        for (Company c: companies){
            // company_id = c.getId();
            company = c;
        }


        route = (Route) routeRepo.findById(route_id);
        if (route != null) {
            tvFullName.setText(route.getFullname().toString());
            tvId.setText(String.valueOf(route.getId()));
            tvTotalStores.setText(String.valueOf(route.getTotal_store()));
            tvAudits.setText(String.valueOf(route.getAudit()));
        }

        storeRecycler  = (RecyclerView) findViewById(R.id.storeRecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        storeRecycler.setLayoutManager(linearLayoutManager);

       // new loadStores().execute();

        storeRepo = new StoreRepo(activity);
        stores = (ArrayList<Store>) storeRepo.findByRouteId(route_id);

        storeAdapterRecyclerView =  new StoreAdapterReciclerView(stores,route_id, R.layout.cardview_store, activity);
        storeRecycler.setAdapter(storeAdapterRecyclerView);

        FabSpeedDial fabMenuMaps = (FabSpeedDial) findViewById(R.id.fabMenuMaps);
        fabMenuMaps.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                // TODO: Do something with yout menu items, or return false if you don't want to show them
                return true;
            }
        });


        //FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fabMenuMaps.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //TODO: Start some activity
                Bundle bundle;
                Intent intent;
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_store_add:
                        bundle = new Bundle();
                        bundle.putInt("route_id", Integer.valueOf(route_id));
                        intent = new Intent(activity,NewStoreActivity.class);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                        break;
                    case R.id.action_location:

                         bundle = new Bundle();
                        bundle.putInt("route_id", Integer.valueOf(route_id));
                        intent = new Intent(activity,MapsRouteActivity.class);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                        break;
                    case R.id.action_location_all:
                        Toast.makeText(activity, menuItem.getTitle().toString() , Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(activity, menuItem.getTitle().toString() , Toast.LENGTH_SHORT).show();
                        break;


                    default:
                        break;
                }
                return false;
            }
        });

        //-------------------------------------------------------------
        // Verificando si hay fotos pendientes por subir
        // segun la configuración dentro del company.getMax_photo_notification
        //--------------------------------------------------------------------
        if(company.getMax_photo_notification() > 0) {
            if(mediaRepo.countReg() >= company.getMax_photo_notification())
                Toasty.error(activity, String.valueOf(mediaRepo.countReg()) + " " + activity.getString(R.string.message_medias_pending_upload) ,Toast.LENGTH_LONG).show();
        }

        //------------------END Verficicacón------------------------------

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
                final ArrayList<Store> filteredMStoreList = filter(stores, newText);
                //adapter.setFilter(filteredModelList);
                storeAdapterRecyclerView.setFilter(filteredMStoreList);
                return false;
            }
        });
        return true;
    }

    private ArrayList<Store> filter(ArrayList<Store> models, String query) {

        query = query.toLowerCase();
        final ArrayList<Store> filteredModelList = new ArrayList<>();
        for (Store s : models) {
            final String fullName = s.getFullname().toLowerCase().trim();
            final String store_id = String.valueOf(s.getId()).toLowerCase().trim();
            if (fullName.contains(query) || store_id.contains(query)) {
                filteredModelList.add(s);
            }

//            if (s.getFullname().toUpperCase().trim().contains(query.toString().toUpperCase().trim())) {
//                        //fRecords.add(s);
//                        //fRecords.add(s);
//                filteredModelList.add(s);
//                    }
        }
        return filteredModelList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                //showSnackBar("Comenzar a buscar...");
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        finish();
        startActivity(getIntent());
    }

}
