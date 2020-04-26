package pro.butovanton.satellite;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

    public class CameraService  {
        private String mCameraID;
        private CameraDevice mCameraDevice = null;
        private CameraCaptureSession mCaptureSession;
        private CameraManager mCameraManager;
        private TextureView mTextureView;

        public CameraService(CameraManager cameraManager, String cameraID, TextureView textureVew) {
            mCameraManager = cameraManager;
            mCameraID = cameraID;
            mTextureView = textureVew;
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
            if (mTextureView.isAvailable()) {
                Surface surface = new Surface(mTextureView.getSurfaceTexture());
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

        }

        public boolean isOpen() {
            if (mCameraDevice == null) {
                return false;
            } else {
                return true;
            }
        }

        public void openCamera(Activity activity) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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

