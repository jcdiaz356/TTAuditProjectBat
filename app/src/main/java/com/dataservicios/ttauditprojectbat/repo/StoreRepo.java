package com.dataservicios.ttauditprojectbat.repo;

import android.content.Context;

import com.dataservicios.ttauditprojectbat.db.DatabaseHelper;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Store;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by jcdia on 13/05/2017.
 */

public class StoreRepo implements Crud {
    private DatabaseHelper helper;

    public StoreRepo(Context context) {

        DatabaseManager.init(context);
        helper = DatabaseManager.getInstance().getHelper();
    }

    @Override
    public int create(Object item) {
        int index = -1;
        Store object = (Store) item;
        try {
            index = helper.getStoreDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
    }


    @Override
    public int update(Object item) {

        int index = -1;

        Store object = (Store) item;

        try {
            helper.getStoreDao().update(object);
           // helper.getStoreDao().upda(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }

    public int updateAddressStoreAndRoute(int store_id, int route_id, String address, String urbanization) {

        int index = -1;


        try {
            UpdateBuilder<Store, Integer> updateBuilder = helper.getStoreDao().updateBuilder();
            updateBuilder.where().eq("id",store_id).and().eq("route_id",route_id);
            updateBuilder.updateColumnValue("address",address);
            updateBuilder.updateColumnValue("urbanization",urbanization);
            index = updateBuilder.update();

            // helper.getStoreDao().upda(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }

    public int updateStatusForStorAndRouteId(int store_id, int route_id,int status) {


        int counter = 0;
        // Store object = (Store) item;


        try {

            UpdateBuilder<Store, Integer> updateBuilder = helper.getStoreDao().updateBuilder();
            updateBuilder.where().eq("id",store_id).and().eq("route_id",route_id);
            updateBuilder.updateColumnValue("status",status);
            counter = updateBuilder.update();

            // helper.getStoreDao().upda(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return counter;
    }




    @Override
    public int delete(Object item) {

        int index = -1;

        Store object = (Store) item;

        try {
            helper.getStoreDao().delete(object);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }

    @Override
    public int deleteAll() {

        List<Store> items = null;
        int counter = 0;
        try {
            //items = helper.getStoreDao().queryForAll();
            long count  = helper.getStoreDao().countOf();
            if (count == 0) {
                return (int) count;
            }

//            for (Store object : items) {
//                // do something with object
//               // helper.getStoreDao().deleteById(object.getId());
//               // helper.getStoreDao().delete(object);
//
//                helper.getStoreDao().deleteBuilder().where().eq("id",object.getId());
//                helper.getStoreDao().de
//
//
//                //helper.getStoreDao().dele;
//                counter ++ ;
//            }



            DeleteBuilder<Store, Integer> deleteBuilder = helper.getStoreDao().deleteBuilder();
            deleteBuilder.where().isNotNull("id");
            counter = deleteBuilder.delete();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }


    @Override
    public Object findById(int id) {

        Store wishList = null;
        try {
            wishList = helper.getStoreDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }

    public Object findByIdAndRouteId(int id,int route_id) {

        Store wishList = null;
        try {
            wishList =  helper.getStoreDao().queryBuilder().where().eq("id",id).and().eq("route_id",route_id).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }


    @Override
    public List<?> findAll() {

        List<Store> items = null;

        try {
            items = helper.getStoreDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;

    }

    @Override
    public Object findFirstReg() {

        Object wishList = null;
        try {
            wishList = helper.getStoreDao().queryBuilder().queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }

    @Override
    public long countReg() {
        long count = 0;
        try {
            count = helper.getStoreDao().countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Busca una lista de Stores por su Route
     * @param route_id
     * @return Retorna lista de stores
     */
    public List<Store> findByRouteId(int route_id) {

        List<Store> wishList = null;
        try {
            wishList = helper.getStoreDao().queryBuilder().where().eq("route_id",route_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }


    public int updateGeo(Store store, int route_id) {


        int counter = 0;
        // Store object = (Store) item;


        try {

            UpdateBuilder<Store, Integer> updateBuilder = helper.getStoreDao().updateBuilder();
            updateBuilder.where().eq("id",store.getId()).and().eq("route_id",route_id);
            updateBuilder.updateColumnValue("latitude",store.getLatitude());
            updateBuilder.updateColumnValue("longitude",store.getLongitude());
            updateBuilder.updateColumnValue("update_geo",store.getUpdate_geo());
            counter = updateBuilder.update();

            // helper.getStoreDao().upda(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return counter;
    }

}