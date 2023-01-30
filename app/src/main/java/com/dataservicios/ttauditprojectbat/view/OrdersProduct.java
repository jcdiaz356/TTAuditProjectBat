package com.dataservicios.ttauditprojectbat.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.AuditRoadStore;
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.model.Poll;
import com.dataservicios.ttauditprojectbat.model.PollDetail;
import com.dataservicios.ttauditprojectbat.model.PollOption;
import com.dataservicios.ttauditprojectbat.model.Product;
import com.dataservicios.ttauditprojectbat.model.Route;
import com.dataservicios.ttauditprojectbat.model.Store;
import com.dataservicios.ttauditprojectbat.repo.AuditRoadStoreRepo;
import com.dataservicios.ttauditprojectbat.repo.CompanyRepo;
import com.dataservicios.ttauditprojectbat.repo.PollOptionRepo;
import com.dataservicios.ttauditprojectbat.repo.PollRepo;
import com.dataservicios.ttauditprojectbat.repo.ProductRepo;
import com.dataservicios.ttauditprojectbat.repo.RouteRepo;
import com.dataservicios.ttauditprojectbat.repo.StoreRepo;
import com.dataservicios.ttauditprojectbat.util.AuditUtil;
import com.dataservicios.ttauditprojectbat.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

//import com.dataservicios.ttauditejecuciongbodegas.util.GPSTracker;

/**
 * Created by Jaime on 26/10/2016.
 */

public class OrdersProduct extends AppCompatActivity {
    private static final String LOG_TAG = OrdersProduct.class.getSimpleName();

    private SessionManager          session;
    private Activity activity =  this;
    private ProgressDialog pDialog;
    private TextView tvStoreFullName,tvStoreId,tvAddress ,tvReferencia,tvDistrict,tvAuditoria,tvPoll ;
    private EditText etComent;
    private EditText etCommentOption;
    private TextView tvCampaign      ;
    private TextView tvUserEmail     ;
    private TextView tvOwner     ;
    private Button btSaveGeo;
    private Button btSave;
    private ImageButton imgShared;
    private CheckBox[]              checkBoxArray;
    private EditText[]              editTextArray;
    private RadioButton[]           radioButtonArray;
    private RadioGroup radioGroup;
    private Switch swYesNo;
    private ImageButton btPhoto;
    private LinearLayout lyComment;
    private LinearLayout lyContentOptions;
    private LinearLayout lyOptionComment;
    private int                     user_id;
    private int                     store_id;
    private int                     audit_id;
    private int                     company_id;
    private int                     orderPoll;
    private int                     category_product_id;
    private int                     publicity_id;
    private int                     product_id;
    private RouteRepo routeRepo ;
    private AuditRoadStoreRepo auditRoadStoreRepo ;
    private StoreRepo storeRepo ;
    private CompanyRepo companyRepo ;
    private PollRepo pollRepo ;
    private ProductRepo productRepo;
    private Route route ;
    private Store store ;
    private Poll poll;

    private PollOption pollOption;
    private PollDetail              pollDetail;
    private AuditRoadStore auditRoadStore;
    private PollOptionRepo pollOptionRepo;
//    private GPSTracker gpsTracker;
    private int                     isYesNo;
    private String comment;
    private String selectedOptions;
    private String commentOptions;
    private ArrayList<Product> products;
    private Company                 company;
    private String user_email;
    private String user_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_product);

        showToolbar(getString(R.string.title_activity_Stores_Audit),true);

        DatabaseManager.init(this);



        Bundle bundle = getIntent().getExtras();
        store_id            = bundle.getInt("store_id");
        audit_id            = bundle.getInt("audit_id");
        orderPoll           = bundle.getInt("orderPoll");
        category_product_id = bundle.getInt("category_product_id");
        publicity_id        = bundle.getInt("publicity_id");
        product_id          = bundle.getInt("product_id");

        session = new SessionManager(activity);
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;
        user_email = String.valueOf(userSesion.get(SessionManager.KEY_EMAIL)) ;
        user_name = String.valueOf(userSesion.get(SessionManager.KEY_NAME)) ;

        routeRepo           = new RouteRepo(activity);
        storeRepo           = new StoreRepo(activity);
        companyRepo         = new CompanyRepo(activity);
        auditRoadStoreRepo  = new AuditRoadStoreRepo(activity);
        pollRepo            = new PollRepo(activity);
        pollOptionRepo      = new PollOptionRepo((activity));
        productRepo         = new ProductRepo(activity);

        etCommentOption     = new EditText(activity);
        etComent            = new EditText(activity);

        ArrayList<Company> companies = (ArrayList<Company>) companyRepo.findAll();
        for (Company c: companies){
            company_id = c.getId();
            company = c;
        }

        tvStoreFullName     = (TextView)    findViewById(R.id.tvStoreFullName) ;
        tvStoreId           = (TextView)    findViewById(R.id.tvStoreId) ;
        tvCampaign           = (TextView) findViewById(R.id.tvCampaign) ;
        tvUserEmail           = (TextView) findViewById(R.id.tvUserEmail) ;
        tvAddress           = (TextView)    findViewById(R.id.tvAddress) ;
        tvReferencia        = (TextView)    findViewById(R.id.tvReferencia) ;
        tvDistrict          = (TextView)    findViewById(R.id.tvDistrict) ;
        tvAuditoria         = (TextView)    findViewById(R.id.tvAuditoria) ;
        tvPoll              = (TextView)    findViewById(R.id.tvPoll) ;
        tvOwner              = (TextView)    findViewById(R.id.tvOwner) ;
        btSaveGeo           = (Button)      findViewById(R.id.btSaveGeo);
        btSave              = (Button)      findViewById(R.id.btSave);
        btPhoto             = (ImageButton) findViewById(R.id.btPhoto);
        swYesNo             = (Switch)      findViewById(R.id.swYesNo);
        lyComment           = (LinearLayout)findViewById(R.id.lyComment);
        lyContentOptions           = (LinearLayout)findViewById(R.id.lyContentOptions);
        lyOptionComment     = (LinearLayout)findViewById(R.id.lyOptionComment);
        imgShared           = (ImageButton) findViewById(R.id.imgShared);

        store               = (Store)               storeRepo.findById(store_id);
        route               = (Route)               routeRepo.findById(store.getRoute_id());
        auditRoadStore      = (AuditRoadStore)      auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
        poll                = (Poll)                pollRepo.findByOrder(orderPoll);
        products            = (ArrayList<Product>)  productRepo.findAll();

        poll.setCategory_product_id(category_product_id);
        poll.setProduct_id(product_id);
        poll.setPublicity_id(publicity_id);


        tvCampaign.setText(Html.fromHtml("<strong>" + getString(R.string.text_campaign )+ ": </strong>") + company.getFullname()+" (ID:"+ company.getId()+")");
        tvUserEmail.setText(Html.fromHtml("<strong>" + getString(R.string.text_user_name) + ": </strong>") + user_email +" (ID:"+ user_id +")");

        tvStoreFullName.setText(String.valueOf(store.getFullname()));
        tvStoreId.setText(String.valueOf(store.getId()));
        tvAddress.setText(String.valueOf(store.getAddress()));
        tvReferencia.setText(String.valueOf(store.getUrbanization()));
        tvDistrict.setText(String.valueOf(store.getDistrict()));
        //tvAuditoria.setText(auditRoadStore.getList().getFullname().toString());
        tvPoll.setText(poll.getQuestion().toString());
        tvOwner.setText(String.valueOf(store.getCodCliente()));

        imgShared.setOnClickListener(new View.OnClickListener() {
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

//        btPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                takePhoto();
//            }
//        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOptions = "";
                int valueChecked = 0;
                if(checkBoxArray != null) {
//                    for(CheckBox r: checkBoxArray) {
//                        if(r.isChecked()){
//                            valueChecked = 1 ;
//                        } else {
//                            valueChecked = 0 ;
//                        }
//                        selectedOptions += r.getTag().toString() + "-" + String.valueOf(valueChecked) + ";" + String.valueOf(r) + "|";
//
//                    }
                    for(int i=0; i<checkBoxArray.length; i++) {
                        valueChecked = 0;
                        if(checkBoxArray[i].isChecked()){
                            valueChecked = 1 ;
                            // selectedOptions += checkBoxArray[i].getTag().toString() + "-" + String.valueOf(valueChecked) + ";" + String.valueOf(editTextArray[i].getText().toString()) + "|";
                            selectedOptions += checkBoxArray[i].getTag().toString() + "-" + String.valueOf(editTextArray[i].getText().toString()) + "|";
                           String strCant  =  String.valueOf(editTextArray[i].getText().toString().trim());
                            if(strCant.equals("")){
                                Toast.makeText(activity, R.string.message_input_cant, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    //                    if(counterSelected==0){
//                        Toast.makeText(activity, R.string.message_select_options, Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar la lista de publicidades: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {


//                        Toast.makeText(activity, selectedOptions , Toast.LENGTH_SHORT).show();

                        pollDetail = new PollDetail();
                        pollDetail.setPoll_id(poll.getId());
                        pollDetail.setStore_id(store_id);
                        pollDetail.setSino(1);
                        pollDetail.setOptions(0);
                        pollDetail.setLimits(0);
                        pollDetail.setMedia(0);
                        pollDetail.setComment(0);
                        pollDetail.setResult(1);
                        pollDetail.setLimite("0");
                        pollDetail.setComentario(selectedOptions);
                        pollDetail.setAuditor(user_id);
                        pollDetail.setProduct_id(0);
                        pollDetail.setCategory_product_id(0);
                        pollDetail.setPublicity_id(0);
                        pollDetail.setCompany_id(company_id);
                        pollDetail.setCommentOptions(0);
                        pollDetail.setSelectdOptions("");
                        pollDetail.setSelectedOtionsComment("");
                        pollDetail.setPriority(0);

                        new loadPoll().execute();
                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                builder.setCancelable(false);
            }
        });

        loadCotrolProducts();

    }

    private void loadCotrolProducts() {

        lyContentOptions.removeAllViews();

        if(products.size() > 0) {
            checkBoxArray = new CheckBox[products.size()];
            editTextArray = new EditText[products.size()];

            int counter =0;

            for(Product p: products){
                checkBoxArray[counter] = new CheckBox(activity);
                checkBoxArray[counter].setId(counter);
                checkBoxArray[counter].setText(p.getFullname().toString());
                checkBoxArray[counter].setTag(String.valueOf(p.getId()));

                editTextArray[counter] = new EditText(activity);
                editTextArray[counter].setId(counter);
                editTextArray[counter].setHint(R.string.text_cant);
                editTextArray[counter].setWidth(500);

                editTextArray[counter].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED );
                editTextArray[counter].setVisibility(View.GONE);
//                    if(po.getComment()==1) {
                final int finalCounter = counter;
                checkBoxArray[counter].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            editTextArray[finalCounter].setVisibility(View.VISIBLE);
                        } else {
                            editTextArray[finalCounter].setVisibility(View.GONE);
                        }
                    }
                });

                lyContentOptions.addView(checkBoxArray[counter]);
                lyContentOptions.addView(editTextArray[counter]);
                counter ++;
            }
            // lyOptions.addView(radioGroup);

        }
    }

    private void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }



    class loadPoll extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
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


             if(!AuditUtil.insertPollDetailAllProduct(pollDetail)) return false;
            if(!AuditUtil.closeAuditStore(audit_id,store_id,company_id,route.getId())) return  false; // (store_id,audit_id, rout_id)) return false ;

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){

//                Poll poll = new Poll();
//                poll.setOrder(4);
//                openActivity(activity, store_id,audit_id,poll);

                AuditRoadStore auditRoadStore = (AuditRoadStore) auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
                //AuditRoadStore auditRoadStore = (AuditRoadStore) auditRoadStoreRepo.findByStoreIdAndAuditIdAndVisitId(store_id,audit_id,store.getVisit_id());
                auditRoadStore.setAuditStatus(1);
                auditRoadStoreRepo.update(auditRoadStore);
                finish();



            } else {
                Toast.makeText(activity , R.string.saveError, Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
        }
    }

    /**
     * Guarda la pregunta segun el orden en casos
     * @param orderPoll
     * @return
     */
    private boolean logicProcess(int orderPoll) {

        pollDetail = new PollDetail();
        pollDetail.setPoll_id(poll.getId());
        pollDetail.setStore_id(store_id);
        pollDetail.setSino(poll.getSino());
        pollDetail.setOptions(poll.getOptions());
        pollDetail.setLimits(0);
        pollDetail.setMedia(poll.getMedia());
        pollDetail.setComment(0);
        pollDetail.setResult(isYesNo);
        pollDetail.setLimite("0");
        pollDetail.setComentario(comment);
        pollDetail.setAuditor(user_id);
        pollDetail.setProduct_id(poll.getProduct_id());
        pollDetail.setCategory_product_id(poll.getCategory_product_id());
        pollDetail.setPublicity_id(poll.getPublicity_id());
        pollDetail.setCompany_id(company_id);
        pollDetail.setCommentOptions(poll.getComment());
        pollDetail.setSelectdOptions(selectedOptions);
        pollDetail.setSelectedOtionsComment(commentOptions);
        pollDetail.setPriority(0);

        switch (orderPoll) {
            case 1:
                if (isYesNo == 1) {
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                } else if (isYesNo == 0) {
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
                }
                break;
            case 2:
                if (isYesNo == 1) {
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                } else if (isYesNo == 0) {
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
                }
                break;

        }
        return true;
    }




    @Override
    public void onBackPressed() {

        if (poll.getOrder() > 6) {
            alertDialogBasico(getString(R.string.message_audit_init) );

        } else {
            super.onBackPressed();
        }

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

    private void openActivity(Activity activity, int store_id, int audit_id, Poll poll) {

        Intent intent = new Intent(activity, ProductCompetityActivity.class);
        intent.putExtra("store_id"              , store_id);
        intent.putExtra("audit_id"              , audit_id);
        intent.putExtra("orderPoll"             , poll.getOrder());
        intent.putExtra("category_product_id"   , 0);
        startActivity(intent);

    }

}
