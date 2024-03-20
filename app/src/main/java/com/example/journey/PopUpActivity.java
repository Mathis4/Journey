package com.example.journey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PopUpActivity extends AppCompatActivity {

    private ImageView countryImage, countryFlag;
    private TextView countryName, capital, population, area, currency;
    private EditText noteEditText;
    private RatingBar ratingBar;
    private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        Intent intent = getIntent();
        country = intent.getStringExtra("key_country");

        countryImage = findViewById(R.id.image_item);
        countryFlag = findViewById(R.id.flag);
        countryName = findViewById(R.id.popup_country_name);
        capital = findViewById(R.id.capital_name);
        population = findViewById(R.id.population_value);
        area = findViewById(R.id.superficie_value);
        currency = findViewById(R.id.text_devise_value);
        noteEditText = findViewById(R.id.note_user);
        ratingBar = findViewById(R.id.rating_bar);

        switch (country) {
            case "France":
                // Modifier les informations pour la France
                countryImage.setImageResource(R.drawable.france);
                countryFlag.setImageResource(R.drawable.france_flag);
                capital.setText("Paris");
                population.setText("67 millions");
                area.setText("551,695 km²");
                currency.setText("Euro");
                break;
            case "Spain":
                // Modifier les informations pour l'Espagne
                countryImage.setImageResource(R.drawable.espagne);
                countryFlag.setImageResource(R.drawable.spain_flag);
                capital.setText("Madrid");
                population.setText("46.9 millions");
                area.setText("505,990 km²");
                currency.setText("Euro");
                break;
            case "Germany":
                // Update information for Germany
                countryImage.setImageResource(R.drawable.germany);
                countryFlag.setImageResource(R.drawable.germany_flag);
                capital.setText("Berlin");
                population.setText("83 millions");
                area.setText("357,386 km²");
                currency.setText("Euro");
                break;
            case "Italy":
                // Update information for Italy
                countryImage.setImageResource(R.drawable.italie);
                countryFlag.setImageResource(R.drawable.italy_flag);
                capital.setText("Rome");
                population.setText("60.4 millions");
                area.setText("301,340 km²");
                currency.setText("Euro");
                break;
            case "United Kingdom":
                // Update information for the United Kingdom
                countryImage.setImageResource(R.drawable.united_kingdom);
                countryFlag.setImageResource(R.drawable.united_kingdom_flag);
                capital.setText("London");
                population.setText("66.7 millions");
                area.setText("243,610 km²");
                currency.setText("British Pound");
                break;
            case "Portugal":
                // Update information for Portugal
                countryImage.setImageResource(R.drawable.portugal);
                countryFlag.setImageResource(R.drawable.portugal_flag);
                capital.setText("Lisbon");
                population.setText("10.3 millions");
                area.setText("92,212 km²");
                currency.setText("Euro");
                break;
            case "United States of America":
                // Update information for the United States
                countryImage.setImageResource(R.drawable.united_states);
                countryFlag.setImageResource(R.drawable.united_states_flag);
                capital.setText("Washington D.C.");
                population.setText("331 millions");
                area.setText("9,833,520 km²");
                currency.setText("US Dollar");
                break;
            case "Brazil":
                // Update information for Brazil
                countryImage.setImageResource(R.drawable.bresil);
                countryFlag.setImageResource(R.drawable.brazil_flag);
                capital.setText("Brasília");
                population.setText("212 millions");
                area.setText("8,515,767 km²");
                currency.setText("Brazilian Real");
                break;
            case "China":
                // Update information for China
                countryImage.setImageResource(R.drawable.china);
                countryFlag.setImageResource(R.drawable.china_flag);
                capital.setText("Beijing");
                population.setText("1.4 billion");
                area.setText("9,596,961 km²");
                currency.setText("Chinese Yuan");
                break;
            case "Japan":
                // Update information for Japan
                countryImage.setImageResource(R.drawable.japan);
                countryFlag.setImageResource(R.drawable.japan_flag);
                capital.setText("Tokyo");
                population.setText("126.5 millions");
                area.setText("377,975 km²");
                currency.setText("Japanese Yen");
                break;
            case "Russia":
                // Update information for Russia
                countryImage.setImageResource(R.drawable.russia);
                countryFlag.setImageResource(R.drawable.russia_flag);
                capital.setText("Moscow");
                population.setText("146 millions");
                area.setText("17,098,242 km²");
                currency.setText("Russian Ruble");
                break;
            case "Mexico":
                // Update information for Mexico
                countryImage.setImageResource(R.drawable.mexico);
                countryFlag.setImageResource(R.drawable.mexico_flag);
                capital.setText("Mexico City");
                population.setText("126 millions");
                area.setText("1,964,375 km²");
                currency.setText("Mexican Peso");
                break;
            case "Canada":
                // Update information for Canada
                countryImage.setImageResource(R.drawable.canada);
                countryFlag.setImageResource(R.drawable.canada_flag);
                capital.setText("Ottawa");
                population.setText("37.6 millions");
                area.setText("9,984,670 km²");
                currency.setText("Canadian Dollar");
                break;
            // Add cases for other countries as needed
            default:
                // Rajoutes les autres pays plus tard
                break;
    }

        countryName.setText(country);

        SharedPreferences prefs = getSharedPreferences("RankingsData", MODE_PRIVATE);
        String savedNote = prefs.getString(country + "_note", "");
        noteEditText.setText(savedNote);

        float savedRating = prefs.getFloat(country + "_rating", 0.0f);
        ratingBar.setRating(savedRating);
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences("RankingsData", MODE_PRIVATE).edit();
        editor.putString(country + "_note", noteEditText.getText().toString());
        editor.putFloat(country + "_rating", ratingBar.getRating());
        editor.apply();
    }
}
