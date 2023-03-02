package com.dataservicios.ttauditprojectbat.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Audit;
import com.dataservicios.ttauditprojectbat.model.AuditRoadStore;
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.model.Poll;
import com.dataservicios.ttauditprojectbat.model.Product;
import com.dataservicios.ttauditprojectbat.model.ProjectionSale;
import com.dataservicios.ttauditprojectbat.model.Route;
import com.dataservicios.ttauditprojectbat.model.RouteStoreTime;
import com.dataservicios.ttauditprojectbat.model.Store;
import com.dataservicios.ttauditprojectbat.repo.AuditRepo;
import com.dataservicios.ttauditprojectbat.repo.AuditRoadStoreRepo;
import com.dataservicios.ttauditprojectbat.repo.CompanyRepo;
import com.dataservicios.ttauditprojectbat.repo.MediaRepo;
import com.dataservicios.ttauditprojectbat.repo.ProductRepo;
import com.dataservicios.ttauditprojectbat.repo.RouteRepo;
import com.dataservicios.ttauditprojectbat.repo.RouteStoreTimeRepo;
import com.dataservicios.ttauditprojectbat.repo.StoreRepo;
import com.dataservicios.ttauditprojectbat.util.AuditUtil;
import com.dataservicios.ttauditprojectbat.util.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class StoreAuditActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String LOG_TAG = StoreAuditActivity.class.getSimpleName();
    private SessionManager session;
    private GoogleMap mMap;
    private Activity activity = this;
    private ProgressDialog pDialog;
    private TextView tvRouteFullName, tvStoreFullName, tvStoreId, tvAddress, tvReferencia, tvDistrict, tvType, tvOwner, tvCampaign, tvUserEmail, tvCodClient;
    private Button btSaveGeo;
    private Button btcloseRouteStore;
    private ImageButton ibEditAddress;
    private ImageButton imgAuditStore;
    private ImageButton imgShared;
    private int user_id;
    private int store_id;
    private int route_id;
    private int company_id;
    private String user_email, user_name;
    private Bitmap bmpMarker;
    private RouteRepo routeRepo;
    private ProductRepo productRepo;
    private AuditRoadStoreRepo auditRoadStoreRepo;
    private Audit audit;
    private AuditRepo auditRepo;
    private StoreRepo storeRepo;
    private CompanyRepo companyRepo;
    private MediaRepo mediaRepo;
    private Route route;
    private Store store;
    private Company company;

    private Boolean gpsStatus;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_audit);

        showToolbar(getString(R.string.title_activity_Stores_Audit), true);

        DatabaseManager.init(this);

        Bundle bundle = getIntent().getExtras();
        store_id = bundle.getInt("store_id");
        route_id = bundle.getInt("route_id");

        session = new SessionManager(activity);
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER));
        user_email = String.valueOf(userSesion.get(SessionManager.KEY_EMAIL));
        user_name = String.valueOf(userSesion.get(SessionManager.KEY_NAME));


        routeRepo = new RouteRepo(activity);
        productRepo = new ProductRepo(activity);
        storeRepo = new StoreRepo(activity);
        companyRepo = new CompanyRepo(activity);
        auditRoadStoreRepo = new AuditRoadStoreRepo(activity);
        auditRepo = new AuditRepo(activity);
        mediaRepo = new MediaRepo(activity);

        ArrayList<Company> companies = (ArrayList<Company>) companyRepo.findAll();
        for (Company c : companies) {
            company_id = c.getId();
            company = c;
        }

        tvRouteFullName = (TextView) findViewById(R.id.tvRouteFullName);
        tvCampaign = (TextView) findViewById(R.id.tvCampaign);
        tvUserEmail = (TextView) findViewById(R.id.tvUserEmail);
        tvStoreFullName = (TextView) findViewById(R.id.tvStoreFullName);
        tvStoreId = (TextView) findViewById(R.id.tvStoreId);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvReferencia = (TextView) findViewById(R.id.tvReferencia);
        tvDistrict = (TextView) findViewById(R.id.tvDistrict);
        tvType = (TextView) findViewById(R.id.tvType);
        tvOwner = (TextView) findViewById(R.id.tvOwner);
        tvCodClient = (TextView) findViewById(R.id.tvCodClient);
        btSaveGeo = (Button) findViewById(R.id.btSaveGeo);
        btcloseRouteStore = (Button) findViewById(R.id.btcloseRouteStore);
        ibEditAddress = (ImageButton) findViewById(R.id.ibEditAddress);
        imgAuditStore = (ImageButton) findViewById(R.id.imgAuditStore);
        imgShared = (ImageButton) findViewById(R.id.imgShared);

        store = (Store) storeRepo.findByIdAndRouteId(store_id, route_id);
        //store = (Store) storeRepo.findById(store_id);
        route = (Route) routeRepo.findById(store.getRoute_id());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        tvCampaign.setText(Html.fromHtml("<strong>" + getString(R.string.text_campaign) + ": </strong>") + company.getFullname() + " (ID:" + company.getId() + ")");
        tvUserEmail.setText(Html.fromHtml("<strong>" + getString(R.string.text_user_name) + ": </strong>") + user_email + " (ID:" + user_id + ")");
        //tvRouteFullName.setText(Html.fromHtml("<strong>" + getString(R.string.text_route_name )+ ": </strong>") + route.getFullname() +" (ID:"+ route.getId() +")");

        tvStoreFullName.setText(String.valueOf(store.getFullname()));
        tvStoreId.setText(String.valueOf(store.getId()));
        tvAddress.setText(String.valueOf(store.getAddress()));
        tvReferencia.setText(String.valueOf(store.getUrbanization()));
        tvDistrict.setText(String.valueOf(store.getDistrict()));
        tvType.setText(String.valueOf(store.getType()));
        // tvOwner.setText(String.valueOf(store.getComment()));
        tvOwner.setText(String.valueOf(store.getComment()));
        tvCodClient.setText(String.valueOf(store.getCodCliente()));


        ibEditAddress.setVisibility(View.VISIBLE);
        ibEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(activity, "No disponible",Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putInt("store_id", store.getId());
                bundle.putInt("route_id", route_id);
                bundle.putInt("action", 1); // Action 0= edit Adress
                Intent intent = new Intent(activity, EditStoreActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        gpsStatus = displayGpsStatus();
        if (!gpsStatus) {
            alertbox();
        } else {
            getLocation(false);
        }

        imgAuditStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.message_sync_audits_store);
                builder.setMessage(R.string.message_sync_audits_store_information);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new syncAuditRoadStore().execute();
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

        imgShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "ID Store: " + store.getId() + "\nTienda: " + store.getFullname();
                String shareSub = "Ruta";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_TITLE, shareBody);
                activity.startActivity(Intent.createChooser(sharingIntent, "Share using"));

            }
        });

        btSaveGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsStatus = displayGpsStatus();
                if (!gpsStatus) {
                    alertbox();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.message_update_gps);
                builder.setMessage(R.string.message_update_gps_information);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getLocation(true);
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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//********************************************************** PARA VERIFICAR ***************************************************
//********************************************************** PARA VERIFICAR ***************************************************
//********************************************************** PARA VERIFICAR ***************************************************
//********************************************************** PARA VERIFICAR ***************************************************
//********************************************************** PARA VERIFICAR ***************************************************
        LinearLayout lyContentButtons = (LinearLayout) findViewById(R.id.lyContentButtons);
//        ArrayList<AuditRoadStore> auditRoadStores = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreId(store_id);
        ArrayList<AuditRoadStore> auditRoadStores = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreAndRoadId(store_id, route_id);
        //***************************************** para soportar multiple visitas ***********************************************
        //***************************************** para soportar multiple visitas ***********************************************
        //***************************************** para soportar multiple visitas ***********************************************
        //***************************************** para soportar multiple visitas ***********************************************
        //***************************************** para soportar multiple visitas ***********************************************

//        ArrayList<Audit> audits = (ArrayList<Audit>) auditRepo.findAll();

        Button[] buttonArray = new Button[auditRoadStores.size()];
//        Button[] buttonArray = new Button[audits.size()];
        int i = 0;
        for (AuditRoadStore ar : auditRoadStores) {
            //tvPrueba.append(String.valueOf(ar.getAudit_id()) +   String.valueOf(ar.getList().getFullname()) +"\n");
            buttonArray[i] = new Button(this, null, R.attr.button_style_r);
//            buttonArray[i].setText(ar.getFullname().toString());
            buttonArray[i].setText(ar.getList().getFullname().toString());
//            buttonArray[i].setTag(String.valueOf(ar.getId()));
            buttonArray[i].setTag(String.valueOf(ar.getList().getId()));
            if (ar.getAuditStatus() == 1) {
                buttonArray[i].setEnabled(false);
            }
            lyContentButtons.addView(buttonArray[i]);

//            Restrición , esta auditoría solo se mostrara para bodegas
            // --------------------NOTA------------------------------------------------
            // Se ocult{o temporalmente en la campaña 451, en la siguiente campaña sacarlo
            //--------------------------------------------------------------------------
//            if (ar.getAudit_id()== 77 && store.getType().equals("Puesto de Mercado") ){
            if (ar.getAudit_id() == 115) {
                buttonArray[i].setVisibility(View.GONE);
            }

            buttonArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(activity,v.getTag().toString(), Toast.LENGTH_SHORT).show();


//       **********  FORZANDO LA GEOLOCALIZACI[ON *************************
//                    if(company.getApp_id().equals("mantenimiento")){
//                        if(store.getUpdate_geo() == 0) {
//                            Toast.makeText(activity,R.string.message_update_geo_store,Toast.LENGTH_LONG).show();
//                            return;
//                        };
//                    }
//                    ************************


                    int audit_id = Integer.valueOf(v.getTag().toString());
//                    if(audit_id == 93) {
//                        Poll poll = new Poll();
//                        poll.setOrder(1);
////                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//                        openActivity(activity, store_id,audit_id,poll);
//                    }


                    if (company.getApp_id().equals("promotoria")) {
//                        if (audit_id == 62) {
//                            Poll poll = new Poll();
//                            poll.setOrder(1);
////                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//
//
//                            openActivity(activity, store_id, audit_id, poll, route_id);
//                        }
//                        if (audit_id == 115) {
//                            Poll poll = new Poll();
//                            poll.setOrder(16);
////                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//                            openActivity(activity, store_id, audit_id, poll, route_id);
//                        }
//                        if (audit_id == 100) {
//                            Poll poll = new Poll();
//                            poll.setOrder(3);
////                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//                            openActivity(activity, store_id, audit_id, poll, route_id);
//                        }




//***************  INFORMACIÓN DE AUDITORIA (CIGARRILLOS) ******************
                        if (audit_id == 102) {
                            Poll poll = new Poll();
                            poll.setOrder(1);
//                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
                            openActivity(activity, store_id, audit_id, poll, route_id);
                        }

//***************  INFORMACIÓN DE AUDITORIA (VUSE) ******************
                        if (audit_id == 132) {
                            Poll poll = new Poll();
                            poll.setOrder(50);
//                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
                            openActivity(activity, store_id, audit_id, poll, route_id);
                        }
//*************** SOLO PARA AUDITORIA QUIEBRE DE STOCK******************
                        // Nota: Realiza una petici{on al stock de productos
                        if (audit_id == 101) {
                            new syncProjectionSales(audit_id).execute();
                        }

//*************** INCIDENCIAS******************

//                        if (audit_id == 103) {
//                            Poll poll = new Poll();
//                            poll.setOrder(10);
////                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//                            openActivity(activity, store_id, audit_id, poll, route_id);
//                        }

                        if (audit_id == 103) {

                            Intent intent = new Intent(activity, ProductCompetityActivity.class);
                            intent.putExtra("store_id", store_id);
                            intent.putExtra("route_id", route_id);
                            intent.putExtra("audit_id", audit_id);
                            intent.putExtra("orderPoll", 0);
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                            intent.putExtra("category_product_id", 224);
                            intent.putExtra("publicity_id", 0);
                            intent.putExtra("product_id", 0);
                            startActivity(intent);

                        }

//*************** ACTIVIDADES DE COMPETECIAS PMI ******************
                        if (audit_id == 104) {
                            Intent intent = new Intent(activity, ProductCompetityActivity.class);
                            intent.putExtra("store_id", store_id);
                            intent.putExtra("route_id", route_id);
                            intent.putExtra("audit_id", audit_id);
                            intent.putExtra("orderPoll", 0);
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                            intent.putExtra("category_product_id", 195);
                            intent.putExtra("publicity_id", 0);
                            intent.putExtra("product_id", 0);
                            startActivity(intent);
                        }

//*************** ACTIVIDADES DE COMPETECIAS CIGARRILLOS ******************
                        if (audit_id == 133) {
                            Intent intent = new Intent(activity, ProductCompetityActivity.class);
                            intent.putExtra("store_id", store_id);
                            intent.putExtra("route_id", route_id);
                            intent.putExtra("audit_id", audit_id);
                            intent.putExtra("orderPoll", 0);
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                            intent.putExtra("category_product_id", 225);
                            intent.putExtra("publicity_id", 0);
                            intent.putExtra("product_id", 0);
                            startActivity(intent);
                        }
//*************** PARTNERSHIPS ******************
                        if (audit_id == 130) {

                            Intent intent = new Intent(activity, ProductCompetityActivity.class);
                            intent.putExtra("store_id", store_id);
                            intent.putExtra("route_id", route_id);
                            intent.putExtra("audit_id", audit_id);
                            intent.putExtra("orderPoll", 0);
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                            intent.putExtra("category_product_id", 207);
                            intent.putExtra("publicity_id", 0);
                            intent.putExtra("product_id", 0);
                            startActivity(intent);

                        }

  //******************* OTROS INSIGHTS ******************
                        if (audit_id == 134) {

                            Intent intent = new Intent(activity, ProductCompetityActivity.class);
                            intent.putExtra("store_id", store_id);
                            intent.putExtra("route_id", route_id);
                            intent.putExtra("audit_id", audit_id);
                            intent.putExtra("orderPoll", 0);
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                            intent.putExtra("category_product_id", 226);
                            intent.putExtra("publicity_id", 0);
                            intent.putExtra("product_id", 0);
                            startActivity(intent);

                        }


//                        if (audit_id == 129) {
//                            Poll poll = new Poll();
//                            poll.setOrder(40);
////                            poll.setOrder(51);
////                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//                            openActivity(activity, store_id, audit_id, poll, route_id);
//                        }



                        //*************** comentarios adicionales ******************
//                        if (audit_id == 131) {
//                            Poll poll = new Poll();
//                            poll.setOrder(60);
////                            poll.setOrder(51);
////                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//                            openActivity(activity, store_id, audit_id, poll, route_id);
//                        }

                    }

                    if (company.getApp_id().equals("bodega")) {

//                        ************ INFORMACI{ON GENERAL ******************************
                        if (audit_id == 116) {
                            Poll poll = new Poll();
                            poll.setOrder(1);
//                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
                            openActivity(activity, store_id, audit_id, poll, route_id);
                        }

//                        if (audit_id == 100) {
//                            Poll poll = new Poll();
//                            poll.setOrder(3);
////                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//                            openActivity(activity, store_id, audit_id, poll, route_id);
//                        }
//                        ******************* QUIEBRE DE STOCK ****************
                        if (audit_id == 101) {

                            Intent intent = new Intent(activity, ProductCompetityActivity.class);
                            intent.putExtra("store_id", store_id);
                            intent.putExtra("route_id", route_id);
                            intent.putExtra("audit_id", audit_id);
                            intent.putExtra("orderPoll", 0);
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                            intent.putExtra("category_product_id", 194);
                            intent.putExtra("publicity_id", 0);
                            intent.putExtra("product_id", 0);
                            startActivity(intent);

                        }

//                        if (audit_id == 117) {
//
//                            Intent intent = new Intent(activity, ProductCompetityActivity.class);
//                            intent.putExtra("store_id", store_id);
//                            intent.putExtra("route_id", route_id);
//                            intent.putExtra("audit_id", audit_id);
//                            intent.putExtra("orderPoll", 0);
////                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
//                            intent.putExtra("category_product_id", 200);
//                            intent.putExtra("publicity_id", 0);
//                            intent.putExtra("product_id", 0);
//                            startActivity(intent);
//
//                        }

// ********************** INFORMACI{ON DE AUDITORIA *****************************
                        if (audit_id == 102) {
                            Poll poll = new Poll();
                            //poll.setOrder(7);
                            poll.setOrder(110);
//                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
                            openActivity(activity, store_id, audit_id, poll, route_id);
                        }

// ********************** INCIDENCIAS *****************************
                        if (audit_id == 103) {
                            Intent intent = new Intent(activity, ProductCompetityActivity.class);
                            intent.putExtra("store_id", store_id);
                            intent.putExtra("route_id", route_id);
                            intent.putExtra("audit_id", audit_id);
                            intent.putExtra("orderPoll", 0);
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                            intent.putExtra("category_product_id", 224);
                            intent.putExtra("publicity_id", 0);
                            intent.putExtra("product_id", 0);
                            startActivity(intent);
                        }
//                        if (audit_id == 103) {
//                            Poll poll = new Poll();
//                            poll.setOrder(10);
////                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//                            openActivity(activity, store_id, audit_id, poll, route_id);
//                        }


//************************* ACTIVIDADES DE COMPETECIAS PMI (MALBORO Y OTROS) ********************
                        if (audit_id == 104) {
                            Intent intent = new Intent(activity, ProductCompetityActivity.class);
                            intent.putExtra("store_id", store_id);
                            intent.putExtra("route_id", route_id);
                            intent.putExtra("audit_id", audit_id);
                            intent.putExtra("orderPoll", 0);
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                            intent.putExtra("category_product_id", 195);
                            intent.putExtra("publicity_id", 0);
                            intent.putExtra("product_id", 0);
                            startActivity(intent);
                        }
 //************************* ACTIVIDADES DE LA COMPETENCIA CIGARRILLOS ELÉCTRÓNICOS (STLTH, RELX, INDY, OTROS) ********************
                        if (audit_id == 135) {
                            Intent intent = new Intent(activity, ProductCompetityActivity.class);
                            intent.putExtra("store_id", store_id);
                            intent.putExtra("route_id", route_id);
                            intent.putExtra("audit_id", audit_id);
                            intent.putExtra("orderPoll", 0);
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                            intent.putExtra("category_product_id", 227);
                            intent.putExtra("publicity_id", 0);
                            intent.putExtra("product_id", 0);
                            startActivity(intent);
                        }



                    }

                    if (company.getApp_id().equals("mantenimiento")) {
                        if (audit_id == 116) {
                            Poll poll = new Poll();
                            poll.setOrder(1);
//                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
                            openActivity(activity, store_id, audit_id, poll, route_id);
                        }


                        if (audit_id == 119) {
                            Poll poll = new Poll();
                            poll.setOrder(30);
//                          PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
                            openActivity(activity, store_id, audit_id, poll, route_id);
                        }

                        if (audit_id == 120) {
                            Poll poll = new Poll();
                            poll.setOrder(32);
//                          PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
                            openActivity(activity, store_id, audit_id, poll, route_id);
                        }

                        if (audit_id == 118) {
                            Poll poll = new Poll();
                            poll.setOrder(3);
//                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
                            openActivity(activity, store_id, audit_id, poll, route_id);
                        }

                        if (audit_id == 108) {
                            Poll poll = new Poll();
                            poll.setOrder(5);
//                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
                            openActivity(activity, store_id, audit_id, poll, route_id);
                        }

                        if (audit_id == 109) {
                            Poll poll = new Poll();
                            poll.setOrder(11);
//                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
                            openActivity(activity, store_id, audit_id, poll, route_id);
                        }

                        if (audit_id == 110) {
                            Poll poll = new Poll();
                            poll.setOrder(12);
//                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);
                            openActivity(activity, store_id, audit_id, poll, route_id);
                        }
                    }

                }
            });
            i++;
        }


        btcloseRouteStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.message_save);
                builder.setMessage(R.string.message_save_information);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new saveCloseRouteStore().execute();
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

        //-------------------------------------------------------------
        // Verificando si hay fotos pendientes por subir
        // segun la configuración dentro del company.getMax_photo_notification
        //--------------------------------------------------------------------
        if (company.getMax_photo_notification() > 0) {
            if (mediaRepo.countReg() >= company.getMax_photo_notification())
                Toasty.error(activity, String.valueOf(mediaRepo.countReg()) + " " + activity.getString(R.string.message_medias_pending_upload), Toast.LENGTH_LONG).show();
        }

        //------------------END Verficicacón------------------------------
    }

    private void openActivity(Activity activity, int store_id, int audit_id, Poll poll, int route_id) {
        Intent intent = new Intent(activity, PollActivity.class);
        intent.putExtra("store_id", store_id);
        intent.putExtra("audit_id", audit_id);
        intent.putExtra("route_id", route_id);
        intent.putExtra("orderPoll", poll.getOrder());
        intent.putExtra("category_product_id", poll.getCategory_product_id());
        intent.putExtra("publicity_id", poll.getPublicity_id());
        intent.putExtra("product_id", poll.getProduct_id());
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        getPositionStoreMap();

        //new loadMarkerPointMap().execute();
    }

    public void showToolbar(String title, boolean upButton) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);


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


    private void getPositionStoreMap() {
        mMap.clear();
        LatLng latLong = new LatLng(store.getLatitude(), store.getLongitude());
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLong);
        markerOption.title(store.getFullname());

        //if (bmpMarker == null) markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_point)) ; else markerOption.icon(BitmapDescriptorFactory.fromBitmap(bmpMarker));
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_point));
        Marker marker = mMap.addMarker(markerOption);
        marker.showInfoWindow();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(15).build();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // pDialog.dismiss();
    }

    private class saveLatLongStore extends AsyncTask<Void, String, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            boolean result;
            result = AuditUtil.saveLatLongStore(store_id, mLocation.getLatitude(), mLocation.getLongitude());
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getString(R.string.text_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                store.setLatitude(mLocation.getLatitude());
                store.setLongitude(mLocation.getLongitude());

                // Activando que ya fue georreferenciado
                store.setUpdate_geo(1);
                storeRepo.updateGeo(store, route_id);

                storeRepo.update(store);
                getPositionStoreMap();
            } else {
                Toast.makeText(activity, R.string.message_gps_error_save_location, Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }


    }

    private class saveCloseRouteStore extends AsyncTask<Void, Integer, Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando ProductDetail...");
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
            String time_close = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
            RouteStoreTimeRepo routeStoreTimeRepo = new RouteStoreTimeRepo(activity);
            RouteStoreTime routeStoreTime;
            routeStoreTime = (RouteStoreTime) routeStoreTimeRepo.findFirstReg();
            if (mLocation != null) {
                routeStoreTime.setLat_close(mLocation.getLatitude());
                routeStoreTime.setLon_close(mLocation.getLongitude());
            } else {
                routeStoreTime.setLat_close(0);
                routeStoreTime.setLon_close(0);
            }

            routeStoreTime.setTime_close(time_close);

            if (!AuditUtil.closeRouteStore(routeStoreTime)) return false;

            return true;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result) {
                Store store;
//                store = (Store) storeRepo.findById(store_id);
                store = (Store) storeRepo.findByIdAndRouteId(store_id, route_id);
                store.setStatus(1);
                storeRepo.updateStatusForStorAndRouteId(store.getId(), store.getRoute_id(), 1);

                ArrayList<Store> stores = (ArrayList<Store>) storeRepo.findByRouteId(route.getId());
                int counterStatus = 0;
                for (Store s : stores) {
                    if (s.getStatus() == 1) {
                        counterStatus++;
                    }
                }
                route.setAudit(counterStatus);
                routeRepo.update(route);

                finish();
            } else {
                Toast.makeText(activity, R.string.message_no_save_data, Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
        }
    }

    private class syncAuditRoadStore extends AsyncTask<Void, String, ArrayList<AuditRoadStore>> {


        @Override
        protected ArrayList<AuditRoadStore> doInBackground(Void... params) {

            ArrayList<AuditRoadStore> auditRoadStores;

            auditRoadStores = AuditUtil.getAuditRoadStore(company_id, route.getId(), store_id);
            return auditRoadStores;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getString(R.string.text_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<AuditRoadStore> auditRoadStores) {
            super.onPostExecute(auditRoadStores);
            if (auditRoadStores.size() > 0) {
                AuditRepo auditRepo = new AuditRepo(activity);
                for (AuditRoadStore m : auditRoadStores) {
                    Audit audit = (Audit) auditRepo.findById(m.getAudit_id());
                    m.setList(audit);
                    auditRoadStoreRepo.update(m);
                }
                ArrayList<AuditRoadStore> lista = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreId(store_id);
                onRestart();
            } else {
                Toast.makeText(activity, R.string.mesaage_no_get_data, Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }


    }


    private class syncProjectionSales extends AsyncTask<Void, String, ArrayList<ProjectionSale>> {
        private int audit_id;

        public syncProjectionSales(int audit_id) {
            this.audit_id = audit_id;
        }

        @Override
        protected ArrayList<ProjectionSale> doInBackground(Void... params) {

            ArrayList<ProjectionSale> projectionSales;

            projectionSales = AuditUtil.getProjectionSales(company_id, store_id);
            return projectionSales;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getString(R.string.text_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<ProjectionSale> projectionSales) {
            super.onPostExecute(projectionSales);

            ArrayList<Product> products = new ArrayList<Product>();
            products = (ArrayList<Product>) productRepo.findAll();
            for (Product m : products) {

                Product product = (Product) productRepo.findById(m.getId());
                product.setStock(0);
                productRepo.update(product);

            }

            if (projectionSales.size() > 0) {
                //  ProjectionSale auditRepo = new AuditRepo(activity);


                for (ProjectionSale m : projectionSales) {
//                    Audit audit = (Audit) auditRepo.findById(m.getAudit_id());
//                    m.setList(audit);
//                    auditRoadStoreRepo.update(m);
                    Product product = (Product) productRepo.findById(m.getProduct_id());

                    if (product != null) {
                        product.setStock(m.getStock_min());
                        productRepo.update(product);
                    }


                }


//                ArrayList<AuditRoadStore> lista = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreId(store_id);
//                onRestart();
            } else {
                Toast.makeText(activity, R.string.mesaage_no_get_data, Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(activity, ProductCompetityActivity.class);
            intent.putExtra("store_id", store_id);
            intent.putExtra("route_id", route_id);
            intent.putExtra("audit_id", audit_id);
            intent.putExtra("orderPoll", 0);
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
            intent.putExtra("category_product_id", 194);
            intent.putExtra("publicity_id", 0);
            intent.putExtra("product_id", 0);
            startActivity(intent);


            pDialog.dismiss();

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }


    }


    /**
     * Verifica si el GPS esta Habilitado
     * @return
     */
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        Boolean status = false;
        try {
            int gpsSignal = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE);
            if (gpsSignal == 0) status = false;
            else status = true;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * Mensaje de Alerta para el GPS
     */
    protected void alertbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.gpsMessage))
                .setCancelable(false)
                .setTitle(getString(R.string.gpsTitle))
                .setPositiveButton(getString(R.string.gpsOn),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the dialog box
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Obtener la geolocalización
     */
    private void getLocation(final Boolean actionSaveRemote) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    mLocation = location;
                    if (actionSaveRemote) {
                        new saveLatLongStore().execute();
                    }

                } else {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Toast.makeText(activity, R.string.message_gps_error_get_location, Toast.LENGTH_SHORT).show();
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            }
        });

//        }
    }

}
