package com.example.coolweather.activity;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.R;
import com.example.coolweather.db.CoolWeatherDB;
import com.example.coolweather.model.City;
import com.example.coolweather.model.Province;
import com.example.coolweather.model.Country;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

public class ChooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countyList;
    private Province selectedProvince;
    private City selectedCity;
    /**
     * 瑜版挸澧犻柅澶夎厬閻ㄥ嫮楠囬崚锟�
     */
    private int currentLevel;
    /**
     * 閺勵垰鎯佹禒宥筫atherActivity娑擃叀鐑︽潪顒冪箖閺夈儯锟斤拷
     */
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AdManager.getInstance(this).init("cf9c2a749cd97145","289874826c698edd", false);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index,
                                    long arg3) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(index);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(index);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String countyCode = countyList.get(index).getCountryCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();  // 閸旂姾娴囬惇浣洪獓閺佺増宓�
    }

    /**
     * 閺屻儴顕楅崗銊ユ禇閹碉拷閺堝娈戦惇渚婄礉娴兼ê鍘涙禒搴㈡殶閹诡喖绨遍弻銉嚄閿涘苯顩ч弸婊勭梾閺堝鐓＄拠銏犲煂閸愬秴骞撻張宥呭閸ｃ劋绗傞弻銉嚄閵嗭拷
     */
    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /**
     * 閺屻儴顕楅柅澶夎厬閻礁鍞撮幍锟介張澶屾畱鐢偊绱濇导妯哄帥娴犲孩鏆熼幑顔肩氨閺屻儴顕楅敍灞筋洤閺嬫粍鐥呴張澶嬬叀鐠囥垹鍩岄崘宥呭箵閺堝秴濮熼崳銊ょ瑐閺屻儴顕楅妴锟�
     */
    private void queryCities() {
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 閺屻儴顕楅柅澶夎厬鐢倸鍞撮幍锟介張澶屾畱閸樺尅绱濇导妯哄帥娴犲孩鏆熼幑顔肩氨閺屻儴顕楅敍灞筋洤閺嬫粍鐥呴張澶嬬叀鐠囥垹鍩岄崘宥呭箵閺堝秴濮熼崳銊ょ瑐閺屻儴顕楅妴锟�
     */
    private void queryCounties() {
        countyList = coolWeatherDB.loadCountries(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (Country country : countyList) {
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     * 閺嶈宓佹导鐘插弳閻ㄥ嫪鍞崣宄版嫲缁鐎锋禒搴㈡箛閸斺€虫珤娑撳﹥鐓＄拠銏㈡阜鐢倸骞欓弫鐗堝祦閵嗭拷
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(coolWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountriesResponse(coolWeatherDB, response, selectedCity.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // 闁俺绻價unOnUiThread()閺傝纭堕崶鐐插煂娑撹崵鍤庣粙瀣槱閻炲棝锟芥槒绶�
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,
                                "获取数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 閺勫墽銇氭潻娑樺鐎电鐦藉锟�
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在请求数据...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 閸忔娊妫存潻娑樺鐎电鐦藉锟�
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 閹规洝骞廈ack閹稿鏁敍灞剧壌閹诡喖缍嬮崜宥囨畱缁狙冨焼閺夈儱鍨介弬顓ㄧ礉濮濄倖妞傛惔鏃囶嚉鏉╂柨娲栫敮鍌氬灙鐞涖劊锟戒胶娓烽崚妤勩€冮妴浣界箷閺勵垳娲块幒銉╋拷锟介崙鎭掞拷锟�
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

}
