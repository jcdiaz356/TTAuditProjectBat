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
import com.dataservicios.ttauditprojectbat.model.Distributor;
import com.dataservicios.ttauditprojectbat.model.Poll;
import com.dataservicios.ttauditprojectbat.model.PollDetail;
import com.dataservicios.ttauditprojectbat.model.PollOption;
import com.dataservicios.ttauditprojectbat.model.Route;
import com.dataservicios.ttauditprojectbat.model.Store;
import com.dataservicios.ttauditprojectbat.repo.AuditRoadStoreRepo;
import com.dataservicios.ttauditprojectbat.repo.CompanyRepo;
import com.dataservicios.ttauditprojectbat.repo.DistributorRepo;
import com.dataservicios.ttauditprojectbat.repo.MediaRepo;
import com.dataservicios.ttauditprojectbat.repo.PollOptionRepo;
import com.dataservicios.ttauditprojectbat.repo.PollRepo;
import com.dataservicios.ttauditprojectbat.repo.RouteRepo;
import com.dataservicios.ttauditprojectbat.repo.StoreRepo;
import com.dataservicios.ttauditprojectbat.util.AuditUtil;
import com.dataservicios.ttauditprojectbat.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class PollActivity extends AppCompatActivity {
    private static final String LOG_TAG = PollActivity.class.getSimpleName();
    private SessionManager          session;
    private Activity activity =  this;
    private ProgressDialog pDialog;
    private TextView tvStoreFullName,tvStoreId,tvAddress ,tvReferencia,tvDistrict,tvAuditoria,tvPoll,tvOwner ;
    private TextView tvRouteFullName ;
    private TextView tvCampaign      ;
    private TextView tvUserEmail     ;
    private EditText etComent;
    private EditText etCommentOption;
    private Button btSaveGeo;
    private Button btSave;
    private ImageButton imgShared;
    private CheckBox[]              checkBoxArray;
    private RadioButton[]           radioButtonArray;
    private RadioGroup radioGroup;
    private Switch swYesNo;
    private ImageButton btPhoto;
    private LinearLayout lyComment;
    private LinearLayout lyOptions;
    private LinearLayout lyOptionComment;
    private int                     user_id;
    private int                     store_id;
    private int                     route_id;
    private int                     audit_id;
    private int                     company_id;
    private int                     orderPoll;
    private int                     category_product_id;
    private int                     publicity_id;
    private int                     product_id;
    private RouteRepo               routeRepo ;
    private AuditRoadStoreRepo      auditRoadStoreRepo ;
    private StoreRepo               storeRepo ;
    private CompanyRepo             companyRepo ;
    private PollRepo                pollRepo ;
    private DistributorRepo         distributorRepo;
    private MediaRepo               mediaRepo;
    private Route                   route ;
    private Store                   store ;
    private Poll                    poll;
    private PollOption              pollOption;
    private PollDetail              pollDetail;
    private AuditRoadStore          auditRoadStore;
    private PollOptionRepo          pollOptionRepo;
    private Company                 company;

    private ArrayList<PollOption> pollOptions;
    private int                     isYesNo;
    private String comment;
    private String selectedOptions;
    private String commentOptions;
    private String user_email;
    private String user_name;

    private Distributor distributor;

    private boolean commenteVisible =true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        showToolbar(getString(R.string.title_activity_Stores_Audit),true);

        DatabaseManager.init(this);

        Bundle bundle = getIntent().getExtras();
        store_id            = bundle.getInt("store_id");
        audit_id            = bundle.getInt("audit_id");
        route_id            = bundle.getInt("route_id");
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
        distributorRepo     = new DistributorRepo(activity);
        mediaRepo           = new MediaRepo(activity);



        etCommentOption     = new EditText(activity);
        etComent            = new EditText(activity);

        ArrayList<Company> companies = (ArrayList<Company>) companyRepo.findAll();
        for (Company c: companies){
            company_id = c.getId();
            company = c;
        }

        tvRouteFullName     = (TextView) findViewById(R.id.tvRouteFullName) ;
        tvCampaign           = (TextView) findViewById(R.id.tvCampaign) ;
        tvUserEmail           = (TextView) findViewById(R.id.tvUserEmail) ;
        tvStoreFullName     = (TextView)    findViewById(R.id.tvStoreFullName) ;
        tvStoreId           = (TextView)    findViewById(R.id.tvStoreId) ;
        tvAddress           = (TextView)    findViewById(R.id.tvAddress) ;
        tvReferencia        = (TextView)    findViewById(R.id.tvReferencia) ;
        tvDistrict          = (TextView)    findViewById(R.id.tvDistrict) ;
        tvAuditoria         = (TextView)    findViewById(R.id.tvAuditoria) ;
        tvPoll              = (TextView)    findViewById(R.id.tvPoll) ;
        tvOwner             = (TextView)    findViewById(R.id.tvOwner) ;
        btSaveGeo           = (Button)      findViewById(R.id.btSaveGeo);
        btSave              = (Button)      findViewById(R.id.btSave);
        btPhoto             = (ImageButton) findViewById(R.id.btPhoto);
        swYesNo             = (Switch)      findViewById(R.id.swYesNo);
        lyComment           = (LinearLayout)findViewById(R.id.lyComment);
        lyOptions           = (LinearLayout)findViewById(R.id.lyOptions);
        lyOptionComment     = (LinearLayout)findViewById(R.id.lyOptionComment);
        imgShared           = (ImageButton) findViewById(R.id.imgShared);

        store               = (Store)                   storeRepo.findByIdAndRouteId(store_id,route_id);
        route               = (Route)                   routeRepo.findById(store.getRoute_id());
        auditRoadStore      = (AuditRoadStore)          auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
        //poll                = (Poll)                    pollRepo.findByCompanyAuditIdAndOrder(auditRoadStore.getList().getCompany_audit_id(),orderPoll);
        poll                = (Poll)                    pollRepo.findByOrder(orderPoll);
        pollOptions         = (ArrayList<PollOption>)   pollOptionRepo.findByPollId(poll.getId());
        distributor         = (Distributor)             distributorRepo.findByActive(1);

        poll.setCategory_product_id(category_product_id);
        poll.setProduct_id(product_id);
        poll.setPublicity_id(publicity_id);

        tvCampaign.setText(Html.fromHtml("<strong>" + getString(R.string.text_campaign )+ ": </strong>") + company.getFullname()+" (ID:"+ company.getId()+")");
        tvUserEmail.setText(Html.fromHtml("<strong>" + getString(R.string.text_user_name) + ": </strong>") + user_email +" (ID:"+ user_id +")");
        //tvRouteFullName.setText(Html.fromHtml("<strong>" + getString(R.string.text_route_name )+ ": </strong>") + route.getFullname() +" (ID:"+ route.getId() +")");

        tvStoreFullName.setText(String.valueOf(store.getFullname()));
        tvStoreId.setText(String.valueOf(store.getId()));
        tvAddress.setText(String.valueOf(store.getAddress()));
        tvReferencia.setText(String.valueOf(store.getUrbanization()));
        tvDistrict.setText(String.valueOf(store.getDistrict()));
        //tvAuditoria.setText(auditRoadStore.getList().getFullname().toString());
        tvPoll.setText(poll.getQuestion().toString());
        tvOwner.setText(activity.getString(R.string.text_simpol_soles) + String.valueOf(store.getComment()));




        establishigPropertyPool(orderPoll);


            /*
            Compartir App
             */
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

        btPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.message_save);
                builder.setMessage(R.string.message_save_information);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(swYesNo.isChecked())isYesNo=1; else isYesNo =0;
                        selectedOptions="";
                        int counterSelected =0;
                        if(radioButtonArray != null) {

                            for(RadioButton r:radioButtonArray ) {
                                if(r.isChecked()){
                                    selectedOptions=r.getTag().toString();
                                    counterSelected ++;
                                }
                            }
                            commentOptions = etCommentOption.getText().toString() ;
                            if(counterSelected==0){
                                Toast.makeText(activity, R.string.message_select_options, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }


                        if(checkBoxArray != null) {
                            for(CheckBox r:checkBoxArray ) {
                                if(r.isChecked()){
                                    selectedOptions= r.getTag().toString()+"|" + selectedOptions;
                                    counterSelected ++;
                                }
                            }
                            for (PollOption m: pollOptions) {
                                if (m.getComment()==1) {
                                    commentOptions = etCommentOption.getText().toString() + "|" + commentOptions;
                                } else {
                                    commentOptions =  "|" + commentOptions;
                                }
                            }
                            if(counterSelected==0){
                                Toast.makeText(activity, R.string.message_select_options, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

//                        comment = etComent.getText().toString().trim();
//                        if(poll.getComment_requiered()== 1) {
//                            if(comment.equals("")) {
//                                Toast.makeText(activity , R.string.message_requiere_comment , Toast.LENGTH_LONG).show();
//                                return ;
//                            }
//                        }

                        if(commenteVisible) {
                            comment = etComent.getText().toString().trim();
                            if(poll.getComment_requiered()== 1) {

                                if(comment.equals("")) {
                                    Toast.makeText(activity , R.string.message_requiere_comment , Toast.LENGTH_LONG).show();
                                    return ;
                                }
                            }
                        }

                       // commentOptions = etCommentOption.getText().toString();

                        new savePoll().execute();
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
        if(company.getMax_photo_notification() > 0) {
            if(mediaRepo.countReg() >= company.getMax_photo_notification())
                Toasty.error(activity, String.valueOf(mediaRepo.countReg()) + " " + activity.getString(R.string.message_medias_pending_upload) ,Toast.LENGTH_LONG).show();
        }

        //------------------END Verficicacón------------------------------

    }

    private void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

    private void takePhoto() {

        Intent intent = new Intent(activity, AndroidCustomGalleryActivity.class);
        intent.putExtra("store_id"              ,store_id);
        intent.putExtra("poll_id"               ,poll.getId());
        intent.putExtra("company_id"            ,company_id);
        intent.putExtra("publicities_id"        ,publicity_id);
        intent.putExtra("product_id"            ,product_id);
        intent.putExtra("category_product_id"   ,category_product_id);
        intent.putExtra("monto"                 ,"");
        intent.putExtra("razon_social"          ,"");
        intent.putExtra("tipo"                  ,1);
        startActivity(intent);
    }


    /**
     * Guarda la pregunta
     */
    class savePoll extends AsyncTask<Void, Integer , Boolean> {
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

            boolean result = logicProcess(orderPoll);

            return result;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                resulProcess(orderPoll);
            } else {
                Toast.makeText(activity , R.string.message_no_save_data , Toast.LENGTH_LONG).show();
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
        pollDetail.setLimits(poll.getLimit());
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
        pollDetail.setRoad_id(route_id);
        pollDetail.setPriority(0);


//        Auditorias de Promotor{ia
        if(company.getApp_id().equals("promotoria")) {
            switch (orderPoll) {
//--------Información-----------
                case 1:
                    if (isYesNo == 1) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    } else if (isYesNo == 0) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                       // if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
                    }
                    break;
                case 2:

                    if (isYesNo == 1) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                      //  if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    } else if (isYesNo == 0) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                      //  if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
                    }
                    break;


//--------Efectividad-----
// ------

                case 3:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;


// ----------------------- Auditoria Bat--------------------

                case 5:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 6:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 30:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 7:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 8:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 31:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 9:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;

// ----------------------- Incidencias cigarreras Bat--------------------


                case 10:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 11:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
//                case 21:
//                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                    break;
//                case 12:
//                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                    //if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
//                    break;

                case 16:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 17:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 18:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;break;
                case 19:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                   // if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    break;


// -----------HYPE --------------------+
//                case 51:
//                    if (isYesNo == 1) {
//                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                    } else if (isYesNo == 0) {
//                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                        // if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
//                    }
//                break;

                case 40:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 41:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 42:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;break;
                case 43:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    // if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    break;
                case 44:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    // if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    break;
                case 48:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    // if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    break;



//                    ****************** COMENTARIOS ADICIONALES

                case 60: case 61: case 62:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    // if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    break;


            }
        }

        if(company.getApp_id().equals("bodega")) {
            switch (orderPoll) {
//--------Información-----------
                case 1:
                    if (isYesNo == 1) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    } else if (isYesNo == 0) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                       // if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
                    }
                    break;
//                case 2:
//
//                    if (isYesNo == 1) {
//                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                      //  if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId()))
//                         //   return false;
//                    } else if (isYesNo == 0) {
//                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                      //  if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
//                    }
//                        break;
                case 30:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;

                case 31:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 32:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 60:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 3:
                    if (isYesNo == 1) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                        if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId()));
                        //   return false;
                    } else if (isYesNo == 0) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                        //  if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
                    }

//--------Efectividad-----
// ------




// ----------------------- Auditoria Bat--------------------
//
//                        case 6:
//                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                            break;
                        case 7:
                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                            break;
                        case 70:
                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                            break;
                        case 100:
                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                            break;
                        case 101:
                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                            break;
//                        case 8:
//                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                            break;
//                        case 21:
//                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                            break;
//                        case 20:
//                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                            break;
                        case 21:
                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                            break;


                        case 50:
                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                            break;




//                        case 22:
//                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                           // if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId()))
                              //  return false;
//                            break;

// ----------------------- Incidencias cigarreras Bat--------------------


                        case 10:
                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                            break;
                        case 11:
                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                            break;
                        case 12:
                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                            break;
//                        case 30:
//                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                            break;
//                        case 31:
//                            if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                          //  if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId()))
//
//                            break;



                    }


        }

        if(company.getApp_id().equals("mantenimiento")) {
            switch (orderPoll) {
//--------Información-----------
                case 1:
                    if (isYesNo == 1) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    } else if (isYesNo == 0) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                     //   if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
                    }
                    break;
                case 20:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;

                case 2:

                    if (isYesNo == 1) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                     //   if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    } else if (isYesNo == 0) {
                        if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                     //   if (!AuditUtil.closeAllAuditRoadStore(store_id, company_id)) return false;
                    }
                        break;
//--------Efectividad-----
// ------

                case 3:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;

//--------EJECUCIÓN EN POS -----
// ------

                case 5:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                   // if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    break;


//--------Vista Mantenimineto -----
// ------

                case 6:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 7:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 8:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 9:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 10:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
                case 11:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    //if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId()))  return false;
                    break;

// ----------------------- Vista Instalación--------------------

                case 12:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;

                case 13:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;

                case 14:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;

                case 15:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    //if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId()))  return false;
                    break;

// ----------------------- Vista Reposici{on de cigarrera--------------------

                case 30:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;

                case 31:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    //if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId()))  return false;
                    break;

                // ----------------------- Vista Implementaci{on de material pop--------------------

                case 32:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;

                case 33:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    //if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId()))  return false;
                    break;


            }


        }

        return true;
    }

    /**
     * Resultado despues de aplicar logicProcess
     * @param orderPoll
     */
    private void resulProcess (int orderPoll) {

        if(company.getApp_id().equals("promotoria")) {
            switch (orderPoll) {
//-------------Introducción---------------------
                case 1:
                    if(isYesNo==1) {
                        poll.setOrder(2);
                        openActivity(activity, store_id,audit_id,poll,route_id);
                        finish();
                    } else if(isYesNo==0){
                        ArrayList<AuditRoadStore> auditRoadStores = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreId(store_id);
                        for (AuditRoadStore m: auditRoadStores){
                            m.setAuditStatus(1);
                            auditRoadStoreRepo.update(m);
                        }
                        finish();
                    }
                    break;
                //break;
                case 2:
                    if(isYesNo==1) {
                        auditRoadStore.setAuditStatus(1);
                        auditRoadStoreRepo.update(auditRoadStore);
                        finish();
                    } else if(isYesNo==0){
                        ArrayList<AuditRoadStore> auditRoadStores = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreId(store_id);
                        for (AuditRoadStore m: auditRoadStores){
                            m.setAuditStatus(1);
                            auditRoadStoreRepo.update(m);
                        }
                        finish();
                    }
                    break;


                case 3:

                    Intent intent = new Intent(activity, ProductCompetityActivity.class);
                    intent.putExtra("store_id"              , store_id);
                    intent.putExtra("route_id"              , route_id);
                    intent.putExtra("audit_id"              , audit_id);
                    intent.putExtra("orderPoll"             , poll.getOrder());
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                    intent.putExtra("category_product_id"   , 193);
                    intent.putExtra("publicity_id"          , poll.getPublicity_id());
                    intent.putExtra("product_id"            , poll.getProduct_id());
                    startActivity(intent);

                    finish();

                    break;


//----------------------- Auditoria Bat--------------------

                case 5:
                    poll.setOrder(6);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 6:
                    poll.setOrder(30);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;


                case 30:
                    poll.setOrder(7);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 7:
                    poll.setOrder(8);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 8:
                    poll.setOrder(31);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 31:

                Intent intentP = new Intent(activity, ProductCompetityActivity.class);
                intentP.putExtra("store_id"              , store_id);
                intentP.putExtra("route_id"              , route_id);
                intentP.putExtra("audit_id"              , audit_id);
                intentP.putExtra("orderPoll"             , poll.getOrder());
//                intentP.putExtra("category_product_id"   , poll.getCategory_product_id());
                intentP.putExtra("category_product_id"   , 205);
                intentP.putExtra("publicity_id"          , poll.getPublicity_id());
                intentP.putExtra("product_id"            , poll.getProduct_id());
                startActivity(intentP);

                finish();

                break;


// ----------------------- Incidencias cigarreras Bat--------------------




                case 10:
                    if(isYesNo==1) {
//                        poll.setOrder(21);
                        poll.setOrder(11);
                        openActivity(activity, store_id,audit_id,poll,route_id);
                        finish();
                    } else if(isYesNo==0){
                        poll.setOrder(11);
                        openActivity(activity, store_id,audit_id,poll,route_id);
                        finish();
                    }
                break;

                case 11:
                    auditRoadStore.setAuditStatus(1);
                    auditRoadStoreRepo.update(auditRoadStore);
                    finish();
                    break;

//                    poll.setOrder(21);
//                    openActivity(activity, store_id,audit_id,poll,route_id);
//                    finish();
//                    break;

//                case 21:
//                    poll.setOrder(12);
//                    openActivity(activity, store_id,audit_id,poll,route_id);
//                    finish();
//                    break;

//                case 12:
//                    auditRoadStore.setAuditStatus(1);
//                    auditRoadStoreRepo.update(auditRoadStore);
//                    finish();
//                    break;


                case 16:
                    poll.setOrder(17);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 17:
                    poll.setOrder(18);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 18:
                    poll.setOrder(19);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;


                case 19:
                    auditRoadStore.setAuditStatus(1);
                    auditRoadStoreRepo.update(auditRoadStore);
                    finish();
                    break;


// ---------HYPE --------------------

//                case 51:
//                    if(isYesNo==1) {
////                        poll.setOrder(21);
//                        poll.setOrder(40);
//                        openActivity(activity, store_id,audit_id,poll,route_id);
//                        finish();
//                    } else if(isYesNo==0){
//                        auditRoadStore.setAuditStatus(1);
//                        auditRoadStoreRepo.update(auditRoadStore);
//                        finish();
//
//                    }
//                    break;

                case 40:
                    poll.setOrder(41);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 41:
                    poll.setOrder(42);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 42:
                    poll.setOrder(43);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 43:
                    poll.setOrder(44);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 44:
                    poll.setOrder(48);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 48:
                    poll.setOrder(50);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 50:
                    auditRoadStore.setAuditStatus(1);
                    auditRoadStoreRepo.update(auditRoadStore);
                    finish();
                    break;


//                    ****************** COMENTARIOS ADICIONALES

                case 60:
                    poll.setOrder(61);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 61:
                    poll.setOrder(62);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 62:
                    auditRoadStore.setAuditStatus(1);
                    auditRoadStoreRepo.update(auditRoadStore);
                    finish();
                    break;

            }
        }

        if(company.getApp_id().equals("bodega")) {
            switch (orderPoll) {
//-------------Introducción---------------------
                case 1:

                    if(isYesNo==1) {
//                        poll.setOrder(2);
                        poll.setOrder(30);
                        openActivity(activity, store_id,audit_id,poll,route_id);
                        finish();
                    } else if(isYesNo==0){
                        ArrayList<AuditRoadStore> auditRoadStores = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreId(store_id);
                        for (AuditRoadStore m: auditRoadStores){
                            m.setAuditStatus(1);
                            auditRoadStoreRepo.update(m);
                        }
                        finish();
                    }
                    break;
                //break;
//                case 2:
//                    if(isYesNo==1) {
//                        poll.setOrder(30);
//                        openActivity(activity, store_id,audit_id,poll,route_id);
//                        finish();
//                    } else if(isYesNo==0){
////                        ArrayList<AuditRoadStore> auditRoadStores = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreId(store_id);
////                        for (AuditRoadStore m: auditRoadStores){
////                            m.setAuditStatus(1);
////                            auditRoadStoreRepo.update(m);
////                        }
//
//                    auditRoadStore.setAuditStatus(1);
//                    auditRoadStoreRepo.update(auditRoadStore);
//                        finish();
//                    }
//                    break;

                case 30:
                    poll.setOrder(31);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 31:
                    poll.setOrder(32);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;


                case 32:
                    poll.setOrder(60);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 60:
                    poll.setOrder(3);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 3:

                    Intent intent = new Intent(activity, ProductCompetityActivity.class);
                    intent.putExtra("store_id"              , store_id);
                    intent.putExtra("route_id"              , route_id);
                    intent.putExtra("audit_id"              , audit_id);
                    intent.putExtra("orderPoll"             , poll.getOrder());
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                    intent.putExtra("category_product_id"   , 193);
                    intent.putExtra("publicity_id"          , poll.getPublicity_id());
                    intent.putExtra("product_id"            , poll.getProduct_id());
                    startActivity(intent);

                    finish();

                    break;


//----------------------- Auditoria Bat--------------------


//                case 6:
//                    poll.setOrder(7);
//                    openActivity(activity, store_id,audit_id,poll,route_id);
//                    finish();
//                    break;

                case 21:
                    if(isYesNo==1) {
                        poll.setOrder(7);
                        openActivity(activity, store_id,audit_id,poll,route_id);
                        finish();
                    } else if(isYesNo==0){
//                        auditRoadStore.setAuditStatus(1);
//                        auditRoadStoreRepo.update(auditRoadStore);
                        poll.setOrder(7);
                        openActivity(activity, store_id,audit_id,poll,route_id);
                        finish();
                    }
                    break;

                case 7:
                    poll.setOrder(50);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 50:
                    poll.setOrder(70);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

//                case 9:
//                    poll.setOrder(20);
//                    openActivity(activity, store_id,audit_id,poll,route_id);
//                    finish();
//                    break;
//                case 20:
//                    poll.setOrder(21);
//                    openActivity(activity, store_id,audit_id,poll,route_id);
//                    finish();
//                    break;

                case 70:
//                    poll.setOrder(22);
//                    openActivity(activity, store_id,audit_id,poll,route_id);
//                    finish();

                    Intent intentP = new Intent(activity, ProductCompetityActivity.class);
                    intentP.putExtra("store_id"              , store_id);
                    intentP.putExtra("route_id"              , route_id);
                    intentP.putExtra("audit_id"              , audit_id);
                    intentP.putExtra("orderPoll"             , poll.getOrder());
//                intentP.putExtra("category_product_id"   , poll.getCategory_product_id());
                    intentP.putExtra("category_product_id"   , 205);
                    intentP.putExtra("publicity_id"          , poll.getPublicity_id());
                    intentP.putExtra("product_id"            , poll.getProduct_id());
                    startActivity(intentP);

                    finish();

                    break;


                case 100:
                    poll.setOrder(101);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

                case 101:
                    auditRoadStore.setAuditStatus(1);
                    auditRoadStoreRepo.update(auditRoadStore);
                    finish();
                    break;

//                case 22:
//                    auditRoadStore.setAuditStatus(1);
//                    auditRoadStoreRepo.update(auditRoadStore);
//                    finish();
//                    break;
//

// ----------------------- Incidencias cigarreras Bat--------------------

                case 10:


                    if(isYesNo==1) {
//                        poll.setOrder(12);
//                        openActivity(activity, store_id,audit_id,poll,route_id);
//                        finish();
                        auditRoadStore.setAuditStatus(1);
                        auditRoadStoreRepo.update(auditRoadStore);
                        finish();
                        break;
                    } else if(isYesNo==0){
                        poll.setOrder(11);
                        openActivity(activity, store_id,audit_id,poll,route_id);
                        finish();

                    }
                    break;

                case 11:
//                    poll.setOrder(12);
//                    openActivity(activity, store_id,audit_id,poll,route_id);
//                    finish();
                    auditRoadStore.setAuditStatus(1);
                    auditRoadStoreRepo.update(auditRoadStore);
                    finish();
                    break;


                case 12:
                    poll.setOrder(30);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;

//                case 30:
//                    poll.setOrder(31);
//                    openActivity(activity, store_id,audit_id,poll,route_id);
//                    finish();
//                    break;
//
//                case 31:
//
//                    auditRoadStore.setAuditStatus(1);
//                    auditRoadStoreRepo.update(auditRoadStore);
//                    finish();
//                    break;

            }
        }

        if(company.getApp_id().equals("mantenimiento")) {
            switch (orderPoll) {
//-------------Introducción---------------------
                case 1:
                    if(isYesNo==1) {
                        poll.setOrder(2);
                        openActivity(activity, store_id,audit_id,poll,route_id);
                        finish();
                    } else if(isYesNo==0){
                        ArrayList<AuditRoadStore> auditRoadStores = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreId(store_id);
                        for (AuditRoadStore m: auditRoadStores){
                            m.setAuditStatus(1);
                            auditRoadStoreRepo.update(m);
                        }
                        finish();
                    }
                    break;

//                case 20:
//                    poll.setOrder(2);
//                    openActivity(activity, store_id,audit_id,poll,route_id);
//                    finish();
//                    break;

                //break;
                case 2:
                    if(isYesNo==1) {
                        auditRoadStore.setAuditStatus(1);
                        auditRoadStoreRepo.update(auditRoadStore);
                        finish();
                    } else if(isYesNo==0){
                        ArrayList<AuditRoadStore> auditRoadStores = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreId(store_id);
                        for (AuditRoadStore m: auditRoadStores){
                            m.setAuditStatus(1);
                            auditRoadStoreRepo.update(m);
                        }
                        finish();
                    }
                    break;
//--------Efectividad-----
// ------
                case 3:



                    Intent intent = new Intent(activity, ProductCompetityActivity.class);
                    intent.putExtra("store_id"              , store_id);
                    intent.putExtra("route_id"              , route_id);
                    intent.putExtra("audit_id"              , audit_id);
                    intent.putExtra("orderPoll"             , poll.getOrder());
//                intent.putExtra("category_product_id"   , poll.getCategory_product_id());
                    intent.putExtra("category_product_id"   , 193);
                    intent.putExtra("publicity_id"          , poll.getPublicity_id());
                    intent.putExtra("product_id"            , poll.getProduct_id());
                    startActivity(intent);

                    finish();

                    break;

//--------EJECUCIÓN EN POS -----
// ------
                case 5:
                    auditRoadStore.setAuditStatus(1);
                    auditRoadStoreRepo.update(auditRoadStore);

                    finish();
                    break;
//--------Vista Mantenimineto -----
// ------

                case 6:
                    if(isYesNo==1) {
                        poll.setOrder(9);
                        openActivity(activity, store_id,audit_id,poll,route_id);
                        finish();
                    } else if(isYesNo==0){
                        poll.setOrder(8);
                        openActivity(activity, store_id,audit_id,poll,route_id);
                        finish();
                    }
                    break;


                case 8:
                    poll.setOrder(9);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;
                case 9:
                    poll.setOrder(10);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;
                case 10:
                    poll.setOrder(11);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;
//                case 11:
//                    auditRoadStore.setAuditStatus(1);
//                    auditRoadStoreRepo.update(auditRoadStore);
//                    finish();
//                    break;


                case 11:
                    poll.setOrder(12);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;
                case 12:
                    poll.setOrder(13);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;
                case 13:
                    poll.setOrder(14);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();

                    break;
                case 14:
                    poll.setOrder(15);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;
                case 15:
                    auditRoadStore.setAuditStatus(1);
                    auditRoadStoreRepo.update(auditRoadStore);
                    finish();
                    break;

// ----------------------- Vista Reposici{on de cigarrera--------------------
                case 30:
                    poll.setOrder(31);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;
                case 31:
                    auditRoadStore.setAuditStatus(1);
                    auditRoadStoreRepo.update(auditRoadStore);
                    finish();
                    break;
// ----------------------- Implementaci{on material pop--------------------
                case 32:
                    poll.setOrder(33);
                    openActivity(activity, store_id,audit_id,poll,route_id);
                    finish();
                    break;
                case 33:
                    auditRoadStore.setAuditStatus(1);
                    auditRoadStoreRepo.update(auditRoadStore);
                    finish();
                    break;

            }
        }

    }

    private void openActivity(Activity activity, int store_id, int audit_id, Poll poll,int route_id) {
        Intent intent = new Intent(activity, PollActivity.class);
        intent.putExtra("store_id"              , store_id);
        intent.putExtra("route_id"              , route_id);
        intent.putExtra("audit_id"              , audit_id);
        intent.putExtra("orderPoll"             , poll.getOrder());
        intent.putExtra("category_product_id"   , poll.getCategory_product_id());
        intent.putExtra("publicity_id"          , poll.getPublicity_id());
        intent.putExtra("product_id"            , poll.getProduct_id());
        startActivity(intent);
    }

    /**
     * Estableciendo propiedades de un Poll (Media, comment, options)
     * @param orderPoll
     */
    private void establishigPropertyPool(int orderPoll) {
        // if(poll.getMedia() == 1)  btPhoto.setVisibility(View.VISIBLE); else btPhoto.setVisibility(View.GONE);
        if(poll.getComment() == 1) {
            if(poll.getComment_yes_no() == 3 ) {
                showCommentControl(true,poll.getComentType());
            } else if(poll.getComment_yes_no() == 0 ) {
                showCommentControl(true,poll.getComentType());
            }  else if(poll.getComment_yes_no() == 1 ) {
                showCommentControl(false,poll.getComentType());
            }
            //
        } else {
            showCommentControl(false, poll.getComentType());
        }
        if(poll.getSino() == 1) {
            swYesNo.setVisibility(View.VISIBLE);
            showPollOptionsControl(0);
        } else if(poll.getSino()==0 && poll.getOptions() == 1) {
            showPollOptionsControl(3);
            swYesNo.setVisibility(View.GONE);
        } else if(poll.getSino() == 0 && poll.getOptions() == 0){
            swYesNo.setVisibility(View.GONE);
        }

        if(poll.getMedia() == 1)  {
            if (poll.getMedia_yes_no()== 2) btPhoto.setVisibility(View.VISIBLE); else  showControlCamera(isYesNo);
        } else {
            btPhoto.setVisibility(View.GONE);
        }

        swYesNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int checked= 0;
                if (isChecked) {
                    showPollOptionsControl(1);
                    if(poll.getMedia() == 1) {
                        showControlCamera(1);
                    }
                    if(poll.getComment() == 1) {
                        if(poll.getComment_yes_no() == 1 ) {
                            showCommentControl(true,poll.getComentType());
                        }else if (poll.getComment_yes_no() == 0){
                            showCommentControl(false,poll.getComentType());
                        }
                    }

                } else{

                    showPollOptionsControl(0);
                    if(poll.getMedia() == 1) {
                        showControlCamera(0);
                    }

                    if(poll.getComment() == 1) {
                        if(poll.getComment_yes_no() == 1 ) {
                            showCommentControl(false,poll.getComentType());
                        }else if (poll.getComment_yes_no() == 0){
                            showCommentControl(true,poll.getComentType());
                        }
                    }
                }

            }
        });
    }

    private void showControlCamera(int intChecked) {
        //if(poll.getMedia() == 1 && poll.getMedia_yes_no()==0)  btPhoto.setVisibility(View.VISIBGonLE);

        if (intChecked == 0) {
            if (poll.getMedia_yes_no()== 0) {
                btPhoto.setVisibility(View.VISIBLE);
            } else if  (poll.getMedia_yes_no()== 1) {
                btPhoto.setVisibility(View.GONE);
            }
        } else if(intChecked == 1 ) {
            if (poll.getMedia_yes_no()== 1) {
                btPhoto.setVisibility(View.VISIBLE);
            }else if (poll.getMedia_yes_no()== 0) {
                btPhoto.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Muestra control de comentario para el Poll principal
     * @param visibility
     */
    private void showCommentControl(boolean visibility, int type) {
        etComent.setText("");
        etComent.setHint(poll.getComentTag().toString());
        switch (type){
            case 0:
                etComent.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS );
                break;
            case 1:
                etComent.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED );
                break;
            case 2:
                etComent.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );
                break;
        }

        if(visibility){
            commenteVisible=visibility;
            lyComment.removeAllViews();

            lyComment.addView(etComent);
        } else {
            commenteVisible=visibility;
            //lyComment.setVisibility(View.GONE);
            lyComment.removeAllViews();
        }
    }
    /**
     * Muestra las opciones de un Poll siempre y cuando tenga option
     * NOTA: Solo está implementado para opciones tipo radioButton, falta implementar para opciones  tipo checBox
     * @param intChecked
     */
    private void showPollOptionsControl(int intChecked) {

//        int intChecked = isChecked ? 1 : 0;

        if(radioGroup != null ){
            radioGroup.clearCheck();
        }
        int contOptionYesNo=0 ;
        for (PollOption po: pollOptions){
            if(po.getOption_yes_no()== intChecked)  contOptionYesNo ++ ;
        }

        lyOptions.removeAllViews();
        lyOptionComment.removeAllViews();
        if(poll.getOptions()== 1) {

            lyOptions.removeAllViews();
            lyOptionComment.removeAllViews();
            radioButtonArray = null;
            checkBoxArray = null;

            if (poll.getOption_type() == 0) {
                if(contOptionYesNo > 0) {
                    radioGroup = new RadioGroup(activity);
                    radioGroup.setOrientation(LinearLayout.VERTICAL);
                    radioButtonArray = new RadioButton[contOptionYesNo];
                    int counter =0;
                    for (final PollOption po: pollOptions){
                        if(po.getOption_yes_no()== intChecked) {
                            radioButtonArray[counter] = new RadioButton(activity);
                            radioButtonArray[counter].setText(po.getOptions());
                            radioButtonArray[counter].setTag(po.getCodigo());
                            if(po.getComment()==1) {
                                radioButtonArray[counter].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        lyOptionComment.removeAllViews();
                                        etCommentOption.setHint(po.getComment_tag());
                                        lyOptionComment.addView(etCommentOption);
                                    }
                                });
                            } else if(po.getComment()==0){
                                radioButtonArray[counter].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        lyOptionComment.removeAllViews();
                                    }
                                });
                            }
                            radioGroup.addView(radioButtonArray[counter]);
                            counter ++;
                        }
                    }
                    lyOptions.addView(radioGroup);
                }
            } else if (poll.getOption_type() == 1) {

                if(contOptionYesNo > 0) {
                    checkBoxArray = new CheckBox[contOptionYesNo];
                    int  counter =0;
                    for ( final PollOption po: pollOptions){
                        if(po.getOption_yes_no()== intChecked) {
                            checkBoxArray[counter] = new CheckBox(activity);
                            checkBoxArray[counter].setText(po.getOptions());
                            checkBoxArray[counter].setTag(po.getCodigo());
                            if (po.getComment() == 1) {
                                checkBoxArray[counter].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (buttonView.isChecked()) {
                                            lyOptionComment.removeAllViews();
                                            etCommentOption.setHint(activity.getString(R.string.text_comment));
                                            etCommentOption.setTag(po.getCodigo().toString());
                                            lyOptionComment.addView(etCommentOption);
                                        } else {
                                            //not checked
                                            lyOptionComment.removeAllViews();
                                        }
                                    }
                                });

                            }

                            lyOptions.addView(checkBoxArray[counter]);
                            counter++;
                        }
                    }
                }

            }
        }
    }


    @Override
    public void onBackPressed() {

        if (poll.getOrder() > 1) {
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
}
