package com.example.dext.camerademo;



import android.graphics.Color;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Camera mCamera = null;
    private CameraView mCameraView = null;
    public XYPlot plot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try{
            mCamera = Camera.open() ;//you can use open(int) to use different cameras
            Camera.Parameters p = mCamera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(p);
            mCamera.startPreview();
            plot = (XYPlot) findViewById(R.id.dynamicXYPlot);

            if(plot ==null)
                Log.i("PLOT","plot is null.");
            else
                Log.i("PLOT","plot is NOT null.");

            // Create a couple arrays of y-values to plot:
            Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
            // Turn the above arrays into XYSeries':
            XYSeries series1 = new SimpleXYSeries(
                    Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                    "Series1");                             // Set the display title of the series
            // Create a formatter to use for drawing a series using LineAndPointRenderer:
            LineAndPointFormatter series1Format = new LineAndPointFormatter(
                    Color.rgb(0, 200, 0),                   // line color
                    Color.rgb(0, 100, 0),                   // point color
                    null,                                   // fill color (none)
                    new PointLabelFormatter(Color.WHITE));                           // text color
            // add a new series' to the xyplot:
            plot.addSeries(series1, series1Format);



        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(mCamera != null) {
            mCameraView = new CameraView(this, mCamera,plot);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
            assert camera_view != null;
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }

        //btn to close the application
        ImageButton imgClose = (ImageButton)findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }


}
