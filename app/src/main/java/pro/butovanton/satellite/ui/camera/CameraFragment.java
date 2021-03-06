package pro.butovanton.satellite.ui.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

import pro.butovanton.satellite.Azimuth;
import pro.butovanton.satellite.CameraService;
import pro.butovanton.satellite.R;
import pro.butovanton.satellite.Sat;
import pro.butovanton.satellite.ui.sats.satsViewModel;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;

public class CameraFragment extends Fragment implements SensorEventListener {

    private pro.butovanton.satellite.ui.sats.satsViewModel satsViewModel;

    private final int MY_REQUEST_CODE_FOR_CAMERA = 110;
    private final int MY_REQUEST_LOCATION = 111;
    CameraService[] myCameras = null;
    int id;

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

        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            // Получение списка камер с устройства
            myCameras = new CameraService[mCameraManager.getCameraIdList().length];
            for (String cameraID : mCameraManager.getCameraIdList()) {
                Log.i("DEBUG", "cameraID: " + cameraID);
                id = Integer.parseInt(cameraID);
                // создаем обработчик для камеры
            }
            myCameras[CAMERA1] = new CameraService(mCameraManager, "0", mTextureView);

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
        List<Integer> positions = new ArrayList<>();
        Location location = satsViewModel.getLocation();
        for(Sat sat : sats) {
     //   for (Sat sat : sats) {
            float azimutplacesat = Azimuth.azimuthsat(location, sat.getPosition());
            float conerplacesat = Azimuth.conerplacesat((float) location.getLongitude(), (float) location.getLatitude(), sat.getPosition());
            String name = Integer.toString(abs(sat.getPosition()));
            if (sat.getPosition() > 0) name = name + "E°";
                                  else name = name + "W°";      ////////////////ВРЕМЕННО КОГДА БУДУТ ЧЕКБОКСЫ НУЖНО УБРАТЬ
            if (conerplacesat > 0 && !positions.contains(sat.getPosition()) && sat.getPosition()%2 == 0) {
                positions.add(sat.getPosition());
                viewsats.add(new viewsat(getContext(), constraintLayout, azimutplacesat, conerplacesat, name));
            }
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_REQUEST_CODE_FOR_CAMERA);
            } else
              if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                  requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_LOCATION);
                  }
                  else {
                 satsViewModel.setLocation();
                               openCamera();
                 }
        }
        else {
            openCamera();
            satsViewModel.setLocation();
        }
        sensorManager.registerListener((SensorEventListener) this, magnite, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener((SensorEventListener) this, gsensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void openCamera() {
        myCameras[CAMERA1].openCamera(getActivity());
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
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                mGeomagnetic[0] = alphageomagnetic * mGeomagnetic[0] + (1 - alphageomagnetic)
                        * event.values[0];
                mGeomagnetic[1] = alphageomagnetic * mGeomagnetic[1] + (1 - alphageomagnetic)
                        * event.values[1];
                mGeomagnetic[2] = alphageomagnetic * mGeomagnetic[2] + (1 - alphageomagnetic)
                        * event.values[2];
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
                    animationGorRot.setDuration(3000);
                    animationGorRot.setRepeatMode(Animation.REVERSE);
                    imageLineGor.startAnimation(animationGorRot);
                    float dxx = x1 - width/2;
                    float dyy = y1 - height;
                    float dalpha = (float) toDegrees(atan(dyy/dxx));
                    if (x1 - width/2 < 0) dalpha = dalpha - 180;
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("DEBUG","accuracy " + accuracy);
    }

   @Override
    public void onPause() {
        super.onPause();
        if(myCameras[CAMERA1].isOpen()){myCameras[CAMERA1].closeCamera();}
        sensorManager.unregisterListener(this);
    }

}
