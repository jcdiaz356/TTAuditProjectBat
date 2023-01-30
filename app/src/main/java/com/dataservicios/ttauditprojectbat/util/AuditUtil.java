package com.dataservicios.ttauditprojectbat.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.dataservicios.ttauditprojectbat.R;
import com.dataservicios.ttauditprojectbat.model.Audit;
import com.dataservicios.ttauditprojectbat.model.AuditRoadStore;
import com.dataservicios.ttauditprojectbat.model.CategoryProduct;
import com.dataservicios.ttauditprojectbat.model.Company;
import com.dataservicios.ttauditprojectbat.model.Distributor;
import com.dataservicios.ttauditprojectbat.model.Media;
import com.dataservicios.ttauditprojectbat.model.Poll;
import com.dataservicios.ttauditprojectbat.model.PollDetail;
import com.dataservicios.ttauditprojectbat.model.PollOption;
import com.dataservicios.ttauditprojectbat.model.Product;
import com.dataservicios.ttauditprojectbat.model.ProjectionSale;
import com.dataservicios.ttauditprojectbat.model.Publicity;
import com.dataservicios.ttauditprojectbat.model.PublicityStore;
import com.dataservicios.ttauditprojectbat.model.Route;
import com.dataservicios.ttauditprojectbat.model.RouteStoreTime;
import com.dataservicios.ttauditprojectbat.model.Store;
import com.dataservicios.ttauditprojectbat.model.User;
import com.dataservicios.ttauditprojectbat.repo.AuditRepo;
import com.dataservicios.ttauditprojectbat.repo.AuditRoadStoreRepo;
import com.dataservicios.ttauditprojectbat.repo.MediaRepo;
import com.dataservicios.ttauditprojectbat.repo.StoreRepo;

import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Jaime on 28/08/2016.
 */
public class AuditUtil {
    public static final String LOG_TAG = AuditUtil.class.getSimpleName();
    //private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private Context context;

    public AuditUtil(Context context) {
        this.context = context ;
    }

    public boolean uploadMedia(Media media, int typeSend){
        HttpURLConnection httpConnection = null;
        final String url_upload_image = GlobalConstant.dominio + "/insertImagesMayorista";
        File file = new File(BitmapLoader.getAlbumDirTemp(context).getAbsolutePath() + "/" + media.getFile());
        if(!file.exists()){
            return true;
        }
        Bitmap bbicon = null;
        //Bitmap scaledBitmap;
        bbicon = BitmapLoader.loadBitmap(file.getAbsolutePath(),280,280);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bbicon.compress(Bitmap.CompressFormat.JPEG,100, bos);
        try {

            ContentBody photo = new ByteArrayBody(bos.toByteArray(), file.getName());
            AndroidMultiPartEntity mpEntity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
                @Override
                public void transferred(long num) {
                    //notification.contentView.setProgressBar(R.id.progressBar1, 100,(int) ((num / (float) totalSize) * 100), true);
                    // notificationManager.notify(1, notification);
                }
            });

            mpEntity.addPart("fotoUp"               , photo                                                             );
            mpEntity.addPart("archivo"              , new StringBody(String.valueOf(file.getName()))                    );
            mpEntity.addPart("store_id"             , new StringBody(String.valueOf(media.getStore_id()))               );
            mpEntity.addPart("product_id"           , new StringBody(String.valueOf(media.getProduct_id()))             );
            mpEntity.addPart("poll_id"              , new StringBody(String.valueOf(media.getPoll_id()))                );
            mpEntity.addPart("publicities_id"       , new StringBody(String.valueOf(media.getPublicity_id()))           );
            mpEntity.addPart("category_product_id"  , new StringBody(String.valueOf(media.getCategory_product_id()))    );
            mpEntity.addPart("company_id"           , new StringBody(String.valueOf(media.getCompany_id()))             );
            mpEntity.addPart("tipo"                 , new StringBody(String.valueOf(media.getType()))                   );
            mpEntity.addPart("monto"                , new StringBody(String.valueOf(media.getMonto()))                  );
            mpEntity.addPart("razon_social"         , new StringBody(String.valueOf(media.getRazonSocial()))            );
            mpEntity.addPart("horaSistema"          , new StringBody(String.valueOf(media.getCreated_at()))             );

            URL url = new URL(url_upload_image);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setReadTimeout(20000);
            httpConnection.setConnectTimeout(25000);
            httpConnection.setRequestMethod("POST");
            httpConnection.setUseCaches(false);
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.setRequestProperty("Connection", "Keep-Alive");
            httpConnection.addRequestProperty("Content-length", mpEntity.getContentLength()+"");
            httpConnection.addRequestProperty(mpEntity.getContentType().getName(), mpEntity.getContentType().getValue());
            OutputStream os = httpConnection.getOutputStream();
            mpEntity.writeTo(httpConnection.getOutputStream());
            os.close();
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d(LOG_TAG, "HTTP 200 OK." + httpConnection.getResponseCode()+" "+httpConnection.getResponseMessage()+".");
//                if(file.exists()){
//                    file.delete();
//                }
                return  true ;

            } else {
                Log.d(LOG_TAG, "HTTP "+httpConnection.getResponseCode()+" "+httpConnection.getResponseMessage()+".");
                return  false ;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(LOG_TAG, e.getMessage().toString());
            e.printStackTrace();
            return  false ;
        }finally {
            if(httpConnection != null){
                httpConnection.disconnect();
            }
        }
        //return true;
    }

    /**
     * Envio de fotos, Solicitud sincrónica usando librería AndroidNetworking
     * @param media
     * @return
     */
    public boolean  sendUploadPhotoServer(final Media media ){

        boolean result = false;
        HashMap<String, String> params = new HashMap<>();

        File file = new File(BitmapLoader.getAlbumDirTemp(context).getAbsolutePath() + "/" + media.getFile());

        // params.put("fotoUp"               ,  imgFile                                                            );
        params.put("archivo"              ,  String.valueOf(media.getFile())                );
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

        // Solicitud sincrónica usando librería AndroidNetworking
        ANRequest request = AndroidNetworking.upload(GlobalConstant.dominio + "/insertImagesMayorista")
                .addMultipartFile("fotoUp",file)
                .addMultipartParameter(params)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                });

        ANResponse<JSONObject> response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
            Log.d(LOG_TAG, "response : " + jsonObject.toString());
            Response okHttpResponse = response.getOkHttpResponse();

            Log.d(LOG_TAG, "headers : " + okHttpResponse.headers().toString());
            int success;
            try {
                success = jsonObject.getInt("success");
                if(success==1) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                result = false;
            }
        } else {
            ANError error = response.getError();
            // Handle Error
            result = false;
        }

        return result;
    }


    public boolean insertDataImages(final Media media) {
        boolean result = false;
        HashMap<String, String> params = new HashMap<>();

        File file = new File(BitmapLoader.getAlbumDirTemp(context).getAbsolutePath() + "/" + media.getFile());

        // params.put("fotoUp"               ,  imgFile                                                            );
        params.put("archivo"              ,  String.valueOf(media.getFile())                );
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


//        $product_id = Input::only('product_id');
//        $poll_id = Input::only('poll_id');
//        $store_id = Input::only('store_id');
//        $publicities_id = Input::only('publicities_id');
//        $tipo = Input::only('tipo');
//        $archivo = Input::only('archivo');
//        $company_id = Input::only('company_id');
//        $monto = Input::only('monto');
//        $razon_social = Input::only('razon_social');
//        $category_product_id = Input::only('category_product_id');

        // Solicitud sincrónica usando librería AndroidNetworking
        ANRequest request = AndroidNetworking.upload(GlobalConstant.dominio + "/insertImagesMayorista")
                .addMultipartParameter(params)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                });

        ANResponse<JSONObject> response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
            Log.d(LOG_TAG, "response : " + jsonObject.toString());
            Response okHttpResponse = response.getOkHttpResponse();
            Log.d(LOG_TAG, "headers : " + okHttpResponse.headers().toString());
            result = true;
        } else {
            ANError error = response.getError();
            // Handle Error
            result = false;
        }

        return result;
    }



    /**
     * Cierra una Auditoría determinada, de una tienda determinada en su ruta
     * @param audit_id
     * @param store_id
     * @param company_id
     * @param route_id
     * @return true si se realizó con éxito
     */
    public static boolean closeAuditStore(int audit_id, int store_id, int company_id,int route_id) {
        int success;
        try {
            HashMap<String, String> params = new HashMap<>();

            params.put("audit_id"       , String.valueOf(audit_id)  );
            params.put("store_id"       , String.valueOf(store_id)  );
            params.put("company_id"     , String.valueOf(company_id));
            params.put("rout_id"        , String.valueOf(route_id)  );

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/closeAudit" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());

            // json success, tag que retorna el json

            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                    return true;
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    // return false;
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Obtien lista de productos segun un typo
     * @param company_id
     * @param type Tipo = 1 : competencias
     * @return
     */
    public static ArrayList<Product> getListProductsCompetity(int company_id, int type){
        int success ;

        ArrayList<Product> productsCompetity = new ArrayList<Product>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ajax", String.valueOf("1"));
            params.put("company_id", String.valueOf(company_id));
            params.put("competition", String.valueOf(type));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonRoadsDetail" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/ajaxGetProductsCompetition" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("products");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Product product = new Product();

                            product.setId(obj.getInt("id"));
                            product.setCompany_id(obj.getInt("company_id"));
                            product.setCategory_product_id(obj.getInt("category_product_id"));
                            if(obj.isNull("fullname")) product.setFullname("");  else product.setFullname(obj.getString("fullname"));
                            if(obj.isNull("precio")) product.setPrecio("");  else product.setPrecio(obj.getString("precio"));
                            if(obj.isNull("imagen")) product.setImagen("/media/images/");  else product.setImagen(GlobalConstant.dominio + "/media/images/" + obj.getString("imagen"));;
                            if(obj.isNull("composicion")) product.setComposicion("");  else product.setComposicion(obj.getString("composicion"));
                            if(obj.isNull("fabricante")) product.setFabricante("");  else product.setFabricante(obj.getString("fabricante"));
                            if(obj.isNull("presentacion")) product.setPresentacion("");  else product.setPresentacion(obj.getString("presentacion"));
                            if(obj.isNull("unidad")) product.setUnidad("");  else product.setUnidad(obj.getString("unidad"));
                            product.setType(1);
                            product.setCreated_at(obj.getString("created_at"));
                            product.setUpdated_at(obj.getString("updated_at"));
                            productsCompetity.add(i, product);
                        }

                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return productsCompetity;
    }

    public static ArrayList<Product> getListProducts(int company_id){
        int success ;

        ArrayList<Product> productsCompetity = new ArrayList<Product>();
        try {
            HashMap<String, String> params = new HashMap<>();

            params.put("company_id", String.valueOf(company_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonRoadsDetail" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/admin_api/api_get_products.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("products");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Product product = new Product();

                            product.setId(obj.getInt("id"));
                            product.setCompany_id(obj.getInt("company_id"));
                            product.setCategory_product_id(obj.getInt("category_product_id"));
                            if(obj.isNull("fullname")) product.setFullname("");  else product.setFullname(obj.getString("fullname"));
                            if(obj.isNull("precio")) product.setPrecio("");  else product.setPrecio(obj.getString("precio"));
                            if(obj.isNull("imagen")) product.setImagen("/media/images/");  else product.setImagen(GlobalConstant.URL_PUBLICITY_IMAGES + obj.getString("imagen"));;
                            if(obj.isNull("composicion")) product.setComposicion("");  else product.setComposicion(obj.getString("composicion"));
                            if(obj.isNull("fabricante")) product.setFabricante("");  else product.setFabricante(obj.getString("fabricante"));
                            if(obj.isNull("presentacion")) product.setPresentacion("");  else product.setPresentacion(obj.getString("presentacion"));
                            if(obj.isNull("unidad")) product.setUnidad("");  else product.setUnidad(obj.getString("unidad"));
                            product.setType(1);
                            product.setCreated_at(obj.getString("created_at"));
                            product.setUpdated_at(obj.getString("updated_at"));
                            productsCompetity.add(i, product);
                        }

                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return productsCompetity;
    }
    /**
     * Cierra la ruta en inserta el tiempo de la auditoría
     * @param routeStoreTIme
     * @return true si se realizó con éxito
     */
    public static boolean closeRouteStore(RouteStoreTime routeStoreTIme) {


        int success;
        try {
            HashMap<String, String> paramsData = new HashMap<>();
            paramsData.put("latitud_close"  , String.valueOf(routeStoreTIme.getLat_close()) );
            paramsData.put("longitud_close" , String.valueOf(routeStoreTIme.getLon_close()) );
            paramsData.put("latitud_open"   , String.valueOf(routeStoreTIme.getLat_open())  );
            paramsData.put("longitud_open"  , String.valueOf(routeStoreTIme.getLon_open())  );
            paramsData.put("tiempo_inicio"  , routeStoreTIme.getTime_open()                 );
            paramsData.put("tiempo_fin"     , routeStoreTIme.getTime_close()                );
            paramsData.put("tduser"         , String.valueOf(routeStoreTIme.getUser_id())   );
            paramsData.put("id"             , String.valueOf(routeStoreTIme.getStore_id())  );
            paramsData.put("idruta"         , String.valueOf(routeStoreTIme.getRoute_id())  );
            paramsData.put("company_id"     , String.valueOf(routeStoreTIme.getCompany_id()));

            Log.d("request", "starting");
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json", "POST", paramsData);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/insertaTiempoNew", "POST", paramsData);
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                    return true;
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    /**
     * Iserta la respuest de una pregunta a la tabla PollDetail
     * @param pollDetail
     * @return true si se realizó con éxito
     */
    public static boolean insertPollDetail(PollDetail pollDetail) {
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("poll_id"                , String.valueOf(pollDetail.getPoll_id()));
            params.put("store_id"               , String.valueOf(pollDetail.getStore_id()));
            params.put("sino"                   , String.valueOf(pollDetail.getSino()));
            params.put("options"                , String.valueOf(pollDetail.getOptions()));
            params.put("limits"                 , String.valueOf(pollDetail.getLimits()));
            params.put("media"                  , String.valueOf(pollDetail.getMedia()));
            params.put("coment"                 , String.valueOf(pollDetail.getComment()));
            params.put("result"                 , String.valueOf(pollDetail.getResult()));
            params.put("limite"                 , String.valueOf(pollDetail.getLimite()));
            params.put("comentario"             , String.valueOf(pollDetail.getComentario()));
            params.put("auditor"                , String.valueOf(pollDetail.getAuditor()));
            params.put("product_id"             , String.valueOf(pollDetail.getProduct_id()));
            params.put("publicity_id"           , String.valueOf(pollDetail.getPublicity_id()));
            params.put("company_id"             , String.valueOf(pollDetail.getCompany_id()));
            params.put("category_product_id"    , String.valueOf(pollDetail.getCategory_product_id()));
            params.put("commentOptions"         , String.valueOf(pollDetail.getCommentOptions()));
            params.put("selectedOptions"        , String.valueOf(pollDetail.getSelectdOptions()));
            params.put("selectedOptionsComment" , String.valueOf(pollDetail.getSelectedOtionsComment()));
            params.put("priority"               , String.valueOf(pollDetail.getPriority()));
            params.put("road_id"               , String.valueOf(pollDetail.getRoad_id()));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
//            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/savePollDetailsReg" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/savePollDetailsNew" ,"POST", params);
            // check your log for json response

            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                    return true ;
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cierra todas las auditorias del Store
     * @param store_id
     * @param company_id
     * @return
     */
    public static boolean closeAllAuditRoadStore(int store_id, int company_id) {
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();


            params.put("store_id"               , String.valueOf(store_id));
            params.put("company_id"             , String.valueOf(company_id));


            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_close_audit_road_stores.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success > 0) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                    return true ;
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validate user and password for login
     * @param userName Nombre de Usuario
     * @param password Contraseña del usuario
     * @param imei Imei del dispositivo
     * @return User
     */
    public static User userLogin(String userName, String password , String imei){

        int success ;
        User user = new User();
        try {
            HashMap<String, String> params = new HashMap<>();

            params.put("username", String.valueOf(userName));
            params.put("password", String.valueOf(password));
            params.put("imei", String.valueOf(imei));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/loginMovil" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nulo");
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    user.setId(json.getInt("id"));
                    user.setEmail(userName);
                    user.setFullname(json.getString("fullname"));
                    if(json.isNull("image")) user.setImage("null");  else user.setImage(json.getString("image"));
                    user.setPassword(password);
                }else{
                    Log.d(LOG_TAG, "No se pudo iniciar sesión");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  user;
    }




    /**
     * Obtiene una lista de Rutas
     * @param user_id
     * @param company_id
     * @return
     */
    public static ArrayList<Route> getListRoutes(int user_id, int company_id){
        int success ;

        ArrayList<Route> list = new ArrayList<Route>();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("id", String.valueOf(user_id));
            params.put("company_id", String.valueOf(company_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonRoadsTotal" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success == 1) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("roads");
                    // looping through All Products
                    if(ObjJson.length() > 0) {
                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Route route = new Route();
                            route.setId(Integer.valueOf(obj.getString("id")));
                            route.setFullname(String.valueOf(obj.getString("fullname")));
                            route.setAudit(Integer.valueOf(obj.getString("auditados")));
                            route.setTotal_store(Integer.valueOf(obj.getString("pdvs")));
                            list.add(i,route);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  list;
    }

    /**
     * Obtiene una lista de Stores
     * @param user_id
     * @param company_id
     * @return
     */
    public static ArrayList<Store> getListStores(int user_id, int company_id){
        int success ;

        ArrayList<Store> stores= new ArrayList<Store>();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("user_id", String.valueOf(user_id));
            params.put("company_id", String.valueOf(company_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonRoadsDetail" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_JsonRoadsDetailNoAudits.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("roadsDetail");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Store store = new Store();
                            store.setId(obj.getInt("id"));
                            store.setRoute_id(obj.getInt("road_id"));
                            if(obj.isNull("fullname")) store.setFullname("");  else store.setFullname(obj.getString("fullname"));;
                            if(obj.isNull("cadenaRuc")) store.setCadenRuc("");  else store.setCadenRuc(obj.getString("cadenaRuc"));;
                            if(obj.isNull("documento")) store.setDocument("");  else store.setDocument(obj.getString("documento"));;
                            if(obj.isNull("tipo_documento")) store.setTypo_document("");  else store.setTypo_document(obj.getString("tipo_documento"));;
                            if(obj.isNull("region")) store.setRegion("");  else store.setRegion(obj.getString("region"));;
                            if(obj.isNull("tipo_bodega")) store.setTypeBodega("");  else store.setTypeBodega(obj.getString("tipo_bodega"));
                            if(obj.isNull("address")) store.setAddress("");  else store.setAddress(obj.getString("address"));
                            if(obj.isNull("district")) store.setDistrict("");  else store.setDistrict(obj.getString("district"));
                            store.setStatus(obj.getInt("status"));
                            if(obj.isNull("codclient")) store.setCodCliente("");  else store.setCodCliente(obj.getString("codclient"));
                            if(obj.isNull("urbanization")) store.setUrbanization("");  else store.setUrbanization(obj.getString("urbanization"));
                            if(obj.isNull("type")) store.setType("");  else store.setType(obj.getString("type"));
                            if(obj.isNull("ejecutivo")) store.setEjecutivo("");  else store.setEjecutivo(obj.getString("ejecutivo"));
                            if(obj.isNull("latitude")) store.setLatitude(0.0);  else store.setLatitude(obj.getDouble("latitude"));
                            if(obj.isNull("longitude")) store.setLongitude(0.0);  else store.setLongitude(obj.getDouble("longitude"));
                            if(obj.isNull("telephone")) store.setTelephone("");  else store.setTelephone(obj.getString("telephone"));
                            if(obj.isNull("cell")) store.setCell("");  else store.setCell(obj.getString("cell"));
                            if(obj.isNull("comment")) store.setComment("");  else store.setComment(obj.getString("comment"));
                            if(obj.isNull("owner")) store.setOwner("");  else store.setOwner(obj.getString("owner"));
                            if(obj.isNull("fnac")) store.setFnac("");  else store.setFnac(obj.getString("fnac"));


                            stores.add(i,store);
                        }

                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  stores;
    }



    /**
     * Obtiene el company_id campaña actual
     * @param active
     * @param visible
     * @param app_id
     * @return
     */
    public static Company getCompany(int active, int visible, String app_id){
        int success ;

        Company company = new Company();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("app_id", String.valueOf(app_id));
            params.put("active", String.valueOf(active));
            params.put("visible", String.valueOf(visible));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_company_app.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{


                success = json.getInt("success");
                if (success == 1) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("company");
                    // looping through All Products
                    if(ObjJson.length() > 0) {
                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);

                            company.setId(obj.getInt("id"));
                            company.setFullname(obj.getString("fullname"));
                            company.setActive(obj.getInt("active"));
                            company.setCustomer_id(obj.getInt("customer_id"));
                            company.setVisible(obj.getInt("visible"));
                            company.setAuditory(obj.getInt("auditoria"));
                            company.setLogo(obj.getString("logo"));
                            company.setMarkerPoint(obj.getString("markerPoint"));
                            company.setApp_id(obj.getString("app_id"));
                            company.setCreated_at(obj.getString("created_at"));
                            company.setUpdated_at(obj.getString("updated_at"));
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  company;
    }

    /**
     * Obtiene todas las auditorías de un company_id determinado
     * @param company_id
     * @return
     */
    public static ArrayList<Audit> getAudits(int company_id){
        int success ;

        ArrayList<Audit> audits= new ArrayList<Audit>();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("company_id", String.valueOf(company_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_company_audits.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("audits");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Audit audit = new Audit();
                            audit.setId(obj.getInt("id"));
                            audit.setCompany_audit_id(obj.getInt("company_audit_id"));
                            audit.setCompany_id(obj.getInt("company_id"));
                            if(obj.isNull("fullname")) audit.setFullname("");  else audit.setFullname(obj.getString("fullname"));;
                            audit.setOrden(obj.getInt("orden"));
                            audit.setAudit(obj.getInt("audit"));
                            if(obj.isNull("created_at")) audit.setCreated_at("");  else audit.setCreated_at(obj.getString("created_at"));;
                            if(obj.isNull("updated_at")) audit.setUpdated_at("");  else audit.setUpdated_at(obj.getString("updated_at"));;
                            audits.add(i,audit);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  audits;
    }

    /**
     * Obtiene una lista de todas las auditorias y las rutas de los stores
     * @param company_id
     * @param user_id
     * @return
     */
    public static ArrayList<AuditRoadStore> getAuditRoadStores(int company_id, int user_id){
        int success ;

        ArrayList<AuditRoadStore> auditRoadsStores= new ArrayList<AuditRoadStore>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("company_id", String.valueOf(company_id));
            params.put("user_id", String.valueOf(user_id));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_audit_road_stores.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("audits");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            AuditRoadStore auditRoadStore = new AuditRoadStore();
                            auditRoadStore.setId(obj.getInt("id"));
                            auditRoadStore.setRoad_id(obj.getInt("road_id"));
                            auditRoadStore.setAudit_id(obj.getInt("audit_id"));
                            auditRoadStore.setStore_id(obj.getInt("store_id"));
                            auditRoadStore.setAuditStatus(obj.getInt("audit"));
                            auditRoadStore.setOrder(obj.getInt("orden"));

                            auditRoadsStores.add(i,auditRoadStore);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  auditRoadsStores;
    }

    /**
     * Obtiene una unica Ruta con las auditorias y  stores
     * @param company_id
     * @param road_id
     * @param store_id
     * @return Retorna un Lista de  AuditRoadStore
     */
    public static ArrayList<AuditRoadStore> getAuditRoadStore(int company_id, int road_id, int store_id){
        int success ;

        ArrayList<AuditRoadStore> auditRoadsStores= new ArrayList<AuditRoadStore>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("company_id", String.valueOf(company_id));
            params.put("road_id", String.valueOf(road_id));
            params.put("store_id", String.valueOf(store_id));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_audit_road_store.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("audits");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            AuditRoadStore auditRoadStore = new AuditRoadStore();
                            auditRoadStore.setId(obj.getInt("id"));
                            auditRoadStore.setRoad_id(obj.getInt("road_id"));
                            auditRoadStore.setAudit_id(obj.getInt("audit_id"));
                            auditRoadStore.setStore_id(obj.getInt("store_id"));
                            auditRoadStore.setAuditStatus(obj.getInt("audit"));

                            auditRoadsStores.add(i,auditRoadStore);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  auditRoadsStores;
    }



    public static ArrayList<ProjectionSale> getProjectionSales(int company_id,int store_id){
        boolean success ;

        ArrayList<ProjectionSale> projectionSales= new ArrayList<ProjectionSale>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("company_id", String.valueOf(company_id));
            params.put("store_id", String.valueOf(store_id));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_ttaudit_system + "/api/getProjectionSalesForComapyStore" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getBoolean("success");
                if (success) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("pojection_sales");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            ProjectionSale projectionSale = new ProjectionSale();
                            projectionSale.setId(obj.getInt("id"));
                            projectionSale.setCompany_id(obj.getInt("company_id"));
                            projectionSale.setProduct_id(obj.getInt("product_id"));
                            projectionSale.setStock_min(obj.getInt("stock_min"));


                            projectionSales.add(i,projectionSale);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  projectionSales;
    }
    /**
     * Obitne  todas las preguntas de un company_id
     * @param company_id
     * @return
     */
    public static ArrayList<Poll> getPolls(int company_id){
        int success ;

        ArrayList<Poll> polls= new ArrayList<Poll>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("company_id", String.valueOf(company_id));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_poll.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("polls");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Poll poll = new Poll();
                            poll.setId(obj.getInt("id"));
                            poll.setCompany_audit_id(obj.getInt("company_audit_id"));
                            poll.setCompany_id(obj.getInt("company_id"));
                            poll.setQuestion(obj.getString("question"));
                            if(obj.isNull("question")) poll.setQuestion("");  else poll.setQuestion(obj.getString("question"));
                            poll.setOrder(obj.getInt("orden"));
                            poll.setLimit(obj.getInt("limit"));
                            poll.setSino(obj.getInt("sino"));
                            poll.setOptions(obj.getInt("options"));
                            poll.setOption_type(obj.getInt("option_type"));
                            poll.setMedia(obj.getInt("media"));
                            poll.setMedia_yes_no(obj.getInt("media_yes_no"));
                            poll.setComment(obj.getInt("comment"));
                            poll.setComment_requiered(obj.getInt("comment_requiered"));
                            if(obj.isNull("comentTag")) poll.setComentTag("");  else poll.setComentTag(obj.getString("comentTag"));
                            poll.setComentType(obj.getInt("comentType"));
                            poll.setComment_yes_no(obj.getInt("comment_yes_no"));
                            poll.setPublicity_id(obj.getInt("publicity"));
                            poll.setCategory_product_id(obj.getInt("categoryProduct"));
                            poll.setProduct_id(obj.getInt("product"));
                            if(obj.isNull("created_at")) poll.setCreated_at("");  else poll.setCreated_at(obj.getString("created_at"));
                            if(obj.isNull("updated_at")) poll.setUpdated_at("");  else poll.setUpdated_at(obj.getString("updated_at"));
                            polls.add(i,poll);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  polls;
    }

    public static ArrayList<PollOption> getPollOptions(int company_id){
        int success ;

        ArrayList<PollOption> pollOptions= new ArrayList<PollOption>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("company_id", String.valueOf(company_id));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_poll_option.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("poll_options");
                    // looping through All Products
                    if(ObjJson.length() > 0) {
                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            PollOption pollOption = new PollOption();
                            pollOption.setId(obj.getInt("id"));
                            pollOption.setPoll_id(obj.getInt("poll_id"));
                            pollOption.setCompany_id(obj.getInt("company_id"));
                            if(obj.isNull("options")) pollOption.setOptions("");  else pollOption.setOptions(obj.getString("options"));
                            if(obj.isNull("options_abreviado")) pollOption.setOptions_abreviado("");  else pollOption.setOptions_abreviado(obj.getString("options_abreviado"));
                            if(obj.isNull("codigo")) pollOption.setCodigo("");  else pollOption.setCodigo(obj.getString("codigo"));;
                            pollOption.setProduct_id(obj.getInt("product_id"));
                            if(obj.isNull("region")) pollOption.setRegion("");  else pollOption.setRegion(obj.getString("region"));
                            pollOption.setOption_yes_no(obj.getInt("option_yes_no"));
                            pollOption.setComment(obj.getInt("comment"));
                            if(obj.isNull("comment_tag")) pollOption.setComment_tag("");  else pollOption.setComment_tag(obj.getString("comment_tag"));
                            pollOption.setComment_type(obj.getInt("comment_type"));
                            if(obj.isNull("created_at")) pollOption.setCreated_at("");  else pollOption.setCreated_at(obj.getString("created_at"));;
                            if(obj.isNull("updated_at")) pollOption.setUpdated_at("");  else pollOption.setUpdated_at(obj.getString("updated_at"));;
                            pollOptions.add(i,pollOption);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  pollOptions;
    }

    public static boolean saveLatLongStore(int store_id, double latitude,double longitude ) {

        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("id"             , String.valueOf(store_id));
            params.put("latitud"        , String.valueOf(latitude));
            params.put("longitud"       , String.valueOf(longitude));

            JSONParserX jsonParser = new JSONParserX();
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/updatePositionStore" ,"POST", params);
            Log.d(LOG_TAG, json.toString());

            // json success, tag que retorna el json

            if (json == null) {
                Log.d(LOG_TAG, "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                    return true;
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia la direción de un store
     * @param store_id
     * @param user_id
     * @param company_id
     * @param address
     * @param reference
     * @param userName
     * @param storeName
     * @param comment
     * @return
     */
    public static boolean saveChangeAddressStore(int store_id, int user_id, int company_id, String address, String reference, String userName, String storeName, String comment ) {

        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("store_id"     , String.valueOf(store_id));
            params.put("user_id"      , String.valueOf(user_id));
            params.put("company_id"   , String.valueOf(company_id));
            params.put("direccion"      , String.valueOf(address));
            params.put("referencia"    , String.valueOf(reference));
            params.put("userName"     , String.valueOf(userName));
            params.put("storeName"    , String.valueOf(storeName));
            params.put("comentario"      , String.valueOf(comment));

            JSONParserX jsonParser = new JSONParserX();

            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/changeAddressStore" ,"POST", params);
            Log.d(LOG_TAG, json.toString());

            // json success, tag que retorna el json

            if (json == null) {
                Log.d(LOG_TAG, "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                    return true;
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    public static ArrayList<Publicity> getListPublicities(int company_id){
        int success ;

        ArrayList<Publicity> publicities= new ArrayList<Publicity>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("ajax", String.valueOf("1"));
            params.put("company_id", String.valueOf(company_id));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonRoadsDetail" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/ajaxGetPopBayer" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("publicities");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Publicity publicity = new Publicity();

                            publicity.setId(obj.getInt("id"));
                            publicity.setCompany_id(obj.getInt("company_id"));
                            publicity.setCategory_product_id(1);
                            if(obj.isNull("fullname")) publicity.setFullname("");  else publicity.setFullname(obj.getString("fullname"));;
                            if(obj.isNull("description")) publicity.setDescription("");  else publicity.setDescription(obj.getString("description"));;
                            if(obj.isNull("imagen")) publicity.setImagen("/media/images/");  else publicity.setImagen(obj.getString("imagen"));;
                            publicity.setCreated_at(obj.getString("created_at"));
                            publicity.setUpdated_at(obj.getString("updated_at"));
                            publicities.add(i,publicity);
                        }

                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  publicities;
    }

    public static ArrayList<PublicityStore> getPublicitiesStores(int company_id){
        int success ;

        ArrayList<PublicityStore> publicityStores= new ArrayList<PublicityStore>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("company_id", String.valueOf(company_id));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_publicity_type_bodega.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("publicities");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            PublicityStore publicityStore = new PublicityStore();

                            publicityStore.setId(obj.getInt("id"));
                            publicityStore.setFullname(obj.getString("fullname"));
                            //if(obj.isNull("question")) publicityStore.setQuestion("");  else publicityStore.setQuestion(obj.getString("question"));
                            publicityStore.setPublicity_id(obj.getInt("publicity_id"));
                            publicityStore.setTipo_bodega(obj.getString("tipo_bodega"));
                            publicityStore.setType(obj.getString("type"));
                            publicityStore.setCompany_id(obj.getInt("company_id"));
                            publicityStore.setCategory_product_id(obj.getInt("category_product_id"));
                            publicityStore.setImage(GlobalConstant.URL_PUBLICITY_IMAGES +  obj.getString("imagen"));
                            publicityStore.setCategory_name(obj.getString("category_name"));
                            publicityStore.setActive(0);
                            publicityStores.add(i,publicityStore);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  publicityStores;
    }


    public static boolean saveExhibidorBodegaAlicorp(int store_id, int audit_id, int publicity_id, int category_id, int found , int visible, int contaminated, int status , String comentario, int company_id, int rout_id, int user_id ) {
        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("store_id", String.valueOf(store_id));
            params.put("audit_id", String.valueOf(audit_id));
            params.put("publicity_id", String.valueOf(publicity_id));
            params.put("category_id", String.valueOf(category_id));
            params.put("found", String.valueOf(found));
            params.put("visible", String.valueOf(visible));
            params.put("contaminated", String.valueOf(contaminated));
            params.put("status", String.valueOf(status));
            params.put("comment", String.valueOf(comentario));
            params.put("company_id", String.valueOf(company_id));
            params.put("rout_id", String.valueOf(rout_id));
            params.put("user_id", String.valueOf(user_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/saveExhibidorBodegaAlicorp" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                    return true ;
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveSODBodegaAlicorp(int company_id,int poll_id, int store_id, int audit_id,int rout_id, int user_id,int publicity_id , int result) {
        int success;
        try {
            HashMap<String, String> params = new HashMap<>();

            params.put("poll_id", String.valueOf(poll_id));
            params.put("store_id", String.valueOf(store_id));
            params.put("result", String.valueOf(result));
            params.put("audit_id", String.valueOf(audit_id));
            params.put("company_id", String.valueOf(company_id));
            params.put("rout_id", String.valueOf(rout_id));
            params.put("user_id", String.valueOf(user_id));
            params.put("publicity_id", String.valueOf(publicity_id));


            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/saveSODBodegaAlicorp" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    // return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static ArrayList<CategoryProduct> getListCategoryProduct() {

        int success ;

        ArrayList<CategoryProduct> categoryProducts= new ArrayList<CategoryProduct>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("company_id", String.valueOf(""));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/getCategoryProductsOsaCP" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("categories");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            CategoryProduct categoryProduct = new CategoryProduct();
                            categoryProduct.setId(obj.getInt("id"));
                            if(obj.isNull("fullname")) categoryProduct.setFullname("");  else categoryProduct.setFullname(obj.getString("fullname"));
                            categoryProduct.setStatus(0);
                            categoryProduct.setType(obj.getInt("type"));
                            categoryProduct.setOrden(obj.getInt("orden"));
                            if(obj.isNull("created_at")) categoryProduct.setCreated_at("");  else categoryProduct.setCreated_at(obj.getString("created_at"));
                            if(obj.isNull("updated_at")) categoryProduct.setUpdated_at("");  else categoryProduct.setUpdated_at(obj.getString("updated_at"));
                            categoryProducts.add(i,categoryProduct);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  categoryProducts;

    }


    //SUBIDA DE PHOTOS
    public static void uploadFileNewTest(final Media media, final File imgFile, final Context context) {

        final boolean successOperation = false;
        final ProgressDialog pDialog;
        HashMap<String, String> params = new HashMap<>();

        // params.put("fotoUp"               ,  imgFile                                                            );
        params.put("archivo"              ,  String.valueOf(imgFile.getName())                );
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


        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.text_upload_photo_progress));
        pDialog.setIndeterminate(true);
        pDialog.setMax(100);

        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);

        pDialog.show();

        final File newFileCompresor  ;

        try {
            BitmapLoader.copyFile(imgFile.getPath(), BitmapLoader.getAlbumDirBackup(context).getAbsolutePath() + "/" + imgFile.getName());
            newFileCompresor = BitmapLoader.compresFileDestinationtion(BitmapLoader.getAlbumDirTemp(context).getAbsolutePath(), imgFile,context);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,context.getString(R.string.message_error_compress_file), Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .readTimeout(10000, TimeUnit.MILLISECONDS)
                . writeTimeout(10000, TimeUnit.MILLISECONDS)
                .build();

        AndroidNetworking.upload(GlobalConstant.dominio + "/insertImagesMayorista")
                .addMultipartFile("fotoUp",newFileCompresor)
                .addMultipartParameter(params)
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .setOkHttpClient(okHttpClient)
                //.setExecutor(Executors.newSingleThreadExecutor()) // setting an executor to get response or completion on that executor thread
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        int value = (int) (( bytesUploaded / totalBytes)*100); // (bytesUploaded / totalBytes)*100 + " %");
                        pDialog.setProgress(value);
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // below code will be executed in the executor provided
                        // do anything with response
                        Log.d(LOG_TAG,response.toString());
                        pDialog.dismiss();

                        int success = 0;
                        try {
                            success = response.getInt("success");
                            if (success == 1) {
                                newFileCompresor.delete();
                                imgFile.delete();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context,context.getString(R.string.message_error_upload_file), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        String err = error.getMessage().toString();
                        Toast.makeText(context,context.getString(R.string.message_error_upload_file) + err, Toast.LENGTH_SHORT).show();
                        MediaRepo mediaRepo = new MediaRepo(context) ;
                        mediaRepo.create(media);
//                                Toast.makeText(activity, err,Toast.LENGTH_SHORT);
                        pDialog.dismiss();
                    }
                    @Override
                    protected void finalize() throws Throwable {
//                                notifyItemRangeChanged(position,medias.size());
//                                notifyItemRemoved(position);
                        //  notifyDataSetChanged();
//                                super.finalize();

                        pDialog.dismiss();
                    }
                });
    }

    public static ArrayList<Store> getSearchStores(int company_id, String keyword){
        int success ;

        ArrayList<Store> stores= new ArrayList<Store>();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("keyword", String.valueOf(keyword));
            params.put("company_id", String.valueOf(company_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonRoadsDetail" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_search_stores.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("stores");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Store store = new Store();
                            store.setId(obj.getInt("id"));
                            store.setRoute_id(0);
                            if(obj.isNull("fullname")) store.setFullname("");  else store.setFullname(obj.getString("fullname"));;
                            if(obj.isNull("cadenaRuc")) store.setCadenRuc("");  else store.setCadenRuc(obj.getString("cadenaRuc"));;
                            if(obj.isNull("documento")) store.setDocument("");  else store.setDocument(obj.getString("documento"));;
                            if(obj.isNull("region")) store.setRegion("");  else store.setRegion(obj.getString("region"));;
                            if(obj.isNull("tipo_bodega")) store.setTypeBodega("");  else store.setTypeBodega(obj.getString("tipo_bodega"));
                            if(obj.isNull("address")) store.setAddress("");  else store.setAddress(obj.getString("address"));
                            if(obj.isNull("district")) store.setDistrict("");  else store.setDistrict(obj.getString("district"));
                            store.setStatus(0);
                            if(obj.isNull("codclient")) store.setCodCliente("");  else store.setCodCliente(obj.getString("codclient"));
                            if(obj.isNull("urbanization")) store.setUrbanization("");  else store.setUrbanization(obj.getString("urbanization"));
                            if(obj.isNull("type")) store.setType("");  else store.setType(obj.getString("type"));
                            if(obj.isNull("ejecutivo")) store.setEjecutivo("");  else store.setEjecutivo(obj.getString("ejecutivo"));
                            if(obj.isNull("latitude")) store.setLatitude(0.0);  else store.setLatitude(obj.getDouble("latitude"));
                            if(obj.isNull("longitude")) store.setLongitude(0.0);  else store.setLongitude(obj.getDouble("longitude"));
                            if(obj.isNull("telephone")) store.setTelephone("");  else store.setTelephone(obj.getString("telephone"));
                            if(obj.isNull("cell")) store.setCell("");  else store.setCell(obj.getString("cell"));
                            if(obj.isNull("comment")) store.setComment("");  else store.setComment(obj.getString("comment"));
                            if(obj.isNull("owner")) store.setOwner("");  else store.setOwner(obj.getString("owner"));
                            if(obj.isNull("fnac")) store.setFnac("");  else store.setFnac(obj.getString("fnac"));


                            stores.add(i,store);
                        }

                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  stores;
    }



    public static boolean insertPollDetailAllProduct(PollDetail pollDetail) {

        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("poll_id"                , String.valueOf(pollDetail.getPoll_id()));
            params.put("store_id"               , String.valueOf(pollDetail.getStore_id()));
            params.put("sino"                   , String.valueOf(pollDetail.getSino()));
            params.put("options"                , String.valueOf(pollDetail.getOptions()));
            params.put("limits"                 , String.valueOf(pollDetail.getLimits()));
            params.put("media"                  , String.valueOf(pollDetail.getMedia()));
            params.put("coment"                 , String.valueOf(pollDetail.getComment()));
            params.put("result"                 , String.valueOf(pollDetail.getResult()));
            params.put("limite"                 , String.valueOf(pollDetail.getLimite()));
            params.put("comentario"             , String.valueOf(pollDetail.getComentario()));
            params.put("auditor"                , String.valueOf(pollDetail.getAuditor()));
            params.put("product_id"             , String.valueOf(pollDetail.getProduct_id()));
            params.put("publicity_id"           , String.valueOf(pollDetail.getPublicity_id()));
            params.put("company_id"             , String.valueOf(pollDetail.getCompany_id()));
            params.put("category_product_id"    , String.valueOf(pollDetail.getCategory_product_id()));
            params.put("commentOptions"         , String.valueOf(pollDetail.getCommentOptions()));
            params.put("selectedOptions"        , String.valueOf(pollDetail.getSelectdOptions()));
            params.put("selectedOptionsComment" , String.valueOf(pollDetail.getSelectedOtionsComment()));
            params.put("priority"               , String.valueOf(pollDetail.getPriority()));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/savePollDetailsRegAllProduct" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                    // return true ;
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true ;
    }

    public static ArrayList<Distributor> getListDistributors( int company_id){
        int success ;

        ArrayList<Distributor> distributors= new ArrayList<Distributor>();
        try {

            HashMap<String, String> params = new HashMap<>();


            params.put("company_id", String.valueOf(company_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonRoadsDetail" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_get_distributors_app.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("distibutors");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Distributor distributor = new Distributor();

                            if(obj.isNull("distributor")) distributor.setFullName("");  else distributor.setFullName(obj.getString("distributor"));
                            distributor.setActive(0);
                            distributor.setCode("");
                            distributor.setVendorCode("");
                            distributors.add(i,distributor);
                        }

                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  distributors;
    }


    public static boolean updateStoreVendor(int store_id, String vendorCode) {

        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("store_id"                 , String.valueOf(store_id));
            params.put("codeSell"               , String.valueOf(vendorCode));


            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/updateCodeSellStore" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                    // return true ;
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true ;
    }


    public static int newStore(int route_id,int company_id , Store store, Context context) {
        int success;
        int store_id = 0 ;
        try {



            HashMap<String, String> params = new HashMap<>();

            params.put("company_id"                , String.valueOf(company_id));
            params.put("route_id"                , String.valueOf(route_id));
            params.put("fullname"               , String.valueOf(store.getFullname()));
            params.put("ruc"               , String.valueOf(store.getCadenRuc()));
            params.put("code"                   , String.valueOf(store.getCodCliente()));
//            params.put("distributor"                 , "");
            params.put("distrito"                 , String.valueOf(store.getDistrict()));
            params.put("departamento"                  , String.valueOf(store.getDepartament()));
            params.put("address"                 , String.valueOf(store.getAddress()));
            params.put("phone"                , String.valueOf(store.getTelephone()));
            params.put("ejecutivo"                 , String.valueOf(store.getEjecutivo()));
            params.put("codclient"             , String.valueOf(store.getCodCliente()));
            params.put("giro"             , String.valueOf(store.getGiro()));
            params.put("dex"             , String.valueOf(store.getDex()));
            params.put("contact"             , "");
            params.put("econtact"             , "");
            params.put("pcontact"             , "");


            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/json/prueba.json" ,"POST", params);
//            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/insertPollPalmeras" ,"POST", params);JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_ttaudit_system + "/api/insertNewStore" ,"POST", params);

            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_ttaudit_system + "/api/insertNewStore" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
                return store_id;
            } else{
                success = json.getInt("success");


                if (success == 1) {

                    JSONObject storeObject = json.getJSONObject("store");

                    Store newStore = new Store();

                    newStore.setId(storeObject.getInt("id"));
                    newStore.setRoute_id(route_id);
                    if(storeObject.isNull("fullname")) newStore.setFullname("");  else newStore.setFullname(storeObject.getString("fullname"));
                    if(storeObject.isNull("telephone")) newStore.setTelephone("");  else newStore.setTelephone(storeObject.getString("telephone"));
                    if(storeObject.isNull("district")) newStore.setDistrict("");  else newStore.setDistrict(storeObject.getString("district"));
                    if(storeObject.isNull("latitude")) newStore.setLatitude(0.0);  else newStore.setLatitude(storeObject.getDouble("latitude"));
                    if(storeObject.isNull("longitude")) newStore.setLongitude(0.0);  else newStore.setLongitude(storeObject.getDouble("longitude"));
                    if(storeObject.isNull("region")) newStore.setRegion("");  else newStore.setRegion(storeObject.getString("region"));;
                    if(storeObject.isNull("address")) newStore.setAddress("");  else newStore.setAddress(storeObject.getString("address"));
                    if(storeObject.isNull("ejecutivo")) newStore.setEjecutivo("");  else newStore.setEjecutivo(storeObject.getString("ejecutivo"));


                    StoreRepo storeRepo           = new StoreRepo(context);
                    storeRepo.create(newStore);

                    store = newStore;


                    JSONArray JsonAuditRoadStoresArray = json.getJSONArray("audit_road_stores");
                    for (int i = 0; i < JsonAuditRoadStoresArray.length(); i++) {
                        JSONObject obj = JsonAuditRoadStoresArray.getJSONObject(i);

                        AuditRoadStore auditRoadStore = new AuditRoadStore();
                        auditRoadStore.setId(obj.getInt("id"));
                        auditRoadStore.setRoad_id(obj.getInt("road_id"));
                        auditRoadStore.setAudit_id(obj.getInt("audit_id"));




                        auditRoadStore.setStore_id(obj.getInt("store_id"));
                        auditRoadStore.setAuditStatus(obj.getInt("audit"));
                        auditRoadStore.setOrder(i + 1);


                        AuditRepo auditRepo = new AuditRepo(context);
                        Audit audit = (Audit) auditRepo.findById(auditRoadStore.getAudit_id());
                        auditRoadStore.setList(audit);

                        AuditRoadStoreRepo auditRoadStoreRepo = new AuditRoadStoreRepo(context);
                        auditRoadStoreRepo.create(auditRoadStore);



                    }


                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    // return json.getString("message");
                    // return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return store.getId();
        }
        return store.getId() ;
    }

    /**
     *
     * @param active
     * @param study_id
     * @return
     */
    public static ArrayList<Company> getCompaniesForStudy(int active, int study_id){
        int success ;

        ArrayList<Company> companies = new ArrayList<Company>();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("study_id", String.valueOf(study_id));
            params.put("active", String.valueOf(active));


            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio_dataservicios + "/ttaudit_api/api_get_company_for_study.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{


                success = json.getInt("success");
                if (success > 1) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("company");
                    // looping through All Products
                    if(ObjJson.length() > 0) {
                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);

                            Company company = new Company();
                            company.setId(obj.getInt("id"));
                            company.setFullname(obj.getString("fullname"));
                            company.setActive(obj.getInt("active"));
                            company.setCustomer_id(obj.getInt("customer_id"));
                            company.setVisible(obj.getInt("visible"));
                            company.setAuditory(obj.getInt("auditoria"));
                            company.setLogo(obj.getString("logo"));
                            company.setMarkerPoint(obj.getString("markerPoint"));
                            company.setApp_id(obj.getString("app_id"));
                            company.setMax_photo_notification(obj.getInt("max_photo_notifications"));
                            company.setCreated_at(obj.getString("created_at"));
                            company.setUpdated_at(obj.getString("updated_at"));

                            companies.add(company);


                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  companies;
    }
}
