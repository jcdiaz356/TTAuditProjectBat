package com.dataservicios.ttauditprojectbat.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.model.Audit;
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.model.Departament;
import com.dataservicios.ttauditprojectbat.model.District;
import com.dataservicios.ttauditprojectbat.model.Product;
import com.dataservicios.ttauditprojectbat.model.Store;
import com.dataservicios.ttauditprojectbat.model.TypeStore;
import com.dataservicios.ttauditprojectbat.repo.CompanyRepo;
import com.dataservicios.ttauditprojectbat.repo.DepartamentRepo;
import com.dataservicios.ttauditprojectbat.repo.DistrictRepo;
import com.dataservicios.ttauditprojectbat.repo.ProductRepo;
import com.dataservicios.ttauditprojectbat.repo.RouteStoreTimeRepo;
import com.dataservicios.ttauditprojectbat.repo.StoreRepo;
import com.dataservicios.ttauditprojectbat.repo.TypeStoreRepo;
import com.dataservicios.ttauditprojectbat.util.AuditUtil;
import com.dataservicios.ttauditprojectbat.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewStoreActivity extends AppCompatActivity {
    public static final String LOG_TAG = NewStoreActivity.class.getSimpleName();
    private Activity activity = this ;
    private SessionManager          session;
    private String email_user, name_user;
    private Spinner spDistrict;
    private Spinner spDepartament;
    private Spinner spGiro;
    private EditText etFullname, etAddress, etTelefono,etCodDistributor,etVendorFullname,etRuc ;
    private Button btSave;

    private int                     store_id;
    private int                     user_id;
    private int                     audit_id;
    private int                     route_id;
    private ArrayAdapter<String> adapter;
    private List<String> list;
    private ProgressDialog pDialog;
    private StoreRepo               storeRepo;
    private CompanyRepo             companyRepo;
    private DepartamentRepo         departamentRepo;
    private DistrictRepo            districtRepo;
    private TypeStoreRepo           typeStoreRepo;
    private RouteStoreTimeRepo      routeStoreTimeRepo;
    private ProductRepo             productRepo;
    private Company                 company;
    private Store                   store ;
    private Audit                   audit;
    private ArrayList<Departament> departaments;
    private ArrayList<District> districts;
    private ArrayList<TypeStore> typeStores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_store);


        Bundle bundle = getIntent().getExtras();
        route_id = bundle.getInt("route_id");

        session = new SessionManager(activity);
        HashMap<String, String> user = session.getUserDetails();
        name_user   = user.get(SessionManager.KEY_NAME);
        email_user  = user.get(SessionManager.KEY_EMAIL);
        user_id     = Integer.valueOf(user.get(SessionManager.KEY_ID_USER));

        spDistrict          = (Spinner) findViewById(R.id.spDistrict) ;
        spDepartament       = (Spinner) findViewById(R.id.spDepartament) ;
        spGiro              = (Spinner) findViewById(R.id.spGiro) ;
        etFullname          = (EditText) findViewById(R.id.etFullname);
        etRuc               = (EditText) findViewById(R.id.etRuc);
        //etCodDistributor  = (EditText) findViewById(R.id.etCodDistributor);
        etVendorFullname    = (EditText) findViewById(R.id.etVendorFullname);
        etAddress           = (EditText) findViewById(R.id.etAddress) ;
        etTelefono          = (EditText) findViewById(R.id.etTelefono) ;
        btSave              = (Button) findViewById(R.id.btSave);

        companyRepo         = new CompanyRepo(activity);
        storeRepo           = new StoreRepo(activity);
        departamentRepo     = new DepartamentRepo(activity);
        districtRepo        = new DistrictRepo(activity);
        typeStoreRepo       = new TypeStoreRepo(activity);
        routeStoreTimeRepo  = new RouteStoreTimeRepo(activity);
        productRepo         = new ProductRepo(activity);

        store = new Store();

        company         = (Company) companyRepo.findFirstReg();
        departaments    = (ArrayList<Departament>) departamentRepo.findAll();
        //audit_id        = GlobalConstant.audit_id[0];



        ArrayAdapter<Departament> spinnerAdapter = new ArrayAdapter<Departament>(activity, android.R.layout.simple_spinner_item, departaments);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartament.setAdapter(spinnerAdapter);


        spDepartament.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String label = parent.getItemAtPosition(position).toString();
                int departament_id = ((Departament) spDepartament.getSelectedItem()).getId () ;
                String label = ((Departament) spDepartament.getSelectedItem () ).getName () ;


                districts = (ArrayList<District>) districtRepo.findByForDepartamentId(departament_id);
                ArrayAdapter<District> spinnerAdapter = new ArrayAdapter<District>(activity, android.R.layout.simple_spinner_item, districts);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spDistrict.setAdapter(spinnerAdapter);

                //Toast.makeText(parent.getContext(), "Seleciono: " + label + " ID: " + String.valueOf(departament_id) ,Toast.LENGTH_LONG).show();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String label = parent.getItemAtPosition(position).toString();
                int district_id = ((District) spDistrict.getSelectedItem()).getId () ;
                String label = ((District) spDistrict.getSelectedItem () ).getName () ;

                typeStores = (ArrayList<TypeStore>) typeStoreRepo.findAll();
                ArrayAdapter<TypeStore> spinnerAdapter = new ArrayAdapter<TypeStore>(activity, android.R.layout.simple_spinner_item, typeStores);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spGiro.setAdapter(spinnerAdapter);

                //Toast.makeText(parent.getContext(), "Seleciono: " + label + " ID: " + String.valueOf(departament_id) ,Toast.LENGTH_LONG).show();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(etFullname.getText().toString().equals("")) {
                    Toast.makeText(activity, R.string.text_requiere_fullname, Toast.LENGTH_LONG).show();
                    etFullname.requestFocus();
                    return;
                }



//                if(etAddress.getText().toString().equals("")) {
//                    Toast.makeText(activity,R.string.text_requiere_address,Toast.LENGTH_LONG).show();
//                    etAddress.requestFocus();
//                    return;
//                }



                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.message_save);
                builder.setMessage(R.string.message_save_information);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        store.setFullname(etFullname.getText().toString());
                        //store.setCode("Alicorp");
                        store.setCodCliente("NewStore");
                        store.setAddress(etAddress.getText().toString());
                        store.setCadenRuc(etRuc.getText().toString());
                        store.setCodCliente("");
                        store.setDex("");
                        store.setDistrict(spDistrict.getSelectedItem().toString());
                        store.setDepartament(spDepartament.getSelectedItem().toString());
                        store.setGiro(spGiro.getSelectedItem().toString());
                        store.setTelephone(etTelefono.getText().toString());
                        store.setEjecutivo(etVendorFullname.getText().toString());
                        new saveNewStore().execute();
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

    class saveNewStore extends AsyncTask<Void , Integer , Integer> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getString(R.string.text_loading));
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub

            store_id = AuditUtil.newStore(route_id,company.getId(), store,activity);
            if (store_id > 0) {
               // AuditUtil.saveLatLongStore(store_id,lat, lon);
              //  store.setId(store_id);
              //  storeRepo.create(store);
            }
            return store_id;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Integer result) {
            // dismiss the dialog once product deleted
            if (result == 0){
                Toast.makeText(activity , R.string.message_no_save_data, Toast.LENGTH_LONG).show();
            } else {

                ArrayList<Product> products = (ArrayList<Product>) productRepo.findAll();
                for(Product p: products){
                        p.setStatus(0);
                        productRepo.update(p);
                }

//                int store_id = store.getId();
//                Bundle bundle = new Bundle();
//                bundle.putInt("store_id", Integer.valueOf(store_id));
//                Intent intent = new Intent(activity,OrderActivity.class);
//                intent.putExtras(bundle);
//                activity.startActivity(intent);



//                Bundle bundle = new Bundle();
//                bundle.putInt("route_id", Integer.valueOf(0));
//                Intent intent = new Intent(activity,StoresActivity.class);
//                intent.putExtras(bundle);
//                activity.startActivity(intent);

                finish();
            }
            pDialog.dismiss();
        }
    }



}
