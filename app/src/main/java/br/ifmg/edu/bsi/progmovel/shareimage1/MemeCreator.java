package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

/**
 * Cria um meme com um texto e uma imagem de fundo.
 *
 * Você pode controlar o texto, a cor do texto e a imagem de fundo.
 */
public class MemeCreator {

    //texto baixo
    private String texto;
    private int corTexto;
    int tamanhoTexto;
    //texto cima
    private String textoCima;
    private int corTextoCima;
    int tamanhoTextoCima;
    private Bitmap fundo;
    private DisplayMetrics displayMetrics;
    private Bitmap meme;
    private boolean dirty; // se true, significa que o meme precisa ser recriado.

    private float posXTexto = 0.5f;
    private float posYTexto = 0.9f;
    private float posXTextoCima = 0.5f;
    private float posYTextoCima = 0.15f;



    public MemeCreator(String texto, int corTexto, int tamanhoTexto, Bitmap fundo, DisplayMetrics displayMetrics, String textoCima, int corTextoCima, int tamanhoTextoCima) {
        this.texto = texto;
        this.corTexto = corTexto;
        this.fundo = fundo;
        this.tamanhoTexto = tamanhoTexto;

        this.textoCima = textoCima;
        this.corTextoCima = corTextoCima;
        this.tamanhoTextoCima = tamanhoTextoCima;

        this.displayMetrics = displayMetrics;
        this.meme = criarImagem();
        this.dirty = false;


    }

    public int getTamanhoTexto() {
        return tamanhoTexto;
    }

    public void setTamanhoTexto(int tamanhoTexto) {
        this.tamanhoTexto = tamanhoTexto;
        dirty = true;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
        dirty = true;
    }

    public int getCorTexto() {
        return corTexto;
    }

    public void setCorTexto(int corTexto) {
        this.corTexto = corTexto;
        dirty = true;
    }

    public Bitmap getFundo() {
        return fundo;
    }

    public void setFundo(Bitmap fundo) {
        this.fundo = fundo;
        dirty = true;
    }

    public void rotacionarFundo(float graus) {
        Matrix matrix = new Matrix();
        matrix.postRotate(graus);
        fundo = Bitmap.createBitmap(fundo, 0, 0, fundo.getWidth(), fundo.getHeight(), matrix, true);
        dirty = true;
    }

    public Bitmap getImagem() {
        if (dirty) {
            meme = criarImagem();
            dirty = false;
        }
        return meme;
    }

    public String getTextoCima() {
        return textoCima;
    }

    public void setTextoCima(String textoCima) {
        this.textoCima = textoCima;
    }

    public int getCorTextoCima() {
        return corTextoCima;
    }

    public void setCorTextoCima(int corTextoCima) {
        this.corTextoCima = corTextoCima;
    }

    public int getTamanhoTextoCima() {
        return tamanhoTextoCima;
    }
    public void setPosicaoTexto(float x, float y) {
        this.posXTexto = x;
        this.posYTexto = y;
        dirty = true;
    }

    public void setPosicaoTextoCima(float x, float y) {
        this.posXTextoCima = x;
        this.posYTextoCima = y;
        dirty = true;
    }

    public float getPosXTexto() {
        return posXTexto;
    }

    public float getPosYTexto() {
        return posYTexto;
    }

    public float getPosXTextoCima() {
        return posXTextoCima;
    }

    public float getPosYTextoCima() {
        return posYTextoCima;
    }

    public void setTamanhoTextoCima(int tamanhoTextoCima) {
        this.tamanhoTextoCima = tamanhoTextoCima;
    }

    protected Bitmap criarImagem() {
        float heightFactor = (float) fundo.getHeight() / fundo.getWidth();
        int width = displayMetrics.widthPixels;
        int height = (int) (width * heightFactor);
        // nao deixa a imagem ocupar mais que 60% da altura da tela.
        if (height > displayMetrics.heightPixels * 0.6) {
            height = (int) (displayMetrics.heightPixels * 0.6);
            width = (int) (height * (1 / heightFactor));
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        Paint paintCima = new Paint();
        Bitmap scaledFundo = Bitmap.createScaledBitmap(fundo, width, height, true);
        canvas.drawBitmap(scaledFundo, 0, 0, new Paint());

        paint.setColor(corTexto);
        paint.setAntiAlias(true);
        paint.setTextSize(tamanhoTexto);
        paint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);

        paintCima.setColor(corTextoCima);
        paintCima.setAntiAlias(true);
        paintCima.setTextSize(tamanhoTextoCima);
        paintCima.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        paintCima.setTextAlign(Paint.Align.CENTER);
        //desenhar texto em cima
        //canvas.drawText(textoCima, (width / 2.f), (height * 0.15f), paintCima);
        canvas.drawText(textoCima, (width * posXTextoCima), (height * posYTextoCima), paintCima);

        // desenhar texto embaixo
        //canvas.drawText(texto, (width / 2.f), (height * 0.9f), paint);
        canvas.drawText(texto, (width * posXTexto), (height * posYTexto), paint);

        return bitmap;
    }
}
