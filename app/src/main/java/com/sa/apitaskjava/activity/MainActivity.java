package com.sa.apitaskjava.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbb20.CountryCodePicker;
import com.sa.apitaskjava.Adapter.Adapter;
import com.sa.apitaskjava.R;
import com.sa.apitaskjava.Utils.ApiController;
import com.sa.apitaskjava.models.ModelClass;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    CountryCodePicker countryCodePicker;
    TextView mTodayTotal,mTotal,mActive,mTodayActive,mRecovered,mTodayRecovered,mDeaths,mTodayDeaths;

    String country;
    TextView mFilter;
    Spinner spinner;
    String[] types = {"cases","deaths","recovered","active"};
    private List<ModelClass> modelClassList;
    private List<ModelClass> modelClassList2;
    PieChart mPieChart;
    private RecyclerView recyclerView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        countryCodePicker = findViewById(R.id.ccp);
        mTodayActive = findViewById(R.id.todayactive);
        mActive = findViewById(R.id.activecases);
        mDeaths = findViewById(R.id.totaldeath);
        mTodayDeaths = findViewById(R.id.todaydeath);
        mRecovered = findViewById(R.id.recoveredcase);
        mTodayRecovered = findViewById(R.id.todayrecovered);
        mTotal = findViewById(R.id.totalcase);
        mTodayTotal = findViewById(R.id.todaytotal);
        mPieChart = findViewById(R.id.piechart);
        spinner = findViewById(R.id.spinner);
        mFilter = findViewById(R.id.filter);
        recyclerView = findViewById(R.id.recyclerview);

        modelClassList = new ArrayList<>();
        modelClassList2 = new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        ApiController.getAPIInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter = new Adapter(getApplication(),modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country = countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country = countryCodePicker.getSelectedCountryName();
                fetchData();
            }
        });

        fetchData();
    }

    private void fetchData() {
        ApiController.getAPIInterface()
                .getCountryData()
                .enqueue(new Callback<List<ModelClass>>() {
                    @Override
                    public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                        modelClassList.addAll(response.body());
                        for(int i=0;i<modelClassList.size();i++)
                        {
                            if(modelClassList.get(i).getCountry().equals(country))
                            {
                                mActive.setText((modelClassList.get(i).getActive()));
                                mTodayDeaths.setText((modelClassList.get(i).getTodayDeaths()));
                                mTodayRecovered.setText((modelClassList.get(i).getTodayRecovered()));
                                mTodayTotal.setText((modelClassList.get(i).getTodayCases()));
                                mTotal.setText((modelClassList.get(i).getCases()));
                                mDeaths.setText((modelClassList.get(i).getDeaths()));
                                mRecovered.setText((modelClassList.get(i).getRecovered()));

                                int active, total, recovered, deaths;
                                active = Integer.parseInt(modelClassList.get(i).getActive());
                                total = Integer.parseInt(modelClassList.get(i).getCases());
                                recovered = Integer.parseInt(modelClassList.get(i).getRecovered());
                                deaths = Integer.parseInt(modelClassList.get(i).getDeaths());
                                
                                updateGraph(active,total,recovered,deaths);

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ModelClass>> call, Throwable t) {

                    }
                });

    }

    private void updateGraph(int active, int total, int recovered, int deaths) {
        mPieChart.clearChart();
        int confirmColor = ContextCompat.getColor(getApplicationContext(), R.color.yellow);
        int activeColor = ContextCompat.getColor(getApplicationContext(), R.color.green);
        int recoveredColor = ContextCompat.getColor(getApplicationContext(), R.color.blue);
        int deathColor = ContextCompat.getColor(getApplicationContext(), R.color.red);
        mPieChart.addPieSlice(new PieModel("Confirm",total, confirmColor));
        mPieChart.addPieSlice(new PieModel("Active",active, activeColor));
        mPieChart.addPieSlice(new PieModel("Recovered",recovered, recoveredColor));
        mPieChart.addPieSlice(new PieModel("Deaths",deaths, deathColor));
        mPieChart.startAnimation();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = types[i];
        mFilter.setText(item);
        adapter.filter(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}