package com.dataservicios.ttauditprojectbat.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.repo.CompanyRepo;

import java.util.ArrayList;


public class NewsInformationsActivity extends AppCompatActivity {

    private Activity activity =  this;
    private TextView tv_linkopen;
    private Company company;
    private CompanyRepo companyRepo ;
    private int                     company_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_informations);
        showToolbar(getString(R.string.menu_news_ttaudit),false);


        DatabaseManager.init(this);

        companyRepo         = new CompanyRepo(activity);

        ArrayList<Company> companies = (ArrayList<Company>) companyRepo.findAll();
        for (Company c: companies){
            company_id = c.getId();
            company = c;
        }


        tv_linkopen     = (TextView) findViewById(R.id.tv_linkopen) ;


        tv_linkopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(company.getApp_id().equals("promotoria")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://linktr.ee/ttaudit.bat"));
                    startActivity(intent);
                } else {

                    Toast.makeText(activity,"Material no disponible", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }


    private void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setIcon(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }
}