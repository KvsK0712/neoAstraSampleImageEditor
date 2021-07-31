package com.example.sampleiamgeeditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class ImageEditorFragment extends Fragment {
    private ActivityInterface activityInterface = null;

    private ImageView imageView;
    private TextView option_open, option_crop, option_rot_left, option_rot_right, option_info, option_save;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_editor, container, false);
        init(rootView);
        loadImageByDefault();

        option_open.setOnClickListener(onClickListener);
        option_crop.setOnClickListener(onClickListener);
        option_rot_left.setOnClickListener(onClickListener);
        option_rot_right.setOnClickListener(onClickListener);
        option_info.setOnClickListener(onClickListener);
        option_save.setOnClickListener(onClickListener);
        return rootView;
    }

    private void init(View rootView) {
        imageView = rootView.findViewById(R.id.imageView);
        View footerView = rootView.findViewById(R.id.footer_view);

        option_open = footerView.findViewById(R.id.open);
        option_crop = footerView.findViewById(R.id.crop);
        option_rot_left = footerView.findViewById(R.id.flip_v);
        option_rot_right = footerView.findViewById(R.id.flip_h);
        option_info = footerView.findViewById(R.id.info);
        option_save = footerView.findViewById(R.id.save);
    }

    private void loadImageByDefault() {
        imageView.setImageResource(R.drawable.neoastra);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (id) {
                case R.id.open:
                    Toast.makeText(getContext(), "Select Image from Gallery.", Toast.LENGTH_SHORT);
                    openGallery();
                    break;

                case R.id.crop:
                    Toast.makeText(getContext(), "Crop Image.", Toast.LENGTH_SHORT);
                    cropImage();
                    break;

                case R.id.flip_v:
                    Toast.makeText(getContext(), "Rotating Image anti clockwise.", Toast.LENGTH_SHORT);
                    rotateImage(true);
                    break;

                case R.id.flip_h:
                    Toast.makeText(getContext(), "Rotating Image clockwise.", Toast.LENGTH_SHORT);
                    rotateImage(false);
                    break;

                case R.id.info:
                    Toast.makeText(getContext(), "Showing info of an Image.", Toast.LENGTH_SHORT);
                    showInfo();
                    break;

                case R.id.save:
                    Toast.makeText(getContext(), "Saving Image.", Toast.LENGTH_SHORT);
                    saveImage();
                    break;

            }
        }
    };

    private void openGallery() {
        activityInterface.onFragmentSelected(true, 1, null);
    }

    private void cropImage() {
        activityInterface.onFragmentSelected(true, 2, null);
    }


    private void rotateImage(boolean isAntiClockWiseRotation) {
        int rotationAngle = 90;
        if (isAntiClockWiseRotation) {
            rotationAngle = -90;
        }

        Matrix matrix = new Matrix();
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.postRotate((float) rotationAngle, imageView.getDrawable().getBounds().width()/2, imageView.getDrawable().getBounds().height()/2);
        imageView.setImageMatrix(matrix);
    }

    private void showInfo() {
        Drawable drawable = imageView.getBackground();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(imageByte);
        ExifInterface exifInterface = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exifInterface = new ExifInterface(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "This feature is still under development.", Toast.LENGTH_LONG);
            return;
        }

        if (exifInterface == null) {
            Toast.makeText(getContext(), "This feature is still under development.", Toast.LENGTH_LONG);
            return;
        }

        String infoText = "Info: ";
        infoText += "\nIMAGE_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        infoText += "\nIMAGE_WIDTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);

        /**
         * We can show more details using {@link ExifInterface}
         */

        showDetailsInUI(infoText);
    }

    private void showDetailsInUI(String infoText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Image Details");
        builder.setMessage(infoText);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void saveImage() {
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("Camera", Context.MODE_PRIVATE);
        File file = new File(directory, Calendar.getInstance().getTime().toString() + ".jpg");
        if (!file.exists()) {
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                Drawable drawable = imageView.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "This feature is still under development.", Toast.LENGTH_LONG);
                return;
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;
            activityInterface = (ActivityInterface) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityInterface = null;
    }

    public void loadImage(Uri imageUri) {
        imageView.setImageURI(imageUri);
    }
}
