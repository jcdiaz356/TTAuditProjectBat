package com.dataservicios.ttauditprojectbat.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.dataservicios.ttauditprojectbat.model.Media;
import com.dataservicios.ttauditprojectbat.repo.MediaRepo;
import com.dataservicios.ttauditprojectbat.util.BitmapLoader;
import com.dataservicios.ttauditprojectbat.util.GlobalConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class UplodMediaService extends Service {
    private static final String LOG_TAG = UplodMediaService.class.getSimpleName();
    public int counter=0;
    private Context context = this;
    private Timer timer;
    private TimerTask timerTask;

    long oldTime=0;

    public UplodMediaService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }
    public UplodMediaService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
//        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
//        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * Establece
     */
    public void startTimer() {
        //establecer un nuevo temporizador
        timer = new Timer();
        //Inicializar el trabajo de TimerTask.
        initializeTimerTask();
        //programar el temporizador, para despertarse cada 5 segundo
        //timer.schedule(timerTask, 1000, 1000); //
        timer.schedule(timerTask, 5000,5000); //
    }

    /**
     * configura el temporizador para imprimir el contador cada x segundos
     */
    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
//                Log.i("in timer", "in timer ++++  "+ (counter++));

                final MediaRepo mediaRepo = new MediaRepo(context);
                final Media media ;
                final File file;

                if (mediaRepo.countReg() >0 ) {

                    ArrayList<Media> medias = (ArrayList<Media>) mediaRepo.findAll();
                    media = (Media) mediaRepo.findFirstActive();
                    file = new File(BitmapLoader.getAlbumDirTemp(context).getAbsolutePath() + "/" + media.getFile());
                    if(!file.exists()){
                        media.setStatus(1);
                        media.setLog("Archivo no se ecuentra en la memoria");
                        mediaRepo.update(media);
                    } else {
                        HashMap<String, String> params = new HashMap<>();

                        // params.put("fotoUp"               ,  imgFile                                                            );
                        params.put("archivo"              ,  String.valueOf(file.getName())                    );
                        params.put("store_id"             ,  String.valueOf(media.getStore_id())              );
                        params.put("product_id"           ,  String.valueOf(media.getProduct_id())            );
                        params.put("poll_id"              ,  String.valueOf(media.getPoll_id())               );
                        params.put("publicities_id"       ,  String.valueOf(media.getPublicity_id())          );
                        params.put("category_product_id"  ,  String.valueOf(media.getCategory_product_id())   );
                        params.put("company_id"           ,  String.valueOf(media.getCompany_id())            );
                        params.put("tipo"                 ,  String.valueOf(media.getType())                  );
                        params.put("monto"                ,  String.valueOf(media.getMonto())                 );
                        params.put("razon_social"         ,  String.valueOf(media.getRazonSocial())           );
                        params.put("horaSistema"          ,  String.valueOf(media.getCreated_at())            );

                        AndroidNetworking.upload(GlobalConstant.dominio + "/insertImagesMayorista")
                                .addMultipartFile("fotoUp",file)
                                .addMultipartParameter(params)
                                .setTag("uploadTest")
                                .setPriority(Priority.HIGH)
                                .build()
                                .setUploadProgressListener(new UploadProgressListener() {
                                    @Override
                                    public void onProgress(long bytesUploaded, long totalBytes) {
                                        // do anything with progress
                                        int value = (int) (( bytesUploaded / totalBytes)*100); // (bytesUploaded / totalBytes)*100 + " %");
                                    }
                                })
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // below code will be executed in the executor provided
                                        // do anything with response
                                        Log.d(LOG_TAG,response.toString());
                                        int success = 0;
                                        try {
                                            success = response.getInt("success");
                                            if (success == 1) {
                                                mediaRepo.delete(media);
                                                file.delete();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.i(LOG_TAG, "----> Se envió correctamente el archivo");
                                    }
                                    @Override
                                    public void onError(ANError error) {
                                        // handle error
                                        String err = error.getMessage().toString();
                                        Log.i(LOG_TAG, "----- Error no se po enciar el archvo ------");
                                    }
                                    @Override
                                    protected void finalize()  {

                                    }
                                });
                    }
                } else{
                    Log.i(LOG_TAG, "----> No hay fotos para enviar al servidor");
                }


            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
