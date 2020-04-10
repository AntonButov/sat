package pro.butovanton.satellite.ui.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pro.butovanton.satellite.R;
import pro.butovanton.satellite.Sat;
import pro.butovanton.satellite.ui.sats.satsViewModel;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class CameraFragment extends Fragment implements SensorEventListener{

    private pro.butovanton.satellite.ui.sats.satsViewModel satsViewModel;

    private final int MY_REQUEST_CODE_FOR_CAMERA = 110;
    CameraService[] myCameras = null;

    private CameraManager mCameraManager = null;
    private final int CAMERA1 = 0;
    private final int CAMERA2 = 1;
    private TextureView mTextureView = null;
    private ImageView imageLineGor, left;
    private TextView azimut, corner;

    private SensorManager sensorManager;
    private Sensor magnite;
    private Sensor gsensor;

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] I = new float[9];
    private float[] r = new float[9];
    float orientation[] = new float[3];

    private long timeold1 = 0;
    private float y1, x1;

    private float Lx, Ly;

    private int conerplacesat = 28;
    private int azimuthsatint = 16;

    private int width, height;
    private ArrayList<viewsat> viewsats;
    private List<Sat> sats;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        satsViewModel =
                ViewModelProviders.of(requireActivity()).get(satsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_camera, container, false);

        sats = satsViewModel.getSatsList();

        Lx = getResources().getDisplayMetrics().widthPixels;//
        Ly = getResources().getDisplayMetrics().heightPixels;// (float) (sin(Math.PI/4)/ 680/2);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensor = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i < listSensor.size(); i++) {
            Log.d("DEBUG", listSensor.get(i).getName());
        }
        magnite = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        azimut = root.findViewById(R.id.textViewAsimut);
        corner = root.findViewById(R.id.textViewConerPl);
        mTextureView = root.findViewById(R.id.texture);
        imageLineGor = root.findViewById(R.id.imageViewGor);
    //    left = root.findViewById(R.id.left);
        //  name = root.findViewById(R.id.name);

        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            // Получение списка камер с устройства
            myCameras = new CameraService[mCameraManager.getCameraIdList().length];
            for (String cameraID : mCameraManager.getCameraIdList()) {
                Log.i("DEBUG", "cameraID: " + cameraID);
                int id = Integer.parseInt(cameraID);
                // создаем обработчик для камеры
                myCameras[id] = new CameraService(mCameraManager, cameraID);
            }
        } catch (CameraAccessException e) {
            Log.e("DEBUG", e.getMessage());
            e.printStackTrace();
        }

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        ConstraintLayout constraintLayout = root.findViewById(R.id.constrait);
        viewsats = new ArrayList<viewsat>();

        for(int i = 0 ; i< 30; i ++) {
     //   for (Sat sat : sats) {
            float azimutplacesat = azimuthsat(35, 49, sats.get(i).getPosition());
            float conerplacesat = conerplacesat(35, 49, sats.get(i).getPosition());
            String name = sats.get(i).getName();
            if (conerplacesat > 0)
                viewsats.add(new viewsat(getContext(), constraintLayout, azimutplacesat, conerplacesat, name));
        }
        int i = 0;

  //      for (viewsat viewsat : viewsats) { // считаем средний азмут и уголместа
 //           azimuthsatint = (int) (azimuthsatint + viewsat.getAzimut());
 //           conerplacesat = (int) (conerplacesat + viewsat.getConerplace());
  //          if (i % 2 == 0) viewsat.setside(true);
 //           else viewsat.setside(false);
 //           i++;
 //       }

        azimuthsatint = azimuthsatint / (viewsats.size() + 1);
        conerplacesat = conerplacesat / (viewsats.size() + 1);

        azimuthsatint = 0;
        conerplacesat = 0;

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        //     azimutsat.setText("Азимут спутника: "+azimuthsatint);
        //     conersat.setText("Угол места спутника: "+ conerplacesat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_REQUEST_CODE_FOR_CAMERA);
            }
            openCamera();
            sensorManager.registerListener((SensorEventListener) this, magnite, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener((SensorEventListener) this, gsensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_REQUEST_CODE_FOR_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // startCameraActivity(); // запускаем активность с камерой (ну или фрагмент)
                openCamera();

            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "no permition camera", Toast.LENGTH_SHORT);
                toast.show();
                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_dashboard_to_navigation_home);
            }
        }
    }

    private void openCamera() {
        myCameras[CAMERA1].openCamera();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alphagravity = 0.97f;
        final float alphageomagnetic = 0.0001f;
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alphagravity * mGravity[0] + (1 - alphagravity)
                        * event.values[0];
                mGravity[1] = alphagravity * mGravity[1] + (1 -alphagravity)
                        * event.values[1];
                mGravity[2] = alphagravity * mGravity[2] + (1 - alphagravity)
                        * event.values[2];
                // mGravity = event.values;

                // Log.e(TAG, Float.toString(mGravity[0]));
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // mGeomagnetic = event.values;

                mGeomagnetic[0] = alphageomagnetic * mGeomagnetic[0] + (1 - alphageomagnetic)
                        * event.values[0];
                mGeomagnetic[1] = alphageomagnetic * mGeomagnetic[1] + (1 - alphageomagnetic)
                        * event.values[1];
                mGeomagnetic[2] = alphageomagnetic * mGeomagnetic[2] + (1 - alphageomagnetic)
                        * event.values[2];
                // Log.e(TAG, Float.toString(event.values[0]));

            }

            boolean success = SensorManager.getRotationMatrix(r, I, mGravity, mGeomagnetic);
            if (success) {
                if (timeold1 == 0) timeold1 = event.timestamp;
                float conerplace,coner,dy, xos,xorR, azimuth, azimuthcon;
                SensorManager.getOrientation(r, orientation);
                coner = (float) ((Math.PI / 2) + orientation[1]);
                conerplace = (float) Math.toDegrees(coner);
                azimuth = orientation[0]; // orientation
                if (azimuth<0) {
                    azimuth = (float) (azimuth + 2*PI);
                }

                // conerplace = (90+(int)Math.toDegrees(orientation[1]))%360; // orientation
                xorR = orientation[2];
                xos = (int) Math.toDegrees(orientation[2]); // orientation
                //xos = (xos);
                if ((xos < 90 && xos >= 0) | (xos>-90 && xos <= 0)) {
                    conerplace = - conerplace;
                    coner = -coner;
                    xos = -xos;
                }
                else {
                    if (xos <= 0 && xos > -180) xos = xos + 180;
                    else xos = xos - 180;
                    azimuth = (float) (azimuth - PI);
                }

                xos = (int) (xos * cos(orientation[1]));
                if (xorR<=0) xorR = (float) (xorR + PI);
                else xorR = (float) (xorR - PI);

                //Log.d("DEBUG", "xor= "+toDegrees(xorR));
                azimuth = (float) (azimuth - xorR*cos(coner));
                if (coner <0) azimuth = (float) (azimuth - PI);
                if (azimuth<0) {
                    //azimuth = -azimuth;
                    azimuth = (float) (azimuth + 2*PI);
                }

                azimuthcon = (float) Math.toDegrees(azimuth);

                // animation-----------------------------------------------------------------------

                if (event.timestamp-timeold1>140000000) {
                    timeold1 = event.timestamp;
                    azimut.setText("Azimuth: "+ (int)azimuthcon);
                    corner.setText("coner: " + (int)conerplace);
                    ////////////////////////////////////////////////////////
                    for (viewsat viewsat: viewsats) {
                        x1 = width / 2 - viewsat.getWight() / 2 + (int) dX((float) (rad(viewsat.getAzimut()) - azimuth));
                        y1 = height / 2 - viewsat.getHeight() / 2 + (int) -dY((float) (rad(viewsat.getConerplace()) - coner), 0);
                        if (viewsat.getAzimut() - azimuthcon > 90) {
                            x1 = x1 + 2000;
                        }
                        if (azimuthcon - viewsat.getAzimut() > 90) {
                            x1 = x1 - 2000;
                        }
                        viewsat.setX(x1);
                        viewsat.setY(y1);
                    }
                    ////////////////////////////////////////////////////////
                    int xG = -(imageLineGor.getWidth() - width)/2;
                    imageLineGor.setX(xG);
                    dy = height/2 + dY(coner,orientation[2]);
                    imageLineGor.setY(dy);
                    Animation animationGorRot = new RotateAnimation(xos, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
                    animationGorRot.setDuration(4000);
                    animationGorRot.setRepeatMode(Animation.REVERSE);
                    imageLineGor.startAnimation(animationGorRot);
                    //satelites.satelitteinfos.get
                    float dxx = x1 - width/2;
                    float dyy = y1 - height;
                    float dalpha = (float) toDegrees(atan(dyy/dxx));
                    if (x1 - width/2 < 0) dalpha = dalpha - 180;
                    Animation animatArrow = new RotateAnimation(dalpha, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
                    animatArrow.setDuration(5000);
//                    left.startAnimation(animatArrow);

                  //    Log.d("DEBUG", "azimut="+(int)azimuthcon+" xos= "+(int)xos+" coner= "+(int)conerplace);
                }
                ///------------------------------------------------------------------------------------
            }

        }
    }

    private float dX(float coner){
        return (float) (Lx*sin(coner));
    }

    private float dY(float coner, float xos){
        return (float) (Ly*sin(coner)*abs(cos(xos)));
    }

    private double rad(double coner) {
        return (Math.PI*coner)/180;
    }

    //где g1 - долгота спутника, g2 - долгота места приема, v - широта места приема.
    private float conerplacesat(float longitudesat, float longitudeplace, float conersat) {
        //    g1 = 36;
        //     g2 = 37;
        //     v =56;
        float g2 = longitudesat;
        float v = longitudeplace;
        float g1 = conersat;
        g2 = (float) toRadians(g2);
        g1 = (float) toRadians(g1);
        v = (float) toRadians(v);
        float c1= (float) (cos(g2-g1)*cos(v)-0.151);
        float c2 = (float)(1-(cos(g2-g1)*cos(g2-g1)*cos(v)*cos(v)));
        return (float) toDegrees(Math.atan(c1/sqrt(c2)));
    }

    private float azimuthsat(float longitudesat, float longitudeplace, float conersat) {
        float g2 = longitudesat;
        float v = longitudeplace;
        float g1 = conersat;
        g2 = (float) toRadians(g2);
        g1 = (float) toRadians(g1);
        v = (float) toRadians(v);
        return (float) (180 + toDegrees(atan(tan(g2-g1)/sin(v))));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("DEBUG","accuracy " + accuracy);
    }

    public class CameraService {
        private String mCameraID;
        private CameraDevice mCameraDevice = null;
        private CameraCaptureSession mCaptureSession;

        public CameraService(CameraManager cameraManager, String cameraID) {
            mCameraManager = cameraManager;
            mCameraID = cameraID;
        }

        private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice camera) {
                mCameraDevice = camera;
                Log.i("DEBUG", "Open camera  with id:"+mCameraDevice.getId());
                createCameraPreviewSession();
            }

            @Override
            public void onDisconnected(CameraDevice camera) {
                mCameraDevice.close();
                Log.i("DEBUG", "disconnect camera  with id:"+mCameraDevice.getId());
                mCameraDevice = null;
            }

            @Override
            public void onError(CameraDevice camera, int error) {
                Log.i("DEBUG", "error! camera id:"+camera.getId()+" error:"+error);
            }
        };

        private void createCameraPreviewSession() {
            mTextureView.setSurfaceTextureListener(surfaceTextureListener);
        }

        TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                //              SurfaceTexture texture = mTextureView.getSurfaceTexture();
                //               texture.setDefaultBufferSize(720,480);
                //              Surface surface = new Surface(texture);
                Surface surface = new Surface(surfaceTexture);
                try {
                    final CaptureRequest.Builder builder =
                            mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                    builder.addTarget(surface);
                    mCameraDevice.createCaptureSession(Arrays.asList(surface),
                            new CameraCaptureSession.StateCallback() {
                                @Override
                                public void onConfigured(CameraCaptureSession session) {
                                    mCaptureSession = session;
                                    try {
                                        mCaptureSession.setRepeatingRequest(builder.build(),null,null);
                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onConfigureFailed(CameraCaptureSession session) { }}, null );
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        };

        public boolean isOpen() {
            if (mCameraDevice == null) {
                return false;
            } else {
                return true;
            }
        }

        public void openCamera() {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        mCameraManager.openCamera(mCameraID,mCameraCallback,null);
                    }
                }

            } catch (CameraAccessException e) {
                Log.i("LOG_TAG",e.getMessage());
            }
        }

        public void closeCamera() {

            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(myCameras[CAMERA1].isOpen()){myCameras[CAMERA1].closeCamera();}
        if(myCameras[CAMERA2].isOpen()){myCameras[CAMERA2].closeCamera();}
        sensorManager.unregisterListener(this);
    }

}
