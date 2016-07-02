package com.example.dext.camerademo;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dext on 7/2/2016.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback,Camera.PreviewCallback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int[] pixels = null;
    private byte[] FrameData = null;
    private int imageFormat;
    private int PreviewSizeWidth;
    private int PreviewSizeHeight;
    private Number[] frameSequence;
    private int counter;
    private int Nsec=5;
    private XYPlot plot;

    public CameraView(Context context, Camera camera,XYPlot plot){
        super(context);
        mCamera = camera;
        mCamera.setDisplayOrientation(90);
        //get the holder and set this class as the callback, so we can get camera data here
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        this.plot = plot;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            //when the surface is created, we can set the camera to draw images in this surfaceholder
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            for(int i=0; i<sizes.size(); i++)
            {
                Log.i("CS", i+" - width: "+sizes.get(i).width+" height: "+sizes.get(i).height+" size: "+(sizes.get(i).width*sizes.get(i).height));
            }

            // change preview size
            final Camera.Size cs = sizes.get(8);
            parameters.setPreviewSize(cs.width, cs.height);
            PreviewSizeWidth =cs.width;
            PreviewSizeHeight = cs.height;
            imageFormat = parameters.getPreviewFormat();
            frameSequence = new Number[parameters.getPreviewFrameRate()*Nsec];
            counter = 0;

        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
        }
    }
    @Override
    public void onPreviewFrame(byte[] arg0, Camera arg1)
    {
        //Log.i("CS","onPreviewFrame called.");
        // At preview mode, the frame data will push to here.
        if (imageFormat == ImageFormat.NV21)
        {
            //We only accept the NV21(YUV420) format.
            FrameData = arg0;
            //Log.i("CS","FrameData length="+FrameData.length);
            double avg=0;
            for(double i:FrameData){
                avg +=i;
            }
            avg /= FrameData.length;
            //Log.v("Image","Frame Avg="+avg);
            frameSequence[counter]=(Number)avg;
            counter = (counter+1)%(frameSequence.length);

            XYSeries s1 = new SimpleXYSeries(Arrays.asList(frameSequence),SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
            // create formatters to use for drawing a series using LineAndPointRenderer
            // and configure them from xml:
            LineAndPointFormatter series1Format = new LineAndPointFormatter();

            if(plot != null) {
                plot.clear();
                plot.addSeries(s1, series1Format);
                plot.redraw();
            }
            else{
                Log.i("CS","plot is NULL");
            }


        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        //before changing the application orientation, you need to stop the preview, rotate and then start it again
        if(mHolder.getSurface() == null)//check if the surface is ready to receive camera data
            return;

        try{
            mCamera.stopPreview();
        } catch (Exception e){
            //this will happen when you are trying the camera if it's not running
        }

        //now, recreate the camera preview
        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //our app has only one screen, so we'll destroy the camera in the surface
        //if you are unsing with more screens, please move this code your activity
        mCamera.stopPreview();
        mCamera.release();
    }
}
