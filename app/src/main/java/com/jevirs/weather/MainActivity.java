package com.jevirs.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonFloat;
import com.google.gson.Gson;
import com.jevirs.weather.Json.Index;
import com.jevirs.weather.Json.JsonBean;
import com.jevirs.weather.Json.Results;
import com.jevirs.weather.Json.Weather_data;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    public String thecity;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    private ListView listView;
    private static final String[] strings={"城市管理","设置","关于","欢迎使用"};
    private static final int REQUEST_CODE=33;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState==null) {
            setContentView(R.layout.activity_main);
            handler = new Handler();
            initView();
        }
        else {
            TextView tv1=(TextView) findViewById(R.id.city);
            tv1.setText(savedInstanceState.getString("city","hehe"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (thecity!=null) {
            outState.putString("city", findViewById(R.id.city).toString());
            outState.putString("pm25", findViewById(R.id.pm25).toString());
            outState.putString("date1", findViewById(R.id.date1).toString());
            outState.putString("weather1", findViewById(R.id.weather1).toString());
            outState.putString("tem1", findViewById(R.id.tem1).toString());
            outState.putString("wind1", findViewById(R.id.wind1).toString());
            outState.putString("date2", findViewById(R.id.date2).toString());
            outState.putString("weather2", findViewById(R.id.weather2).toString());
            outState.putString("tem2", findViewById(R.id.tem2).toString());
            outState.putString("wind2", findViewById(R.id.wind2).toString());
            outState.putString("date3", findViewById(R.id.date3).toString());
            outState.putString("weather3", findViewById(R.id.weather3).toString());
            outState.putString("tem3", findViewById(R.id.tem3).toString());
            outState.putString("wind3", findViewById(R.id.wind3).toString());
            outState.putString("date4", findViewById(R.id.date4).toString());
            outState.putString("weather4", findViewById(R.id.weather4).toString());
            outState.putString("tem4", findViewById(R.id.tem4).toString());
            outState.putString("wind4", findViewById(R.id.wind4).toString());
            super.onSaveInstanceState(outState);
        }
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        ButtonFloat fab= (ButtonFloat) findViewById(R.id.fab);
        Resources resources=getApplicationContext().getResources();
        Drawable drawable=resources.getDrawable(R.drawable.ic_add_white_24dp);
        fab.setIconDrawable(drawable);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(),ManageActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (isOnline()){
                            int num=39;
                            getData(thecity);
                            SharedPreferences sharedPreferences=getSharedPreferences(thecity,MODE_PRIVATE);
                            do {
                                num=sharedPreferences.getInt("error",38);
                            }while (num==38);

                            if (num==0) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showData(thecity);
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }, 1000);
                            }else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"木有该城市",Toast.LENGTH_SHORT).show();
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                });
                            }
                        }else {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(getApplicationContext(), "木有网啊╮(╯▽╰)╭", Toast.LENGTH_SHORT).show();
                                }
                            },1000);
                        }
                    }
                }).start();
            }
        });

        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.hello_world, R.string.hello_world);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        listView= (ListView) findViewById(R.id.list);
        ArrayAdapter arrayAdapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent=new Intent(getApplicationContext(),ManageActivity.class);
                        startActivityForResult(intent,REQUEST_CODE);
                        break;
                    case 1:
                        Intent intent1=new Intent(getApplicationContext(),SettingsActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2=new Intent(getApplicationContext(),AboutActivity.class);
                        startActivity(intent2);
                        break;
                    default:
                        drawerLayout.closeDrawers();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            Bundle bundle=data.getExtras();
            String string=bundle.getString("add_city","上海");
            thecity=string;
        }
    }

    private void getData(final String city2query){
        Log.e("getData","GETDATA");
        String url="http://api.map.baidu.com/telematics/v3/weather?location="+decode(city2query)+"&output=json&ak=So1v0MPGI6v0bcL3yXVhAKl9";
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson=new Gson();
                JsonBean jsonBean=gson.fromJson(String.valueOf(response),JsonBean.class);

                int error= Integer.parseInt(jsonBean.getError());    //错误信息
                String status=jsonBean.getStatus();   //信息状态
                String date=jsonBean.getDate();      //信息时间
                Log.e("request",String.valueOf(error));

                if (error==0) {
                    List<Results> list_results = jsonBean.getResults();
                    Results results = list_results.get(0);
                    List<Index> index = results.getIndex();

                    String currentCity = results.getCurrentCity();   //城市
                    String pm25 = results.getPm25();                 //pm2.5

                    Index cloth = index.get(0);     //穿衣指数
                    String cloth_title = cloth.getTitle();
                    String cloth_zs = cloth.getZs();
                    String cloth_tipt = cloth.getTipt();
                    String cloth_des = cloth.getDes();

                    Index car = index.get(1);   //洗车指数
                    String car_title = car.getTitle();
                    String car_zs = car.getZs();
                    String car_tipt = car.getTipt();
                    String car_des = car.getDes();

                    Index travel = index.get(2);    //旅游指数
                    String travel_title = travel.getTitle();
                    String travel_zs = travel.getZs();
                    String travel_tipt = travel.getTipt();
                    String travel_des = travel.getDes();

                    Index ill = index.get(3);    //感冒指数
                    String ill_title = ill.getTitle();
                    String ill_zs = ill.getZs();
                    String ill_tipt = ill.getTipt();
                    String ill_des = ill.getDes();

                    Index sports = index.get(4);    //运动指数
                    String sports_title = sports.getTitle();
                    String sports_zs = sports.getZs();
                    String sports_tipt = sports.getTipt();
                    String sports_des = sports.getDes();

                    Index light = index.get(5);      //紫外线指数
                    String light_title = light.getTitle();
                    String light_zs = light.getZs();
                    String light_tipt = light.getTipt();
                    String light_des = light.getDes();

                    List<Weather_data> list_weather = results.getWeatherData();
                    Weather_data weather_1 = list_weather.get(0);    //今日天气
                    String weather_1_date = weather_1.getDate();
                    String weather_1_weather = weather_1.getWeather();
                    String weather_1_wind = weather_1.getWind();
                    String weather_1_tem = weather_1.getTemperature();

                    Weather_data weather_2 = list_weather.get(1);    //明日天气
                    String weather_2_date = weather_2.getDate();
                    String weather_2_weather = weather_2.getWeather();
                    String weather_2_wind = weather_2.getWind();
                    String weather_2_tem = weather_2.getTemperature();

                    Weather_data weather_3 = list_weather.get(2);      //后天
                    String weather_3_date = weather_3.getDate();
                    String weather_3_weather = weather_3.getWeather();
                    String weather_3_wind = weather_3.getWind();
                    String weather_3_tem = weather_3.getTemperature();

                    Weather_data weather_4 = list_weather.get(3);    //大后天
                    String weather_4_date = weather_4.getDate();
                    String weather_4_weather = weather_4.getWeather();
                    String weather_4_wind = weather_4.getWind();
                    String weather_4_tem = weather_4.getTemperature();


                    SharedPreferences.Editor editor = getSharedPreferences(currentCity, MODE_PRIVATE).edit();
                    editor.putInt("error", error);
                    editor.putString("status", status);
                    editor.putString("date", date);
                    editor.putString("city", currentCity);
                    editor.putString("pm25", pm25);

                    editor.putString("weather1", weather_1_weather);
                    editor.putString("tem1", weather_1_tem);
                    editor.putString("date1", weather_1_date);
                    editor.putString("wind1", weather_1_wind);

                    editor.putString("weather2", weather_2_weather);
                    editor.putString("tem2", weather_2_tem);
                    editor.putString("date2", weather_2_date);
                    editor.putString("wind2", weather_2_wind);

                    editor.putString("weather3", weather_3_weather);
                    editor.putString("tem3", weather_3_tem);
                    editor.putString("date3", weather_3_date);
                    editor.putString("wind3", weather_3_wind);

                    editor.putString("weather4", weather_4_weather);
                    editor.putString("tem4", weather_4_tem);
                    editor.putString("date4", weather_4_date);
                    editor.putString("wind4", weather_4_wind);

                    editor.putString("cloth", cloth_des);
                    editor.putString("sports", sports_des);
                    editor.putString("light", light_des);
                    editor.apply();
                }
                if (error==-3){
                    SharedPreferences.Editor editor = getSharedPreferences(city2query, MODE_PRIVATE).edit();
                    editor.putInt("error", error);
                    editor.apply();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse",error.getMessage());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 0.1f));
        requestQueue.getCache().clear();
        requestQueue.add(jsonObjectRequest);
        requestQueue.start();
    }

    private void showData(String city2show){
        Log.e("showData","showDate");

        TextView city= (TextView) findViewById(R.id.currentcity);
        TextView pm25= (TextView) findViewById(R.id.pm25);
        TextView date1= (TextView) findViewById(R.id.date1);
        TextView date2= (TextView) findViewById(R.id.date2);
        TextView date3= (TextView) findViewById(R.id.date3);
        TextView date4= (TextView) findViewById(R.id.date4);
        TextView tem1= (TextView) findViewById(R.id.tem1);
        TextView tem2= (TextView) findViewById(R.id.tem2);
        TextView tem3= (TextView) findViewById(R.id.tem3);
        TextView tem4= (TextView) findViewById(R.id.tem4);
        TextView weather1= (TextView) findViewById(R.id.weather1);
        TextView weather2= (TextView) findViewById(R.id.weather2);
        TextView weather3= (TextView) findViewById(R.id.weather3);
        TextView weather4= (TextView) findViewById(R.id.weather4);
        TextView wind1= (TextView) findViewById(R.id.wind1);
        TextView wind2= (TextView) findViewById(R.id.wind2);
        TextView wind3= (TextView) findViewById(R.id.wind3);
        TextView wind4= (TextView) findViewById(R.id.wind4);

        SharedPreferences preferences=getSharedPreferences(city2show,MODE_PRIVATE);
        String strcity=preferences.getString("city","城市");
        String strpm25=preferences.getString("pm25","PM25");
        String strweather1=preferences.getString("weather1","weather1");
        String strweather2=preferences.getString("weather1","weather1");
        String strweather3=preferences.getString("weather1","weather1");
        String strweather4=preferences.getString("weather1","weather1");
        String strtem1=preferences.getString("tem1","tem1");
        String strtem2=preferences.getString("tem2","tem2");
        String strtem3=preferences.getString("tem3","tem3");
        String strtem4=preferences.getString("tem4","tem4");
        String strdate1=preferences.getString("date1","date1");
        String strdate2=preferences.getString("date2","date2");
        String strdate3=preferences.getString("date3","date3");
        String strdate4=preferences.getString("date4","date4");
        String strwind1=preferences.getString("wind1","wind1");
        String strwind2=preferences.getString("wind2","wind2");
        String strwind3=preferences.getString("wind3","wind3");
        String strwind4=preferences.getString("wind4","wind4");

        city.setText(strcity);
        pm25.setText(strpm25);
        date1.setText(strdate1);
        date2.setText(strdate2);
        date3.setText(strdate3);
        date4.setText(strdate4);
        tem1.setText(strtem1);
        tem2.setText(strtem2);
        tem3.setText(strtem3);
        tem4.setText(strtem4);
        weather1.setText(strweather1);
        weather2.setText(strweather2);
        weather3.setText(strweather3);
        weather4.setText(strweather4);
        wind1.setText(strwind1);
        wind2.setText(strwind2);
        wind3.setText(strwind3);
        wind4.setText(strwind4);
    }

    public String decode(String city2decode) {
        Log.e("code","code");
        String city_decoded= null;
        try {
            city_decoded = URLDecoder.decode(city2decode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return city_decoded;
    }

    public boolean isOnline() {
        Log.e("isonline","isonline");
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
