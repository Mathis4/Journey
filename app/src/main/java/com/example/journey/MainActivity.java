package com.example.journey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
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


        Button myRankingButton = findViewById(R.id.btn_my_ranking1);

        // Récupérer la référence de la WebView
        webView = findViewById(R.id.webView);

        // Activer JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Charger la carte Leaflet
        webView.loadUrl("file:///android_asset/map.html");

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
                Intent intent = new Intent(MainActivity.this, PopUpActivity.class);
                intent.putExtra("key_country", selectedCountryName);
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

        // Ajouter un écouteur de clics à la croix
        ImageView crossIcon = findViewById(R.id.crossIcon);
        crossIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vérifier si un pays est sélectionné
                if (selectedCountryName != null && !selectedCountryName.isEmpty()) {
                    // Supprimer le pays sélectionné de la liste des pays colorés
                    removeColoredCountry(selectedCountryName);

                    // Appeler la méthode JavaScript pour décolorer le pays
                    String javascript = "javascript:colorCountry('" + selectedCountryName + "', 'white')";
                    webView.loadUrl(javascript);

                    // Effacer le nom du pays sélectionné
                    TextView countryTextView = findViewById(R.id.selected_country_name);
                    countryTextView.setText("");
                    // Cacher le bouton "Plus d'infos"
                    infoButton.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "No country selected!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        myRankingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer un Intent pour passer à la nouvelle activité
                Intent intent = new Intent(MainActivity.this, RankingsActivity.class);

                // Démarrer la nouvelle activité
                startActivity(intent);
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

    // Méthode pour supprimer un pays de la liste des pays colorés
    private void removeColoredCountry(String countryName) {
        // Supprimer le pays de la liste
        coloredCountries.removeIf(entry -> entry.startsWith(countryName + ":"));
        // Enregistrer la liste mise à jour
        saveColoredCountries(coloredCountries);
    }
}
