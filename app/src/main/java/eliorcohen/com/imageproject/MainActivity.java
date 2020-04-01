package eliorcohen.com.imageproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnOpenExtCam;
    private ImageView myImage;
    private TextView myTextRGB1, myTextRGB2, myTextRGB3, myTextRGB4, myTextRGB5,
            myTextRGBPercent1, myTextRGBPercent2, myTextRGBPercent3, myTextRGBPercent4, myTextRGBPercent5;
    private double percentage1, percentage2, percentage3, percentage4, percentage5;
    private static final int CAMERA_REQUEST = 1888, MY_CAMERA_PERMISSION_CODE = 100, CAMERA_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initListeners();
        permissions();
    }

    // Initialize UI
    private void initUI() {
        // Button
        btnOpenExtCam = findViewById(R.id.btnExtCamera);
        // ImageView
        myImage = findViewById(R.id.myImage);
        // TextView
        myTextRGB1 = findViewById(R.id.myTextRGB1);
        myTextRGB2 = findViewById(R.id.myTextRGB2);
        myTextRGB3 = findViewById(R.id.myTextRGB3);
        myTextRGB4 = findViewById(R.id.myTextRGB4);
        myTextRGB5 = findViewById(R.id.myTextRGB5);
        myTextRGBPercent1 = findViewById(R.id.myTextRGBPercent1);
        myTextRGBPercent2 = findViewById(R.id.myTextRGBPercent2);
        myTextRGBPercent3 = findViewById(R.id.myTextRGBPercent3);
        myTextRGBPercent4 = findViewById(R.id.myTextRGBPercent4);
        myTextRGBPercent5 = findViewById(R.id.myTextRGBPercent5);
    }

    // Initialize click buttons
    private void initListeners() {
        btnOpenExtCam.setOnClickListener(this);
    }

    // Permission for camera and others
    private void permissions() {
        // Get permission for the camera
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);

        // Detect methods whose names start with penalty and solve the crash
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    // onRequestPermissionsResult of external camera - opens the camera to getting information below
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    // onActivityResult of external camera - gets the data from onRequestPermissionsResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            // Put data in Bitmap
            Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

            assert bitmap != null;
            // Get pixels and put in Bitmap
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int size = width * height;
            int[] pixels = new int[size];

            Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);

            bitmap2.getPixels(pixels, 0, width, 0, 0, width, height);

            // Formula to get pixels to convert to colors
            final List<HashMap<Integer, Integer>> colorMap = new ArrayList<>();
            colorMap.add(new HashMap<Integer, Integer>());
            colorMap.add(new HashMap<Integer, Integer>());
            colorMap.add(new HashMap<Integer, Integer>());

            int color;
            int r;
            int g;
            int b;
            Integer rC, gC, bC;
            for (int pixel : pixels) {
                color = pixel;

                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);

                rC = colorMap.get(0).get(r);
                if (rC == null)
                    rC = 0;
                colorMap.get(0).put(r, ++rC);

                gC = colorMap.get(1).get(g);
                if (gC == null)
                    gC = 0;
                colorMap.get(1).put(g, ++gC);

                bC = colorMap.get(2).get(b);
                if (bC == null)
                    bC = 0;
                colorMap.get(2).put(b, ++bC);
            }

            // ArrayList to calculates the amount of pixels used
            ArrayList<Integer> arrayListRGB = new ArrayList<>();
            // TreeMap to enter the keys and values
            TreeMap<Integer, Integer> prodTreeMapRGB = new TreeMap<>(Collections.reverseOrder());

            // For loop to get the RGB
            for (int i = 0; i < 3; i++) {
                int max = 0;
                int val;
                // For each loop to get the values of colors
                for (Map.Entry<Integer, Integer> entry : colorMap.get(i).entrySet()) {
                    if (entry.getValue() > max) {
                        max = entry.getValue(); // get the 'pixels of any num of color'
                        val = entry.getKey(); // get the 'value color'

                        prodTreeMapRGB.put(max, val); // put the values as key & value
                        arrayListRGB.addAll(Collections.singleton(max)); // Add all the values of pixels of any num of color into the arrayListRGB
                    }
                }
            }

            // Initialize the TextView(s) & variable(s)
            initVals_Texts();

            // put into the textView(s) the 'value color'
            try {
                getTextRGBMethod(prodTreeMapRGB, 0, 1, 2, myTextRGB1);
                getTextRGBMethod(prodTreeMapRGB, 3, 4, 5, myTextRGB2);
                getTextRGBMethod(prodTreeMapRGB, 6, 7, 8, myTextRGB3);
                getTextRGBMethod(prodTreeMapRGB, 9, 10, 11, myTextRGB4);
                getTextRGBMethod(prodTreeMapRGB, 12, 13, 14, myTextRGB5);
            } catch (Exception e) {

            }

            // put into the textView(s) the 'pixels of any num of color'
            try {
                for (int i = 0; i < 3; i++) {
                    if (!myTextRGB1.getText().toString().matches("")) {
                        percentage1 += Double.parseDouble(String.valueOf(Objects.requireNonNull(prodTreeMapRGB.keySet().toArray())[i]));
                    }
                }

                for (int i = 3; i < 6; i++) {
                    if (!myTextRGB2.getText().toString().matches("")) {
                        percentage2 += Double.parseDouble(String.valueOf(Objects.requireNonNull(prodTreeMapRGB.keySet().toArray())[i]));
                    }
                }

                for (int i = 6; i < 9; i++) {
                    if (!myTextRGB3.getText().toString().matches("")) {
                        percentage3 += Double.parseDouble(String.valueOf(Objects.requireNonNull(prodTreeMapRGB.keySet().toArray())[i]));
                    }
                }

                for (int i = 9; i < 12; i++) {
                    if (!myTextRGB4.getText().toString().matches("")) {
                        percentage4 += Double.parseDouble(String.valueOf(Objects.requireNonNull(prodTreeMapRGB.keySet().toArray())[i]));
                    }
                }

                for (int i = 12; i < 15; i++) {
                    if (!myTextRGB5.getText().toString().matches("")) {
                        percentage5 += Double.parseDouble(String.valueOf(Objects.requireNonNull(prodTreeMapRGB.keySet().toArray())[i]));
                    }
                }
            } catch (Exception e) {

            }

            // Calculates the amount of 'pixels of any num of color'
            int sum = 0;
            for (int i = 0; i < arrayListRGB.size(); i++) {
                sum += arrayListRGB.get(i);
            }

            getPercentMethod(percentage1, sum, myTextRGBPercent1);
            getPercentMethod(percentage2, sum, myTextRGBPercent2);
            getPercentMethod(percentage3, sum, myTextRGBPercent3);
            getPercentMethod(percentage4, sum, myTextRGBPercent4);
            getPercentMethod(percentage5, sum, myTextRGBPercent5);

            // Set the Bitmap into the ImageView
            myImage.setImageBitmap(bitmap);
        }
    }

    private void getTextRGBMethod(TreeMap<Integer, Integer> prodTreeMapRGB, int num1, int num2, int num3, TextView textRGB) {
        textRGB.setText("R:" + prodTreeMapRGB.values().toArray()[num1] +
                " G:" + prodTreeMapRGB.values().toArray()[num2] +
                " B:" + prodTreeMapRGB.values().toArray()[num3]);
    }

    private void getPercentMethod(double percentage, int sum, TextView textPercent) {
        String percentageMe = String.format("%.2f", 100 * (percentage / sum));
        textPercent.setText(percentageMe + "%");
    }

    private void initVals_Texts() {
        myTextRGB1.setText("");
        myTextRGB2.setText("");
        myTextRGB3.setText("");
        myTextRGB4.setText("");
        myTextRGB5.setText("");

        percentage1 = 0.00;
        percentage2 = 0.00;
        percentage3 = 0.00;
        percentage4 = 0.00;
        percentage5 = 0.00;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Opens the camera
            case R.id.btnExtCamera:
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                break;
        }
    }

}
