package com.example.sampleiamgeeditor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class ImageEditorActivity extends AppCompatActivity implements ActivityInterface {
    private static final int IMAGE_KEY = 100;
    private static final int CROP_KEY = 200;


    private ImageEditorFragment imageEditorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("NeoAstra Image Editor");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        onFragmentSelected(false, 0, null);
    }

    @Override
    public void onFragmentSelected(boolean intentActivity, int position, Bundle bundle) {
        if (!intentActivity) {
            if (position == 0) {
                if (imageEditorFragment == null) {
                    imageEditorFragment = new ImageEditorFragment();
                    if (bundle != null) {
                        imageEditorFragment.setArguments(bundle);
                    }
                }

                String IMAGE_EDITOR_FRAGMENT_KEY = "IMAGE_EDITOR_FRAGMENT";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, imageEditorFragment, IMAGE_EDITOR_FRAGMENT_KEY).commit();
            }
        } else {
            switch (position) {
                case 1:
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, IMAGE_KEY);
                    break;

                case 2:
                    Intent cropIntent = new Intent();
                    cropIntent.setAction(Intent.ACTION_VIEW);
                    File file = new File("/image/*");
                    cropIntent.setDataAndType(Uri.fromFile(file), "image/*");
                    cropIntent.putExtra("crop","true");
                    cropIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(cropIntent, CROP_KEY);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IMAGE_KEY:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    imageEditorFragment.loadImage(imageUri);
                }
                break;

            case CROP_KEY:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    imageEditorFragment.loadImage(imageUri);
                }
                break;
        }
    }
}
