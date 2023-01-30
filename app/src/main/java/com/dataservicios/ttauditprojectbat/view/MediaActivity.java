package com.dataservicios.ttauditprojectbat.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.adapter.MediaAdapterReciclerView;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.model.Media;
import com.dataservicios.ttauditprojectbat.repo.CompanyRepo;
import com.dataservicios.ttauditprojectbat.repo.MediaRepo;
import com.dataservicios.ttauditprojectbat.services.UplodMediaService;
import com.dataservicios.ttauditprojectbat.util.SessionManager;
import com.dataservicios.ttauditprojectbat.view.fragment.MediasFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class MediaActivity extends AppCompatActivity {
    private static final String LOG_TAG = MediasFragment.class.getSimpleName();

    private Activity activity = this;
    private SessionManager session;
    private int                         user_id;
    private int                         company_id;
    private MediaAdapterReciclerView mediaAdapterRecyclerView;
    private RecyclerView mediaRecycler;
    private Media media;
    private CompanyRepo companyRepo;
    private Company company;
    private MediaRepo mediaRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        String myString = getResources().getStringArray(R.array.nav_drawer_items)[3];
        showToolbar(myString,false);

        session = new SessionManager(activity);
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;

        DatabaseManager.init(activity);

        //activity.stopService(new Intent(activity, UpdateService.class));

        mediaRepo = new MediaRepo(activity);
        companyRepo = new CompanyRepo(activity);

        ArrayList<Company> companies = (ArrayList<Company>) companyRepo.findAll();
        for (Company c: companies){
            company_id = c.getId();
        }

        mediaRepo = new MediaRepo(activity);
//        ArrayList<Media> medias = (ArrayList<Media>) mediaRepo.findAll();
        ArrayList<Media> medias = (ArrayList<Media>) mediaRepo.findByCompanyId(company_id);

        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_medias, container, false);
        //RecyclerView mediaRecycler  = (RecyclerView) view.findViewById(R.id.mediaRecycler);
        mediaRecycler  = (RecyclerView) findViewById(R.id.mediaRecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mediaRecycler.setLayoutManager(linearLayoutManager);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(activity,"fff", Toast.LENGTH_SHORT).show();
            }
        };


        mediaAdapterRecyclerView =  new MediaAdapterReciclerView(medias, R.layout.cardview_media, activity);
        mediaRecycler.setAdapter(mediaAdapterRecyclerView);

        stopService(new Intent(activity, UplodMediaService.class)) ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isMyServiceRunning(UplodMediaService.class)) {
            startService(new Intent(activity, UplodMediaService.class));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    private void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setIcon(R.drawable.ic_action_camera_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }
}
