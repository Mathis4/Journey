package com.example.journey;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RankingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new); // Assurez-vous que le fichier XML est correctement nommé

        // Vous pouvez ajouter du contenu à cette activité ici
        TextView textView = findViewById(R.id.textView);
        textView.setText("Créer la");
    }
}
