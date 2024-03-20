package com.example.coolweather.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.coolweather.model.City;
import com.example.coolweather.model.Country;
import com.example.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

public class CoolWeatherDB {
    //数据库名
    public static final String DB_NAME = "cool_weather";
    //数据库版本
    public static final int VERSION = 1;

    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    //构造方法私有化
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }
    //获取CoolWeatherDB的实例
    public synchronized static CoolWeatherDB getInstance(Context context){
        if (coolWeatherDB == null){
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }
    //将Province实例存储到数据库
    public void saveProvince(Province province){
        Log.e("排错","将Province实例存储到数据库");
        if (province != null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
            Log.e("排错","Province实例存储到数据库成功");
        }
    }
    //从数据库读取全国所有省份信息
    @SuppressLint("Range")
    public List<Province> loadProvinces(){
        Log.e("排错","从数据库读取全国所有省份信息");
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while (cursor.moveToNext());
            Log.e("排错","从数据库读取全国所有省份信息成功，共"+list.size()+"条数据");
        }
        return list;

    }
    //将City实例存储到数据库
    public void savaCity(City city){
        Log.e("排错","将City实例存储到数据库");
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
            Log.e("排错","City实例存储到数据库成功");
        }

    }
    //从数据库读取省份所有市信息
    @SuppressLint("Range")
    public List<City> loadCities(int provinceId){
        Log.e("排错","数据库查询市信息");
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City",null,"province_id = ?",new String[]{String.valueOf(provinceId)},null,null,null);
        if (cursor.moveToFirst()){
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            }while (cursor.moveToNext());
            Log.e("排错","查询市信息成功，共"+list.size()+"条数据");
        }
        return list;
    }
    //将Country实例存储到数据库
    public void saveCountry(Country country){
        Log.e("排错","将Country实例存储到数据库");
        if (country != null){
            ContentValues values = new ContentValues();
            values.put("country_name",country.getCountryName());
            values.put("country_code",country.getCountryCode());
            values.put("city_id",country.getCityId());
            db.insert("Country",null,values);
            Log.e("排错","Country实例存储到数据库成功");
        }

    }
    //从数据库读取区县所有天气编码信息
    @SuppressLint("Range")
    public List<Country> loadCountries(int cityId){
        Log.e("排错","从数据库读取区县所有天气编码信息");
        List<Country> list = new ArrayList<Country>();
        Cursor cursor = db.query("Country",null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null,null);
        if (cursor.moveToFirst()){
            do {
                Country country = new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
                country.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(country);
            }while (cursor.moveToNext());
            Log.e("排错","读取区县所有天气编码信息成功，共"+list.size()+"条数据");
        }
        return list;
    }
}
