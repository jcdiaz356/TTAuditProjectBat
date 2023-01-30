package com.dataservicios.ttauditprojectbat.repo;

import android.content.Context;

import com.dataservicios.ttauditprojectbat.db.DatabaseHelper;
import com.dataservicios.ttauditprojectbat.db.DatabaseManager;
import com.dataservicios.ttauditprojectbat.model.Publicity;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by jcdia on 4/06/2017.
 */

public class PublicityRepo implements Crud {
    private DatabaseHelper helper;

    public PublicityRepo(Context context) {

        DatabaseManager.init(context);
        helper = DatabaseManager.getInstance().getHelper();
    }

    @Override
    public int create(Object item) {
        int index = -1;
        Publicity object = (Publicity) item;
        try {
            index = helper.getPublicityDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
    }


    @Override
    public int update(Object item) {

        int index = -1;

        Publicity object = (Publicity) item;

        try {
            helper.getPublicityDao().update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }


    @Override
    public int delete(Object item) {

        int index = -1;

        Publicity object = (Publicity) item;

        try {
            helper.getPublicityDao().delete(object);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }

    @Override
    public int deleteAll() {

        List<Publicity> items = null;
        int counter = 0;
        try {
            items = helper.getPublicityDao().queryForAll();

            for (Publicity object : items) {
                // do something with object
                helper.getPublicityDao().deleteById(object.getId());
                counter ++ ;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }


    @Override
    public Object findById(int id) {

        Publicity wishList = null;
        try {
            wishList = helper.getPublicityDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }


    @Override
    public List<?> findAll() {

        List<Publicity> items = null;

        try {
            items = helper.getPublicityDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;

    }

    @Override
    public Object findFirstReg() {

        Object wishList = null;
        try {
            wishList = helper.getPublicityDao().queryBuilder().queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }

    @Override
    public long countReg() {
        long count = 0;
        try {
            count = helper.getPublicityDao().countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }


    /**
     * Busca una lista de Publicity por su company_id
     * @param company_id
     * @return Retorna lista de Products
     */
    public List<Publicity> findByCompanyId(int company_id) {

        List<Publicity> wishList = null;
        try {
            wishList = helper.getPublicityDao().queryBuilder().where().eq("company_id",company_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }

}