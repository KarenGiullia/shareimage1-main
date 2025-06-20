package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class TemplateActivity extends AppCompatActivity {

    private final int[] idsTemplates = {
            R.id.image1, R.id.image2, R.id.image3, R.id.image4, R.id.image5,
            R.id.image6, R.id.image7, R.id.image8, R.id.image9, R.id.image10
    };

    private final int[] imagens = {
            R.drawable.meme1, R.drawable.meme2, R.drawable.meme3, R.drawable.meme4, R.drawable.meme5,
            R.drawable.meme6, R.drawable.meme7, R.drawable.meme8, R.drawable.meme9, R.drawable.meme10
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);

        for (int i = 0; i < idsTemplates.length; i++) {
            ImageView img = findViewById(idsTemplates[i]);
            int resId = imagens[i];

            img.setOnClickListener(view -> {
                Intent resultado = new Intent();
                resultado.putExtra("imagem_template", resId);
                setResult(RESULT_OK, resultado);
                finish();
            });
        }
    }
}
