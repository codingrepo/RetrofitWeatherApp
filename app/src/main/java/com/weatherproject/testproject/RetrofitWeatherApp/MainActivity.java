package com.weatherproject.testproject.RetrofitWeatherApp;


import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weatherproject.testproject.RetrofitWeatherApp.models.Forecast;
import com.weatherproject.testproject.RetrofitWeatherApp.models.JsonWeatherResponse;
import com.weatherproject.testproject.RetrofitWeatherApp.rest.ApiEndpointInterface;
import com.weatherproject.testproject.RetrofitWeatherApp.rest.RestApiClient;
import com.weatherproject.testproject.WeatherAppRetrofit.R;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText etCity;
    Button submitButton;
    ProgressBar progressBar;
    ListView listView;
    CustomListAdapter listAdapter;
    Handler handler;
    public static final String CITY ="city";
    public static final String GSON_OBJ ="gson_obj";
    ArrayList<Forecast> arrayList = new ArrayList<>();
    List<Forecast> forecastData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        etCity = (EditText)findViewById(R.id.editCityName);
        submitButton =(Button) findViewById(R.id.button);
        progressBar =(ProgressBar) findViewById(R.id.progressBar);
        listView =(ListView) findViewById(R.id.listViewData);

        if(savedInstanceState!=null && savedInstanceState.containsKey(CITY)){
            etCity.setText(savedInstanceState.getString(CITY));
        }
        if(savedInstanceState!=null && savedInstanceState.containsKey(GSON_OBJ)){
            Type type = new TypeToken<ArrayList<Forecast>>(){}.getType();
            String jsonString = savedInstanceState.getString(GSON_OBJ, "");
            forecastData = (new Gson()).fromJson(jsonString, type);
            listAdapter = new CustomListAdapter(this, R.layout.weather_data_row, (ArrayList<Forecast>) forecastData);
            listView.setAdapter(listAdapter);

        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = etCity.getText().toString();
                arrayList.clear();
                findWeather(city);

            }
        });
    }

    //Handling app rotation for city name
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String city = etCity.getText().toString();
        outState.putString(CITY, city);

        if(forecastData!=null) {
            Gson gson = new Gson();
            String json = gson.toJson(forecastData);
            outState.putString(GSON_OBJ, json);
            super.onSaveInstanceState(outState);
        }
    }

    //Get data from weather API using retrofit
    public void findWeather(final String city){
        progressBar.setVisibility(View.VISIBLE);
        String unit ="c";

        ApiEndpointInterface apiService =
                RestApiClient.getClient().create(ApiEndpointInterface.class);

        String query = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u ='"+unit+"'", city);
        retrofit2.Call<JsonWeatherResponse> call = apiService.getForecast(query, "json");
        call.enqueue(new Callback<JsonWeatherResponse>() {
            @Override
            public void onResponse(retrofit2.Call<JsonWeatherResponse> call, Response<JsonWeatherResponse> response) {

                forecastData = response.body().getQuery().getResults().getChannel().getItem().getForecast();
                if(forecastData!=null){
                    renderWeather(forecastData);
                }else{
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.place_not_found),
                            Toast.LENGTH_LONG).show();
                }
                Log.d("day is", response.body()+"data");
            }

            @Override
            public void onFailure(retrofit2.Call<JsonWeatherResponse> call, Throwable t) {
                Log.d("error from rest api",t.toString());
            }
        });

    }

    private void renderWeather(List<Forecast> forecastData) {
        progressBar.setVisibility(View.INVISIBLE);
        listAdapter = new CustomListAdapter(this, R.layout.weather_data_row, (ArrayList<Forecast>) forecastData);
        listView.setAdapter(listAdapter);
    }
}
