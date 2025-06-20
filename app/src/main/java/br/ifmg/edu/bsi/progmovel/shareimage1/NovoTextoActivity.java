package br.ifmg.edu.bsi.progmovel.shareimage1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NovoTextoActivity extends AppCompatActivity {

    //texto de baixo
    public static String EXTRA_TEXTO_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.texto_atual";
    public static String EXTRA_COR_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.cor_atual";
    public static String EXTRA_TAMANHO_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.tamanho_atual";
    public static String EXTRA_NOVO_TEXTO = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_texto";
    public static String EXTRA_NOVA_COR = "br.ifmg.edu.bsi.progmovel.shareimage1.nova_cor";
    public static String EXTRA_NOVO_TAMANHO = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_tamanho";

    //texto de cima
    public static String EXTRA_TEXTO_ATUAL2 = "br.ifmg.edu.bsi.progmovel.shareimage1.texto_atual2";
    public static String EXTRA_COR_ATUAL2 = "br.ifmg.edu.bsi.progmovel.shareimage1.cor_atual2";
    public static String EXTRA_TAMANHO_ATUAL2 = "br.ifmg.edu.bsi.progmovel.shareimage1.tamanho_atual2";
    public static String EXTRA_NOVO_TEXTO2 = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_texto2";
    public static String EXTRA_NOVA_COR2 = "br.ifmg.edu.bsi.progmovel.shareimage1.nova_cor2";
    public static String EXTRA_NOVO_TAMANHO2 = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_tamanho2";


    //Texto de baixo (texto1)
    private EditText etTexto;
    private EditText etCor;
    private EditText etTamanho;

    //texto de cima (texto2)
    private EditText etTexto2;
    private EditText etCor2;
    private EditText etTamanho2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_texto);
        //baixo
        etTexto = findViewById(R.id.etTexto);
        etCor = findViewById(R.id.etCor);
        etTamanho = findViewById(R.id.etTamanho);
        //cima
        etTexto2 = findViewById(R.id.etTextoCima);
        etCor2 = findViewById(R.id.etCorCima);
        etTamanho2 = findViewById(R.id.etTamanhoCima);

        //baixo
        Intent intent = getIntent();
        String textoAtual = intent.getStringExtra(EXTRA_TEXTO_ATUAL);
        String corAtual = intent.getStringExtra(EXTRA_COR_ATUAL);
        String tamanhoAtual = intent.getStringExtra(EXTRA_TAMANHO_ATUAL);
        etTexto.setText(textoAtual);
        etCor.setText(corAtual);
        etTamanho.setText(tamanhoAtual);

        //cima
        textoAtual = intent.getStringExtra(EXTRA_TEXTO_ATUAL2);
        corAtual = intent.getStringExtra(EXTRA_COR_ATUAL2);
        tamanhoAtual = intent.getStringExtra(EXTRA_TAMANHO_ATUAL2);
        etTexto2.setText(textoAtual);
        etCor2.setText(corAtual);
        etTamanho2.setText(tamanhoAtual);
    }

    public void enviarNovoTexto(View v) {
        //BAIXO
        String novoTexto = etTexto.getText().toString();
        String novaCor = etCor.getText().toString();
        String novoTamanho = etTamanho.getText().toString();
        //CIMA
        String novoTexto2 = etTexto2.getText().toString();
        String novaCor2 = etCor2.getText().toString();
        String novoTamanho2 = etTamanho2.getText().toString();

        Intent intent = new Intent();
        intent.putExtra(EXTRA_NOVO_TEXTO, novoTexto);
        intent.putExtra(EXTRA_NOVA_COR, novaCor);
        intent.putExtra(EXTRA_NOVO_TAMANHO, novoTamanho);

        intent.putExtra(EXTRA_NOVO_TEXTO2, novoTexto2);
        intent.putExtra(EXTRA_NOVA_COR2, novaCor2);
        intent.putExtra(EXTRA_NOVO_TAMANHO2, novoTamanho2);
        setResult(RESULT_OK, intent);
        finish();
    }
}