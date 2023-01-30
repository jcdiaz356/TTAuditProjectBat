package com.dataservicios.ttauditprojectbat.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.adapter.MediaAdapterReciclerView;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.model.Media;
import com.dataservicios.ttauditprojectbat.repo.CompanyRepo;
import com.dataservicios.ttauditprojectbat.repo.MediaRepo;
import com.dataservicios.ttauditprojectbat.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class MediasFragment extends Fragment {


    private static final String LOG_TAG = MediasFragment.class.getSimpleName();

    private SessionManager              session;
    private int                         user_id;
    private int                         company_id;
    private MediaAdapterReciclerView    mediaAdapterRecyclerView;
    private RecyclerView mediaRecycler;
    private Media                       media;
    private CompanyRepo                 companyRepo;
    private Company                     company;
    private MediaRepo                   mediaRepo;

    public MediasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        session = new SessionManager(getActivity());
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;

        DatabaseManager.init(getActivity());

        //getActivity().stopService(new Intent(getActivity(), UpdateService.class));

        mediaRepo = new MediaRepo(getActivity());
        companyRepo = new CompanyRepo(getActivity());

        ArrayList<Company> companies = (ArrayList<Company>) companyRepo.findAll();
        for (Company c: companies){
            company_id = c.getId();
        }

        mediaRepo = new MediaRepo(getActivity());
//        ArrayList<Media> medias = (ArrayList<Media>) mediaRepo.findAll();
        ArrayList<Media> medias = (ArrayList<Media>) mediaRepo.findByCompanyId(company_id);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medias, container, false);
        //RecyclerView mediaRecycler  = (RecyclerView) view.findViewById(R.id.mediaRecycler);
        mediaRecycler  = (RecyclerView) view.findViewById(R.id.mediaRecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mediaRecycler.setLayoutManager(linearLayoutManager);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),"fff", Toast.LENGTH_SHORT).show();
            }
        };


        mediaAdapterRecyclerView =  new MediaAdapterReciclerView(medias, R.layout.cardview_media, getActivity());
        mediaRecycler.setAdapter(mediaAdapterRecyclerView);

        // new loadRoute().execute();



        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        //getActivity().startService(new Intent(getActivity(), UpdateService.class));

    }
    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        super.onDetach();
       // getActivity().startService(new Intent(getActivity(), UpdateService.class));
       // getActivity().startService(new Intent(getActivity(), UpdateService.class));
    }
    @Override
    public void onDestroy() {

        // getActivity().startService(new Intent(getActivity(), UpdateService.class));
        super.onDestroy();


    }
}
