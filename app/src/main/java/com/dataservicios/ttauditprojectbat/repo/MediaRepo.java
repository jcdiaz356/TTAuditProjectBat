package com.dataservicios.ttauditprojectbat.repo;

import android.content.Context;

import com.dataservicios.ttauditprojectbat.db.DatabaseHelper;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Media;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by jcdia on 31/05/2017.
 */

public class MediaRepo implements Crud {
    private DatabaseHelper helper;

    public MediaRepo(Context context) {

        DatabaseManager.init(context);
        helper = DatabaseManager.getInstance().getHelper();
    }

    @Override
    public int create(Object item) {
        int index = -1;
        Media object = (Media) item;
        try {
            index = helper.getMediaDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
    }


    @Override
    public int update(Object item) {

        int index = -1;

        Media object = (Media) item;

        try {
            helper.getMediaDao().update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }


    @Override
    public int delete(Object item) {

        int index = -1;

        Media object = (Media) item;

        try {
            helper.getMediaDao().delete(object);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }

    @Override
    public int deleteAll() {

        List<Media> items = null;
        int counter = 0;
        try {
            items = helper.getMediaDao().queryForAll();

            for (Media object : items) {
                // do something with object
                helper.getMediaDao().deleteById(object.getId());
                counter ++ ;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }


    @Override
    public Object findById(int id) {

        Media wishList = null;
        try {
            wishList = helper.getMediaDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }


    @Override
    public List<?> findAll() {

        List<Media> items = null;

        try {
            items = helper.getMediaDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;

    }

    /**
     * Obtiene el primer registro encontrado en la base de datos
     * @return
     */
    @Override
    public Object findFirstReg() {

        Object wishList = null;
        try {
            wishList = helper.getMediaDao().queryBuilder().queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }

    @Override
    public long countReg() {
        long count = 0;
        try {
            count = helper.getMediaDao().countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }


    public List<Media> findByCompanyId(int company_id) {

        List<Media> wishList = null;
        try {
            wishList = helper.getMediaDao().queryBuilder().where().eq("company_id",company_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }


    public Object findFirstActive() {

        Object wishList = null;
        try {
            wishList = helper.getMediaDao().queryBuilder().where().eq("status","0").queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }
}