package com.dataservicios.ttauditprojectbat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Audit;
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.model.Poll;
import com.dataservicios.ttauditprojectbat.model.PollOption;
import com.dataservicios.ttauditprojectbat.model.Product;
import com.dataservicios.ttauditprojectbat.repo.AuditRepo;
import com.dataservicios.ttauditprojectbat.repo.CompanyRepo;
import com.dataservicios.ttauditprojectbat.repo.PollOptionRepo;
import com.dataservicios.ttauditprojectbat.repo.PollRepo;
import com.dataservicios.ttauditprojectbat.repo.ProductRepo;
import com.dataservicios.ttauditprojectbat.util.AuditUtil;
import com.dataservicios.ttauditprojectbat.util.SessionManager;
import com.dataservicios.ttauditprojectbat.util.SyncData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Logcat tag
    private static final int    REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ProgressBar pbLoad;
    private TextView tvLoad, tv_Version ;
    private Activity activity;
    private String app_id;

    private Company company;
    private ArrayList<Company> companies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = (Activity) this;
        pbLoad = (ProgressBar) findViewById(R.id.pbLoad);
        pbLoad.setIndeterminate(false);
        tvLoad = (TextView) findViewById(R.id.tvLoad);
        tv_Version = (TextView) findViewById(R.id.tvVersion);

        DatabaseManager.init(activity);
        PackageInfo pckInfo ;
        try {
            pckInfo= getPackageManager().getPackageInfo(getPackageName(),0);
            tv_Version.setText(pckInfo.versionName);
            app_id = pckInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(checkAndRequestPermissions()) loadLoginActivity();

        company = new Company();
    }


    private void loadLoginActivity() {


       // showRadioButtonDialog();
       // new loadLogin().execute();

        new loadingCampain().execute();

    }






    class loadLogin extends AsyncTask<Void, String, Boolean> {


        ArrayList<Audit> audits;
        ArrayList<Poll> polls;
        ArrayList<PollOption> pollOptions;
        @Override
        protected void onProgressUpdate(String... values) {
            //super.onProgressUpdate(values);
            tvLoad.setText(values[0].toString());
        }
        @Override
        protected void onPreExecute() {
            tvLoad.setText(getString(R.string.text_loading));
           // super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            publishProgress(activity.getString(R.string.text_download_init));
            publishProgress(activity.getString(R.string.text_download_companies));










          //  company = AuditUtil.getCompany(1,1,app_id);


//            -----------------PARA PRUEBAS----------------------

//            company.setId(284);
//            company.setActive(1);
//            company.setApp_id("com.dataservicios.ttauditejecuciongbodegas");
//            company.setCustomer_id(4);
//            company.setFullname("Prueba Alicorp Solutions");
//            company.setLogo("");
//            company.setVisible(1);
//            company.setMarkerPoint("http://ttaudit.com/rutas-auditor/img/marker_bayer_mercaderismo_app.png");
//            companyRepo.deleteAll();
//            companyRepo.create(company);

//            -------------------------------------------------







            if(company.getId() == 0) {
                return false;
            }


            if( company.getId() != 0 ) {

                publishProgress(activity.getString(R.string.text_download_audits));
                AuditRepo auditRepo= new AuditRepo(activity);
                auditRepo.deleteAll();
                ArrayList<Audit> audits = (ArrayList<Audit>) auditRepo.findAllForCompanyId(company.getId());
                if(audits.size()==0){
                    //auditRepo.deleteAll();
                    audits = AuditUtil.getAudits(company.getId());
                    if(audits.size()!=0){
                        for(Audit p: audits){
                            auditRepo.create(p);
                        }
                    } else  {
                        return  false;
                    }
                }


                publishProgress(activity.getString(R.string.text_download_polls));
                PollRepo pollRepo= new PollRepo(activity);
                pollRepo.deleteAll();
                ArrayList<Poll> polls = (ArrayList<Poll>) pollRepo.findAllForCompanyId(company.getId());
                if(polls.size()==0){

                    polls = AuditUtil.getPolls(company.getId());
                    if(polls.size()!=0){
                        for(Poll p: polls){
                            pollRepo.create(p);
                        }
                    } else  {
                        return  false;
                    }
                }

                publishProgress(activity.getString(R.string.text_download_polls_otpions));
                PollOptionRepo pollOptionRepo= new PollOptionRepo(activity);
                pollOptionRepo.deleteAll();
                ArrayList<PollOption> pollOptions = (ArrayList<PollOption>) pollOptionRepo.findAllForCompanyId(company.getId());
                if(pollOptions.size()==0){

                    pollOptions = AuditUtil.getPollOptions(company.getId());
                    if(pollOptions.size()!=0){
                        for(PollOption p: pollOptions){
                            pollOptionRepo.create(p);
                        }
                    } else  {
                        return  false;
                    }
                }

                publishProgress(activity.getString(R.string.text_download_products_competity));
                ProductRepo productRepo = new ProductRepo(activity);

                productRepo.deleteAll();
                ArrayList<Product> products = (ArrayList<Product>) productRepo.findByCompanyId(company.getId());

                if(products.size()==0){

                    //products = AuditUtil.getListProductsCompetity(company.getId(),0);
                    products = AuditUtil.getListProducts(company.getId());
                    if(products.size()!=0){
                        for(Product p: products){
                            productRepo.create(p);
                        }
                    } else  {
                        //return  false;
                    }
                }


//                publishProgress(activity.getString(R.string.text_download_publicity));
//                PublicityRepo publictyRepo= new PublicityRepo(activity);
//                ArrayList<Publicity> publicities = (ArrayList<Publicity>) publictyRepo.findByCompanyId(company.getId());
//                if(publicities.size()==0){
//                    publictyRepo.deleteAll();
//                    publicities = AuditUtil.getListPublicities(company.getId());
//                    if(publicities.size()!=0){
//                        for(Publicity p: publicities){
//                            publictyRepo.create(p);
//                        }
//                    } else  {
//                        return  false;
//                    }
//                }
            }
            publishProgress(activity.getString(R.string.text_download_terminate));
            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if(result == true) {
                SessionManager session = new SessionManager(activity);
                if(session.isLoggedIn()){
//                    Intent i = new Intent(activity, PanelAdminActivity.class);
//                    startActivity(i);
//                    finish();


                    HashMap<String, String> userSesion = session.getUserDetails();
                    Integer user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;

                    AsyncTask syncData = new SyncData(activity,  user_id, company.getId(), new SyncData.AsyncResponse() {
                        @Override
                        public void processFinish(Boolean output) {
                            Intent i = new Intent(activity, PanelAdminActivity.class);
                            startActivity(i);
                            finish();
                            Toast.makeText(activity, R.string.message_login_success, Toast.LENGTH_LONG).show();
                        }
                    }).execute();

                }else{
                    session.checkLogin(LoginActivity.class);
                };

//                HashMap<String, String> userSesion = session.getUserDetails();
//                user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;
            } else {
                String message  ;
                message = activity.getString(R.string.message_app_no_initialize) + "\n";

                if (company.getId() == 0) {
                    message = message.concat(activity.getString(R.string.message_no_get_company));
                    message = message.concat("\n");
                    //activity.finish();
                    //-------------------------------------------
                    //Creandd Publicity de prueba
                    //-------------------------------------------
                }
                if (audits == null) {
                    message = message.concat(activity.getString(R.string.message_no_get_audit));
                    message = message.concat("\n");

                    //activity.finish();
                }
                alertDialogBasico(message);
            }
        }
    }


    class  loadingCampain extends  AsyncTask<Void,String,Boolean>{


        @Override
        protected void onProgressUpdate(String... values) {
            //super.onProgressUpdate(values);
            tvLoad.setText(values[0].toString());
        }
        @Override
        protected void onPreExecute() {
            tvLoad.setText(getString(R.string.text_loading));
            // super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            publishProgress(activity.getString(R.string.text_download_init));
            publishProgress(activity.getString(R.string.text_download_companies));


            companies = AuditUtil.getCompaniesForStudy(1,1);

            if(companies.size() >0){

                return true;

            }


            return false;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if(result == true) {

                if(companies.size()>0){

                    final Dialog dialog = new Dialog(activity);
                   // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.radiobutton_dialog);
                    //dialog.setTitle("message");
                    dialog.setCancelable(false);

//                    List<String> stringList=new ArrayList<>();  // here is list
//                    for(int i=0;i<2;i++) {
//                        if (i==0){
//                            stringList.add("Number Mode");
//                        }else {
//                            stringList.add("Character Mode");
//                        }
//
//                    }

                    TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
                    RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

                    tvTitle.setText("Seleccione Campaña");

                    for(int i=0;i<companies.size();i++){
                        RadioButton rb=new RadioButton(activity); // dynamically creating RadioButton and adding to RadioGroup.
                        rb.setText(companies.get(i).getFullname().toString());
                        rb.setTag(i);

                        rg.addView(rb);
                        rb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CompanyRepo companyRepo = new CompanyRepo(activity);
                                companyRepo.deleteAll();

                                
                                company = companies.get(Integer.valueOf(rb.getTag().toString()));

//                                 ************** SECAMBIO PARA HACER PRUEBAS********
//                                company.setId(423);
//                                company.setActive(1);
//                                company.setApp_id("promotoria");
//                                company.setCustomer_id(18);
//                                company.setFullname("Promotoria Bat Pruebas");
//                                company.setLogo("");
//                                company.setVisible(1);
//                                company.setMarkerPoint("http://ttaudit.com/rutas-auditor/img/marker_bayer_mercaderismo_app.png");

//                                ************** SECAMBIO PARA HACER PRUEBAS********
                            //    company.setId(387);
//                                ****************************************************

                                companyRepo.create(company);

                                Toast.makeText(activity, rb.getText().toString(), Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                                new loadLogin().execute();


                            }
                        });
                    }

                    dialog.show();

                }

            } else {


            }
        }
    }

    //  Chequeando permisos de usuario Runtime
    private boolean checkAndRequestPermissions() {

        int locationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int callPhonePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
        int readPhoneStatePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

//        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);
//        }








//        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            //listPermissionsNeeded.add(Manifest.permission.WRITE);
//        }

        if (callPhonePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }

        if (readPhoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean respuestas = false ;
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {


            if (grantResults.length > 0) {
                boolean permissionsApp = true ;
                for(int i=0; i < grantResults.length; i++) {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //alertDialogBasico();
                        permissionsApp = false;
                        break;
                    }
                }
                if (permissionsApp==true)  loadLoginActivity();
                else alertDialogBasico(activity.getString(R.string.dialog_message_permission));
            }
        }
    }

    public void alertDialogBasico(String message) {

        // 1. Instancia de AlertDialog.Builder con este constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 2. Encadenar varios métodos setter para ajustar las características del diálogo
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.exit(0);
            }
        });
        builder.show();

    }


    private void showRadioButtonDialog() {

        // custom dialog

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radiobutton_dialog);
        List<String> stringList=new ArrayList<>();  // here is list
        for(int i=0;i<2;i++) {
            if (i==0){
                stringList.add("Number Mode");
            }else {
                stringList.add("Character Mode");
            }

        }

        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        for(int i=0;i<stringList.size();i++){
            RadioButton rb=new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(stringList.get(i));

            rg.addView(rb);
             rb.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Toast.makeText(activity, rb.getText().toString(), Toast.LENGTH_SHORT).show();
                 }
             });
        }

        dialog.show();
    }




}
