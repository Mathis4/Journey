package com.example.journey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RankingsActivity extends AppCompatActivity {

    private ListView rankingsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        rankingsListView = findViewById(R.id.rankings_list_view);
        refreshAndSortRankingsDataList();

        Button myMapButton = findViewById(R.id.btn_my_map2);

        myMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAndSortRankingsDataList();
    }

    private void refreshAndSortRankingsDataList() {
        SharedPreferences prefs = getSharedPreferences("RankingsData", MODE_PRIVATE);
        List<CountryData> rankingsDataList = new ArrayList<>();

        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().endsWith("_note")) {
                String country = entry.getKey().replace("_note", "");
                String note = prefs.getString(country + "_note", "");
                float rating = prefs.getFloat(country + "_rating", 0.0f);
                rankingsDataList.add(new CountryData(country, note, rating));
            }
        }

        Collections.sort(rankingsDataList, new Comparator<CountryData>() {
            @Override
            public int compare(CountryData countryData1, CountryData countryData2) {
                return Float.compare(countryData2.getRating(), countryData1.getRating());
            }
        });

        CustomRankingsAdapter adapter = new CustomRankingsAdapter(this, rankingsDataList);
        rankingsListView.setAdapter(adapter);
    }

    private static class CountryData {
        private String country;
        private String note;
        private float rating;

        public CountryData(String country, String note, float rating) {
            this.country = country;
            this.note = note;
            this.rating = rating;
        }

        public String getCountry() {
            return country;
        }

        public String getNote() {
            return note;
        }

        public float getRating() {
            return rating;
        }
    }

    private class CustomRankingsAdapter extends ArrayAdapter<CountryData> {

        private List<CountryData> mData;

        public CustomRankingsAdapter(Context context, List<CountryData> data) {
            super(context, R.layout.rankings_list_item, data);
            this.mData = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rowView = inflater.inflate(R.layout.rankings_list_item, parent, false);

            TextView countryTextView = rowView.findViewById(R.id.country_text_view);
            RatingBar noteRatingBar = rowView.findViewById(R.id.note_rating_bar);

            CountryData countryData = mData.get(position);

            countryTextView.setText(countryData.getCountry());
            noteRatingBar.setRating(countryData.getRating());

            return rowView;
        }
    }
}
