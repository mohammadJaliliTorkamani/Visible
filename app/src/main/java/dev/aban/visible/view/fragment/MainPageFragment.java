package dev.aban.visible.view.fragment;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import dev.aban.visible.R;
import dev.aban.visible.adapter.DrawerAdapter;
import dev.aban.visible.adapter.FilterAdapter;
import dev.aban.visible.detector.BorderedText;
import dev.aban.visible.detector.Classifier;
import dev.aban.visible.detector.ImageUtils;
import dev.aban.visible.detector.MultiBoxTracker;
import dev.aban.visible.detector.OverlayView;
import dev.aban.visible.detector.TensorFlowObjectDetectionAPIModel;
import dev.aban.visible.model.BubbleItem;
import dev.aban.visible.model.User;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import dev.aban.visible.utils.AudioPlayer;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.FlipAnimation;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.TextViewPlus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dev.aban.visible.utils.Constants.LOGGER;

public class MainPageFragment extends Fragment implements
        ImageReader.OnImageAvailableListener, DrawerLayout.DrawerListener, View.OnClickListener {

    private static int shine_counter;
    private View view;
    private ValueAnimator animator;

    private DrawerLayout drawerLayout;
    private ImageView expand;
    private ImageView filter;
    private ImageView capture;
    private ImageView changeCamera;
    private TextViewPlus developerName;
    private TextViewPlus getPRO;
    private ImageView developerImage;
    private TextViewPlus header_name;
    private ImageView header_shineview;

    private RecyclerView drawer_rv;
    private RecyclerView.Adapter drawer_adapter;
    private RecyclerView.LayoutManager drawer_layout_manager;

    private CoordinatorLayout dialog_marketContainer;
    private RecyclerView dialog_RV;
    private TextViewPlus dialog_RV_empty;
    private RecyclerView.Adapter dialog_adapter;
    private RecyclerView.LayoutManager dialog_layout_manager;
    private Dialog dialog_dialog;

    private Handler main_handler;
    private Handler handler;

    private HandlerThread handlerThread;
    private boolean isProcessingFrame = false;
    private int previewWidth = 0;
    private int previewHeight = 0;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;
    private boolean computingDetection = false;
    private long timestamp = 0;
    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private MultiBoxTracker tracker;
    private byte[] luminanceCopy;
    private BorderedText borderedText;
    private Integer sensorOrientation;
    private Classifier detector;
    private OverlayView trackingOverlay;
    private byte[][] yuvBytes = new byte[3][];
    private int[] rgbBytes = null;
    private int yRowStride;
    private Runnable postInferenceCallback;
    private Runnable imageConverter;

    private List<BubbleItem> bubbleItems = new LinkedList<>();


    private BubbleItem currentFilter;
    private boolean permitToDetect = false;


    private int cameraType = CameraCharacteristics.LENS_FACING_BACK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_main_page, container, false);

        Helper.recordEventView("MainpageFragment");
        findViews();
        initialize();
        manageListeners();
        startAnimations();
        return view;
    }

    private void initialize() {
        initializeDrawer();
        initDialog();
    }

    private void startAnimations() {
        startFilterRotateAnimation();
        startShineAnimation();
    }

    private void shine(TextViewPlus textViewPlus, ImageView shineView) {
        Animation animation = new TranslateAnimation(0, textViewPlus.getWidth() + shineView.getWidth(), 0, 0);
        animation.setDuration(550);
        animation.setFillAfter(false);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        shineView.startAnimation(animation);
    }

    private void startShineAnimation() {
        if (main_handler == null) {
            main_handler = new Handler();
            main_handler.postDelayed(new Runnable() {
                public void run() {
                    shine(header_name, header_shineview);
                    main_handler.postDelayed(this, 4000);

                    shine_counter++;
                    if (shine_counter % 8 == 0) {
                        shine_counter = 0;
                    }
                }
            }, 4000);
        }
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(ContextHelper.retrieveContext()).inflate(R.layout.dialog_filter, null, false);
        builder.setView(view);

        dialog_marketContainer = view.findViewById(R.id.dialog_filter_bubble_market);
        dialog_RV_empty = view.findViewById(R.id.dialog_filter_rv_empty);
        dialog_RV = view.findViewById(R.id.dialog_filter_rv);

        dialog_RV.setHasFixedSize(true);
        dialog_layout_manager = new GridLayoutManager(ContextHelper.retrieveContext(), 3, RecyclerView.VERTICAL, false);
        dialog_RV.setLayoutManager(dialog_layout_manager);

        dialog_dialog = builder.create();
        dialog_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_dialog.setOnDismissListener(dialog1 -> animator.start());
        dialog_dialog.setOnShowListener(dialog1 -> animator.pause());


        dialog_marketContainer.setOnClickListener(v -> {
            Helper.simpleAddFragment(getFragmentManager(), new MarketFragment());
            dialog_dialog.dismiss();
        });
        dialog_adapter = new FilterAdapter(bubbleItems, item -> {
            if (currentFilter != item) {
                currentFilter = item;
                permitToDetect = true;
                setFragment();
                dialog_dialog.dismiss();
            }
        }, dialog_dialog);

        dialog_RV.setAdapter(dialog_adapter);
    }

    private void startFilterRotateAnimation() {
        animator = ValueAnimator.ofFloat(1F, 0.8F);
        animator.setDuration(4000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            filter.setScaleX(scale);
            filter.setScaleY(scale);
            filter.setRotation(filter.getRotation() + 2);
        });
        animator.start();
    }

    private void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);
        tracker = new MultiBoxTracker(getActivity());
        int cropSize = Constants.TF_OD_API_INPUT_SIZE;

        try {
            detector = TensorFlowObjectDetectionAPIModel.create(
                    BubbleItem.getModelPathWithID(currentFilter.getId()),
                    BubbleItem.getLabelPathWithID(currentFilter.getId()),
                    Constants.TF_OD_API_INPUT_SIZE);
            cropSize = Constants.TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            Helper.showToast(getActivity(), "Classifier could not be initialized");
            getActivity().finish();
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();
        sensorOrientation = rotation - Helper.getScreenOrientation(getActivity());
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);
        frameToCropTransform = ImageUtils.getTransformationMatrix(previewWidth, previewHeight, cropSize, cropSize, sensorOrientation, false);
        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);
        trackingOverlay = view.findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(canvas -> tracker.draw(canvas));
    }

    private String chooseCamera(int cameraType) {
        int antiCameraType = Helper.getAntiCameraType(cameraType);
        final CameraManager manager = (CameraManager) ContextHelper.retrieveContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            for (final String cameraId : manager.getCameraIdList()) {
                final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == antiCameraType)
                    continue;
                final StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null)
                    continue;
                boolean useCamera2API = Helper.isHardwareLevelSupported(characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
                return cameraId;
            }
        } catch (CameraAccessException e) {
            Log.d(Constants.TAG, e.getMessage());
        }

        return null;
    }


    private void setFragment() {
        if (permitToDetect) {
            CameraConnectionFragment camera2Fragment = CameraConnectionFragment.newInstance(
                    (size, rotation) -> {
                        previewHeight = size.getHeight();
                        previewWidth = size.getWidth();
                        onPreviewSizeChosen(size, rotation);
                    },
                    MainPageFragment.this::onImageAvailable, R.layout.fragment_camera_connection, Constants.INPUT_IMAGE_SIZE);

            camera2Fragment.setCamera(chooseCamera(cameraType));
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.camera_view, camera2Fragment)
                    .commit();
        }
    }

    private void initializeDrawer() {
        drawer_rv.setHasFixedSize(true);
        drawer_layout_manager = new LinearLayoutManager(ContextHelper.retrieveContext(), RecyclerView.VERTICAL, false);
        drawer_rv.setLayoutManager(drawer_layout_manager);
        drawer_adapter = new DrawerAdapter(item -> handleDrawerItems(item.getPosition()));
        drawer_rv.setAdapter(drawer_adapter);
    }

    private void handleDrawerItems(int position) {
        if (drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.closeDrawer(GravityCompat.END);

        switch (position) {
            case 0:
                Helper.simpleAddFragment(getFragmentManager(), new MarketFragment());
                break;
            case 1:
                Helper.simpleAddFragment(getFragmentManager(), new SettingFragment());
                break;
            case 2:
                Helper.simpleAddFragment(getFragmentManager(), new DonateFragment());
                break;
            case 3:
                Helper.simpleAddFragment(getFragmentManager(), new AboutFragment());
                break;
            case 4:
                Helper.simpleAddFragment(getFragmentManager(), new MoreFragment());
                break;
            case 5:
                Helper.recordEventClick("MainpageFragment", "ShareApp");
                Helper.shareApp();
                break;
        }
    }

    private void manageListeners() {
        getPRO.setOnClickListener(this);
        developerImage.setOnClickListener(this);
        expand.setOnClickListener(this);
        filter.setOnClickListener(this);
        capture.setOnClickListener(this);
        changeCamera.setOnClickListener(this);
        drawerLayout.addDrawerListener(this);
    }

    private void findViews() {
        header_shineview = view.findViewById(R.id.fragment_main_shine_imageview);
        header_name = view.findViewById(R.id.fragment_main_header_name);
        expand = view.findViewById(R.id.fragment_main_toolbar_expand_iv);
        drawerLayout = view.findViewById(R.id.fragment_main_drawer_layout);
        drawer_rv = view.findViewById(R.id.navigation_drawer_rv);
        developerName = view.findViewById(R.id.navigation_drawer_developer_name);
        developerImage = view.findViewById(R.id.navigation_drawer_developer_image);
        getPRO = view.findViewById(R.id.get_pro_version);
        filter = view.findViewById(R.id.fragment_main_filter);
        capture = view.findViewById(R.id.fragment_main_capture);
        changeCamera = view.findViewById(R.id.fragment_main_camera_change);
    }

    @Override
    public void onClick(View v) {
        if (drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.closeDrawer(GravityCompat.END);
        switch (v.getId()) {
            case R.id.fragment_main_toolbar_expand_iv:
                Helper.recordEventClick("MainpageFragment", "ExpandToolbar");
                expandHandle();
                break;
            case R.id.navigation_drawer_developer_image:
                Helper.simpleAddFragment(getFragmentManager(), new ProfileFragment());
                break;
            case R.id.fragment_main_filter:
                Helper.recordEventClick("MainpageFragment", "Filter");
                filterHandle();
                break;
            case R.id.fragment_main_camera_change:
                Helper.recordEventClick("MainpageFragment", "Change Camera");
                cameraChangeHandle();
                break;

            case R.id.get_pro_version:
                Helper.recordEventClick("MainpageFragment", "Get PRO Version");
                Helper.getProVersion();
                break;
            case R.id.fragment_main_capture:
                Helper.recordEventClick("MainpageFragment", "Capture");
                if (currentFilter == null)
                    Helper.showToast(getActivity(), "Select Bubble to turn on the camera !");
                else {
                    capture.startAnimation(Constants.FAST_SCALE_ANIMATION);
                    new AudioPlayer().play(R.raw.capture_sound);
                    Helper.showToast(getActivity(), "Coming soon !");
                }
                break;
        }
    }

    private void cameraChangeHandle() {
        toggleCameraType();
        FlipAnimation flipAnimation = new FlipAnimation(changeCamera, changeCamera);
        if (changeCamera.getVisibility() == View.GONE) {
            flipAnimation.reverse();
            changeCamera.startAnimation(flipAnimation);
        } else {
            changeCamera.startAnimation(flipAnimation);
        }

        setFragment();
    }

    private void toggleCameraType() {
        cameraType = cameraType == CameraCharacteristics.LENS_FACING_FRONT ? CameraCharacteristics.LENS_FACING_BACK : CameraCharacteristics.LENS_FACING_FRONT;
    }

    private void filterHandle() {
        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    showDialog();

                    ServiceGenerator.getInstance().createService(ClientApi.class).getCurrentItems().enqueue(new Callback<List<BubbleItem>>() {
                        @Override
                        public void onResponse(Call<List<BubbleItem>> call, Response<List<BubbleItem>> response) {
                            if (response.body() != null) {
                                bubbleItems.clear();
                                bubbleItems.addAll(BubbleItem.getLocallyExistBubbleItems(response.body()));
                                displayAvailablesEmpty(bubbleItems.isEmpty());
                                if (bubbleItems.isEmpty()) {
                                    permitToDetect = false;
                                } else {
                                    dialog_adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<BubbleItem>> call, Throwable t) {
                            Log.d(Constants.TAG, t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(error -> Log.d(Constants.TAG, "Error while get permission")).check();
    }

    private void showDialog() {
        dialog_dialog.show();
    }

    private void expandHandle() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    @Override
    public synchronized void onPause() {

        if (!getActivity().isFinishing()) {
            getActivity().finish();
        }

        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException e) {
            Log.e(Constants.TAG, e.getMessage());
        }

        super.onPause();
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        if (previewWidth == 0 || previewHeight == 0)
            return;
        if (rgbBytes == null)
            rgbBytes = new int[previewWidth * previewHeight];
        try {
            final Image image = reader.acquireLatestImage();

            if (image == null)
                return;

            if (isProcessingFrame) {
                image.close();
                return;
            }
            isProcessingFrame = true;
            Trace.beginSection("imageAvailable");
            final Image.Plane[] planes = image.getPlanes();
            fillBytes(planes, yuvBytes);
            yRowStride = planes[0].getRowStride();
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();

            imageConverter = () -> ImageUtils.convertYUV420ToARGB8888(yuvBytes[0], yuvBytes[1], yuvBytes[2],
                    previewWidth, previewHeight, yRowStride, uvRowStride, uvPixelStride, rgbBytes);

            postInferenceCallback = () -> {
                image.close();
                isProcessingFrame = false;
            };

            processImage();
        } catch (final Exception e) {
            LOGGER.e(e, "Exception!");
            Trace.endSection();
            return;
        }
        Trace.endSection();
    }

    private void fillBytes(final Image.Plane[] planes, final byte[][] yuvBytes) {
        for (int i = 0; i < planes.length; ++i) {
            final ByteBuffer buffer = planes[i].getBuffer();
            if (yuvBytes[i] == null) {
                LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
                yuvBytes[i] = new byte[buffer.capacity()];
            }
            buffer.get(yuvBytes[i]);
        }
    }

    private int[] getRgbBytes() {
        imageConverter.run();
        return rgbBytes;
    }

    private byte[] getLuminance() {
        return yuvBytes[0];
    }

    private void readyForNextImage() {
        if (postInferenceCallback != null) {
            postInferenceCallback.run();
        }
    }

    private synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }

    private void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        byte[] originalLuminance = getLuminance();
        tracker.onFrame(
                previewWidth,
                previewHeight,
                sensorOrientation);
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        if (luminanceCopy == null) {
            luminanceCopy = new byte[originalLuminance.length];
        }
        System.arraycopy(originalLuminance, 0, luminanceCopy, 0, originalLuminance.length);
        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

        runInBackground(() -> {
            LOGGER.i("Running detection on image " + currTimestamp);
            final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
            cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
            final Canvas canvas1 = new Canvas(cropCopyBitmap);
            final Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.0f);

            final List<Classifier.Recognition> mappedRecognitions = new LinkedList<>();
            for (final Classifier.Recognition result : results) {
                final RectF location = result.getLocation();
                if (location != null && result.getConfidence() >= Constants.MINIMUM_CONFIDENCE_TF_OD_API) {
                    LOGGER.i("Title: " + result.getTitle());
                    canvas1.drawRect(location, paint);
                    cropToFrameTransform.mapRect(location);
                    result.setLocation(location);
                    new AudioPlayer().play(R.raw.bubble_sound);
                    mappedRecognitions.add(result);
                }
            }

            tracker.trackResults(mappedRecognitions);
            trackingOverlay.postInvalidate();
            computingDetection = false;
        });
    }

    private void displayAvailablesEmpty(boolean display) {
        dialog_RV_empty.setVisibility(display ? View.VISIBLE : View.GONE);
//        dialog_RV.setVisibility(!display ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        ServiceGenerator.getInstance().createService(ClientApi.class).getProfilePictures()
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.body() != null) {
                            developerName.setText(response.body().getName());
                            Picasso.get().load(response.body().getImageURL()).into(developerImage);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public synchronized void onStop() {
        super.onStop();
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
    }

    @Override
    public synchronized void onStart() {
        super.onStart();
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }
}