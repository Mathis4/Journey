package com.example.journey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import java.util.List;
import java.util.ArrayList;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ImageButton infoButton;
    private String selectedCountryName; // Variable pour stocker le nom du pays sélectionné
    private Set<String> coloredCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupérer la référence de la WebView
        webView = findViewById(R.id.webView);

        // Activer JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Charger la carte Leaflet
        webView.loadUrl("file:///android_asset/map.html");

        // Récupérer la référence du Spinner
        Spinner spinner = findViewById(R.id.spinner);

        // Créer un adaptateur pour le Spinner à partir d'un tableau de ressources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Définir un écouteur pour le Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                switch (selectedItem) {
                    case "Global country rankings":
                        // Ouvrir l'activité des classements globaux des pays
                        startActivity(new Intent(MainActivity.this, RankingsActivity.class));
                        break;
                    case "My account":
                        // Ouvrir l'activité de mon compte
                        startActivity(new Intent(MainActivity.this, AccountActivity.class));
                        break;
                    // Ajoutez d'autres cas au besoin
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Faites quelque chose si rien n'est sélectionné
            }
        });

        // Récupérer la référence du bouton "Plus d'infos"
        infoButton = findViewById(R.id.info_button);
        infoButton.setVisibility(View.GONE); // Cacher le bouton initialement

        CheckBox checkBoxWant = findViewById(R.id.checkBoxWant);
        CheckBox checkBoxLived = findViewById(R.id.checkBoxLived);
        CheckBox checkBoxBeen = findViewById(R.id.checkBoxBeen);

        // Ajouter une interface JavaScript pour la communication avec Java
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Si une case est cochée, décochez les autres
                    if (buttonView.getId() == R.id.checkBoxWant) {
                        checkBoxLived.setChecked(false);
                        checkBoxBeen.setChecked(false);
                    } else if (buttonView.getId() == R.id.checkBoxLived) {
                        checkBoxWant.setChecked(false);
                        checkBoxBeen.setChecked(false);
                    } else if (buttonView.getId() == R.id.checkBoxBeen) {
                        checkBoxWant.setChecked(false);
                        checkBoxLived.setChecked(false);
                    }
                    // Appeler onCheckboxChecked en passant le nom du pays
                    onCheckboxChecked(selectedCountryName);
                }
            }
        };

        checkBoxWant.setOnCheckedChangeListener(checkBoxListener);
        checkBoxLived.setOnCheckedChangeListener(checkBoxListener);
        checkBoxBeen.setOnCheckedChangeListener(checkBoxListener);

        // Ajouter un écouteur de clics au bouton "Plus d'infos"
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvrir une nouvelle activité
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        // Initialiser la variable coloredCountries avant de charger la liste des pays colorés
        coloredCountries = new HashSet<>();
        coloredCountries.addAll(loadColoredCountries());

        // Attente du chargement complet de la page avant de colorer les pays
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                colorCountriesOnMap();
            }
        });
    }

    // Méthode pour colorer les pays sur la carte
    private void colorCountriesOnMap() {
        for (String entry : coloredCountries) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                String countryName = parts[0];
                String color = parts[1];
                if (!countryName.isEmpty() && !color.isEmpty()) {
                    String javascript = "javascript:colorCountry('" + countryName + "', '" + color + "')";
                    webView.loadUrl(javascript);
                }
            }
        }
    }

    // Classe pour gérer les appels JavaScript
    public class WebAppInterface {
        @JavascriptInterface
        public void onCountryClicked(String countryName) {
            // Stocker le nom du pays sélectionné
            selectedCountryName = countryName;
            // Afficher le nom du pays dans un TextView
            TextView countryTextView = findViewById(R.id.selected_country_name);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    infoButton.setVisibility(View.VISIBLE);
                    countryTextView.setText(countryName);
                }
            });
            CheckBox checkBoxWant = findViewById(R.id.checkBoxWant);
            CheckBox checkBoxLived = findViewById(R.id.checkBoxLived);
            CheckBox checkBoxBeen = findViewById(R.id.checkBoxBeen);

            checkBoxWant.setChecked(false);
            checkBoxLived.setChecked(false);
            checkBoxBeen.setChecked(false);
        }
    }

    // Méthode pour changer la couleur du pays en fonction de la case cochée
    @JavascriptInterface
    public void onCheckboxChecked(String countryName) {
        String color = "";
        CheckBox checkBoxWant = findViewById(R.id.checkBoxWant);
        CheckBox checkBoxLived = findViewById(R.id.checkBoxLived);
        CheckBox checkBoxBeen = findViewById(R.id.checkBoxBeen);

        if (checkBoxWant.isChecked()) {
            color = "yellow";
        } else if (checkBoxLived.isChecked()) {
            color = "darkblue";
        } else if (checkBoxBeen.isChecked()) {
            color = "green";
        }

        // Ajouter le pays coloré à la liste
        coloredCountries.add(countryName + ":" + color);

        // Enregistrer la liste mise à jour
        saveColoredCountries(coloredCountries);

        // Appeler la méthode JavaScript pour changer la couleur du pays
        String javascript = "javascript:colorCountry('" + countryName + "', '" + color + "')";
        webView.loadUrl(javascript);
    }

    // Méthode pour sauvegarder la liste des pays colorés
    private void saveColoredCountries(Set<String> countries) {
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Convertir l'ensemble de pays colorés en une liste
        List<String> countryList = new ArrayList<>(countries);

        // Enregistrer la taille de la liste
        editor.putInt("coloredCountries_size", countryList.size());

        // Enregistrer chaque pays avec sa couleur
        for (int i = 0; i < countryList.size(); i++) {
            editor.putString("coloredCountries_" + i, countryList.get(i));
        }

        editor.apply();
    }

    // Méthode pour charger la liste des pays colorés
    private Set<String> loadColoredCountries() {
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Set<String> countries = new HashSet<>();

        // Récupérer la taille de la liste
        int size = sharedPref.getInt("coloredCountries_size", 0);

        // Charger chaque pays avec sa couleur
        for (int i = 0; i < size; i++) {
            String country = sharedPref.getString("coloredCountries_" + i, null);
            if (country != null) {
                countries.add(country);
            }
        }

        return countries;
    }
}
