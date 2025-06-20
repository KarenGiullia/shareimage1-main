package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Activity que cria uma imagem com um texto e imagem de fundo.
 */
public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private MemeCreator memeCreator;
    private enum TextoSelecionado { NENHUM, TEXTO_BAIXO, TEXTO_CIMA }
    private TextoSelecionado textoSelecionado = TextoSelecionado.NENHUM;


    public void selecionarTextoBaixo(View v) {
        textoSelecionado = TextoSelecionado.TEXTO_BAIXO;
        Toast.makeText(this, "Toque na tela para mover o texto de baixo", Toast.LENGTH_SHORT).show();
    }

    public void selecionarTextoCima(View v) {
        textoSelecionado = TextoSelecionado.TEXTO_CIMA;
        Toast.makeText(this, "Toque na tela para mover o texto de cima", Toast.LENGTH_SHORT).show();
    }

    private final ActivityResultLauncher<Intent> startTemplateActivity =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    int imagemId = result.getData().getIntExtra("imagem_template", -1);
                    if (imagemId != -1) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imagemId);
                        memeCreator.setFundo(bitmap);
                        mostrarImagem();
                    }
                }
            });

    public void iniciarMudarMeme(View view) {
        Intent intent = new Intent(this, TemplateActivity.class);
        startTemplateActivity.launch(intent);
    }

    private final ActivityResultLauncher<Intent> startNovoTexto = registerForActivityResult(new StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            String novoTexto = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TEXTO);
                            String novaCor = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVA_COR);
                            String novoTamanho = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TAMANHO);

                            String novoTexto2 = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TEXTO2);
                            String novaCor2 = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVA_COR2);
                            String novoTamanho2 = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TAMANHO2);
                            if (novaCor == null) {
                                Toast.makeText(MainActivity.this, "Cor desconhecida. Usando preto no lugar.", Toast.LENGTH_SHORT).show();
                                novaCor = "BLACK";
                            }
                            memeCreator.setTexto(novoTexto);
                            memeCreator.setCorTexto(Color.parseColor(novaCor.toUpperCase()));
                            memeCreator.setTamanhoTexto(Integer.parseInt(novoTamanho));

                            memeCreator.setTextoCima(novoTexto2);
                            memeCreator.setCorTextoCima(Color.parseColor(novaCor2.toUpperCase()));
                            memeCreator.setTamanhoTextoCima(Integer.parseInt(novoTamanho2));
                            mostrarImagem();
                        }
                    }
                }
            }
    );

    private final ActivityResultLauncher<PickVisualMediaRequest> startImagemFundo = registerForActivityResult(new PickVisualMedia(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result == null) {
                        return;
                    }
                    try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(result, "r")) {
                        Bitmap imagemFundo = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), result);
                        memeCreator.setFundo(imagemFundo);

                        // descobrir se é preciso rotacionar a imagem
                        FileDescriptor fd = pfd.getFileDescriptor();
                        ExifInterface exif = new ExifInterface(fd);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                            memeCreator.rotacionarFundo(90);
                        }

                        mostrarImagem();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
    );

    private ActivityResultLauncher<String> startWriteStoragePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (!result) {
                        Toast.makeText(MainActivity.this, "Sem permissão de acesso a armazenamento do celular.", Toast.LENGTH_SHORT).show();
                    } else {
                        compartilhar(null);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);

        Bitmap imagemFundo = BitmapFactory.decodeResource(getResources(), R.drawable.meme2);

        memeCreator = new MemeCreator("Olá Android!", Color.WHITE, 64, imagemFundo, getResources().getDisplayMetrics(), "teste", Color.BLACK, 50);
        mostrarImagem();
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX() / v.getWidth();
                float y = event.getY() / v.getHeight();

                switch (textoSelecionado) {
                    case TEXTO_BAIXO:
                        memeCreator.setPosicaoTexto(x, y);
                        break;
                    case TEXTO_CIMA:
                        memeCreator.setPosicaoTextoCima(x, y);
                        break;
                    default:
                        return false;
                }

                imageView.setImageBitmap(memeCreator.getImagem());
                textoSelecionado = TextoSelecionado.NENHUM; // Resetar seleção
                return true;
            }
            return false;
        });

    }

    public void iniciarMudarTexto(View v) {
        Intent intent = new Intent(this, NovoTextoActivity.class);
        intent.putExtra(NovoTextoActivity.EXTRA_TEXTO_ATUAL, memeCreator.getTexto());
        intent.putExtra(NovoTextoActivity.EXTRA_COR_ATUAL, converterCor(memeCreator.getCorTexto()));
        intent.putExtra(NovoTextoActivity.EXTRA_TAMANHO_ATUAL, String.valueOf(memeCreator.getTamanhoTexto()));

        intent.putExtra(NovoTextoActivity.EXTRA_TEXTO_ATUAL2, memeCreator.getTextoCima());
        intent.putExtra(NovoTextoActivity.EXTRA_COR_ATUAL2, converterCor(memeCreator.getCorTextoCima()));
        intent.putExtra(NovoTextoActivity.EXTRA_TAMANHO_ATUAL2, String.valueOf(memeCreator.getTamanhoTextoCima()));
        startNovoTexto.launch(intent);
    }

    public String converterCor(int cor) {
        switch (cor) {
            case Color.BLACK: return "BLACK";
            case Color.WHITE: return "WHITE";
            case Color.BLUE: return "BLUE";
            case Color.GREEN: return "GREEN";
            case Color.RED: return "RED";
            case Color.YELLOW: return "YELLOW";
        }
        return null;
    }

    public void iniciarMudarFundo(View v) {
        startImagemFundo.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ImageOnly.INSTANCE)
                .build());
    }

    public void compartilhar(View v) {
        compartilharImagem(memeCreator.getImagem());
    }

    public void mostrarImagem() {
        imageView.setImageBitmap(memeCreator.getImagem());
    }

    public void compartilharImagem(Bitmap bitmap) {

        // pegar a uri da mediastore
        // pego o volume externo pq normalmente ele é maior que o volume interno.
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            /*
            Em versões <= 28, é preciso solicitar a permissão WRITE_EXTERNAL_STORAGE.
            Mais detalhes em https://developer.android.com/training/data-storage/shared/media#java.
             */
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (PackageManager.PERMISSION_GRANTED != write) {
                startWriteStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        // montar a nova imagem a ser inserida na mediastore
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "shareimage1file");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        Uri imageUri = getContentResolver().insert(contentUri, values);

        // criar a nova imagem na pasta da mediastore
        try (
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(imageUri, "w");
                FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor())
            ) {
            BufferedOutputStream bytes = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gravar imagem:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // compartilhar a imagem com intent implícito
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_TITLE, "Seu meme fabuloso");
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Compartilhar Imagem"));
    }
}