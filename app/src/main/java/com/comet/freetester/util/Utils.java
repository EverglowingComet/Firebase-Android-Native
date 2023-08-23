package com.comet.freetester.util;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.comet.freetester.R;
import com.comet.freetester.model.FirebaseDatabaseListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.type.LatLng;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final String EXTRA = "extra";
    public static final String EXTRA_SEC = "extra_sec";
    public static final String EXTRA_EX = "extra_ex";

    public static final LatLng HOE_POINT = LatLng.newBuilder().setLatitude(43.179387).setLongitude(-79.248433).build();

    public static final int TARGET_WIDTH_LARGE = 900;
    public static final int TARGET_WIDTH_NORMAL = 500;
    public static final int TARGET_WIDTH_SMALL = 200;

    public static final int COACH_SIZE_LIMIT = 20000000;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int REQUEST_CALENDAR = 1001;
    private static String[] PERMISSIONS_CALENDAR = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };

    public static final double ONE_KG_IN_LB = 2.2046226218;
    public static final long TIME_SECOND_IN_MILLISECONDS = 1000;
    public static final long TIME_MINUTE_IN_MILLISECONDS = TIME_SECOND_IN_MILLISECONDS * 60;
    public static final long TIME_HOUR_IN_MILLISECONDS = TIME_MINUTE_IN_MILLISECONDS * 60;
    public static final long TIME_DAY_IN_MILLISECONDS = TIME_HOUR_IN_MILLISECONDS * 24;
    public static final long TIME_WEEK_IN_MILLISECONDS = TIME_DAY_IN_MILLISECONDS * 7;
    public static final long TIME_YEAR_IN_MILLISECONDS = TIME_WEEK_IN_MILLISECONDS * 54;

    public static final String TMP_PICTURE_PATH = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)+"/"+"tmp_picture"+".jpg";

    public static final String TMP_PICTURE_FILE_PATH = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)+"/"+"tmp_picture";

    public static final int PICK_IMAGE = 1001;
    public static final int PICK_VIDEO = 1002;
    public static final int PICK_FILE = 1003;
    @Nullable
    public static final String APP_NAME = "Live.";

    public static String getLargeCountString(int points) {
        if (points < 100000) {
            return String.valueOf(points);
        } else {
            float result = ((float) points) / 1000.0f;
            return String.format("%.2f", result) + "k";
        }
    }

    public static String getCountString(int points) {
        if (points < 1000) {
            return String.valueOf(points);
        } else {
            float result = ((float) points) / 1000.0f;
            return String.format("%.2f", result) + "k";
        }
    }

    public static String getAssetString(Context context, String title) {
        try {
            InputStream is = context.getAssets().open(title);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static void showPrivacyPolicy(Context context, FirebaseDatabaseListener listener) {
        String str = "Terms of Services.";
        try{
            InputStream is = context.getAssets().open("privacy_policy.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            str = new String(buffer);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        new AlertDialog.Builder(context)
                .setMessage(Html.fromHtml(str))
                .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onSuccess();
                    }
                }).setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onFailure(null);
                    }
                }).create().show();
    }

    public static interface EditBoxDialogCallback {
        public void onPositiveClick(String message);
    }

    public static void showEditBoxDialog(Context context, int titleId, int hintId, int type, final EditBoxDialogCallback listener) {

        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null);
        final EditText etComments = (EditText) view.findViewById(R.id.edit_text);
        etComments.setHint(hintId);
        etComments.setInputType(type);
        etComments.setSingleLine(true);

        new AlertDialog.Builder(context)
                .setTitle(titleId)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String location = etComments.getText().toString();

                        listener.onPositiveClick(location);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    public static void showEditBoxDialog(Context context, int titleId, int hintId, final EditBoxDialogCallback listener) {

        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null);
        final EditText etComments = (EditText) view.findViewById(R.id.edit_text);
        etComments.setHint(hintId);
        etComments.setSingleLine(true);

        new AlertDialog.Builder(context)
                .setTitle(titleId)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String location = etComments.getText().toString();

                        listener.onPositiveClick(location);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    public static void showEditBoxDialog(Context context, String title, String hint, final EditBoxDialogCallback listener) {

        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null);
        final EditText etComments = (EditText) view.findViewById(R.id.edit_text);
        etComments.setHint(hint);
        etComments.setSingleLine(true);

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String location = etComments.getText().toString();

                        listener.onPositiveClick(location);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    public static void showEditBoxDialog(Context context, String title, String hint, String placeholder, final EditBoxDialogCallback listener) {

        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null);
        final EditText etComments = (EditText) view.findViewById(R.id.edit_text);
        etComments.setHint(hint);
        etComments.setText(placeholder);
        etComments.setSingleLine(true);

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String location = etComments.getText().toString();

                        listener.onPositiveClick(location);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    public static void showEditBoxDialog(Context context, String title, int inputType, String hint, String placeholder, final EditBoxDialogCallback listener) {

        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null);
        final EditText etComments = (EditText) view.findViewById(R.id.edit_text);
        etComments.setHint(hint);
        etComments.setText(placeholder);
        etComments.setSingleLine(true);
        etComments.setInputType(inputType);

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String location = etComments.getText().toString();

                        listener.onPositiveClick(location);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    public static int compareLongValue(long o1, long o2) {
        if (o1 == o2) {
            return 0;
        } else if (o1 < o2) {
            return -1;
        } else {
            return 1;
        }
    }

    public static boolean compareString(@Nullable String searchStr, @Nullable String toCompare) {
        if (TextUtils.isEmpty(searchStr)) {
            return true;
        }
        if (toCompare != null && toCompare.toLowerCase().contains(searchStr.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static void showIntegerEditBoxDialog(Context context, String title, String hint, final EditBoxDialogCallback listener) {

        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null);
        final EditText etComments = (EditText) view.findViewById(R.id.edit_text);
        etComments.setHint(hint);
        etComments.setSingleLine(true);
        etComments.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String location = etComments.getText().toString();

                        listener.onPositiveClick(location);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    public static void showDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .create().show();
    }

    public static void showDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .create().show();
    }

    public static void showDialog(Context context, int message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .create().show();
    }

    public static void showDialog(Context context, int message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.ok, listener)
                .create().show();
    }

    public static void showQueryDialog(Context context, String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.yes, listener)
                .setNegativeButton(R.string.no, null)
                .create().show();
    }

    public static void showQueryDialog(Context context, String message, DialogInterface.OnClickListener yes, DialogInterface.OnClickListener no) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.yes, yes)
                .setNegativeButton(R.string.no, no)
                .create().show();
    }

    public static void showProgressDialog(ProgressDialog progressDialog) {
        progressDialog.setMessage("Please wait..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

    public static void hideProgressDialog(ProgressDialog progressDialog)
    {
        if(progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }

    public static void hideProgressDialog(Activity activity, final ProgressDialog progressDialog)
    {
        if(progressDialog != null && progressDialog.isShowing())
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.hide();
                }
            });
        }
    }

    public static String getSecondsString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(time));
    }

    public static String getSecString(double speed) {
        double seconds = speed > 0 ? 1000 / speed : 0;

        int hour = (int) (seconds / 3600);
        int min = (int) ((seconds % 3600) / 60);
        int sec = (int) (seconds % 60);
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, min, sec);
        } else {
            return String.format("%02d:%02d", min, sec);
        }
    }

    public static String getPaceString(double speed) {
        double seconds = speed > 0 ? 1000 / speed : 0;

        int hour = (int) (seconds / 3600);
        int min = (int) ((seconds % 3600) / 60);
        int sec = (int) (seconds % 60);
        if (hour > 0) {
            return String.format("%dh%d'%d\"", hour, min, sec);
        } else {
            return String.format("%d'%d\"", min, sec);
        }
    }

    public static String getPaceStr(long time) {
        double seconds = time / 1000;

        int hour = (int) (seconds / 3600);
        int min = (int) ((seconds % 3600) / 60);
        int sec = (int) (seconds % 60);
        if (hour > 0) {
            return String.format("%dh%d'%d\"", hour, min, sec);
        } else {
            return String.format("%d'%d\"", min, sec);
        }
    }

    public static String getGameTimeStr(long time) {
        double seconds = time / 1000;

        int hour = (int) (seconds / 3600);
        int min = (int) ((seconds % 3600) / 60);
        int sec = (int) (seconds % 60);
        if (hour > 0) {
            return String.format("%dh%d'", hour, min);
        } else {
            return String.format("%d'", min);
        }
    }

    public static String getSimpleDateString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        return format.format(new Date(time));
    }

    public static String getNumberDateString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(new Date(time));
    }

    public static String getDateTimeString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy, hh:mm aa");
        return format.format(new Date(time));
    }

    public static String getYearAndTimeString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm aa, yyyy");
        return format.format(new Date(time));
    }

    public static String getDateFormattedString(String formatStr, long time) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(new Date(time));
    }

    public static String getDateString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        return format.format(new Date(time));
    }

    public static String getTimeString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
        return format.format(new Date(time));
    }

    public static String getDateTimeString2Line(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy\nhh:mm aa");
        return format.format(new Date(time));
    }

    public static String getDayString(long j) {
        return new SimpleDateFormat("dd").format(new Date(j));
    }

    public static String getMonthString(long j) {
        return new SimpleDateFormat("MMM").format(new Date(j));
    }

    public static String getDurationString(long start, long end) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
        return simpleDateFormat.format(new Date(start)) + " ~ " + simpleDateFormat.format(new Date(end));
    }

    public static void showImageDialog(final Activity activity, final int requestCode) {
        new AlertDialog.Builder(activity)
                .setItems(R.array.pick_image, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();

                        if (which == 0) {
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
                        } else {
                            /*intent.setClass(activity, TakePictureActivity.class);
                            activity.startActivityForResult(intent, requestCode);*/
                            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                                activity.startActivityForResult(intent, requestCode);
                            }
                        }
                    }
                }).create().show();
    }

    public static void showImageDialog(final Activity activity, final Uri uri, final int requestCode) {
        new AlertDialog.Builder(activity)
                .setItems(R.array.pick_image, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();

                        if (which == 0) {
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
                        } else {
                            /*intent.setClass(activity, TakePictureActivity.class);
                            activity.startActivityForResult(intent, requestCode);*/
                            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            if (activity.getPackageManager().queryIntentActivities(intent, 0) != null) {
                                activity.startActivityForResult(intent, requestCode);
                            }
                        }
                    }
                }).create().show();
    }

    public static void performCrop(Activity context, Uri picUri, int requetCode) {
        // take care of exceptions
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);

            cropIntent.putExtra("return-data", true);

            context.startActivityForResult(cropIntent, requetCode);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(context, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static Matrix configureTransform(int viewWidth, int viewHeight, Size mPreviewSize, Activity context) {

        int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        return matrix;
    }

    public static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                         int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new Utils.CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new Utils.CompareSizesByArea());
        } else {
            Log.e("tag", "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    public static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {

            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }


    public static class ImageSaver implements Runnable {

        private final Image mImage;

        private final File mFile;

        public ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /*public static void showFacebookShareDialog(final Activity activity, final Uri videoUri) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ShareDialog.canShow(ShareContent.class)) {
                    ShareVideo video = new ShareVideo.Builder()
                            .setLocalUrl(videoUri)
                            .build();
                    ShareVideoContent content = new ShareVideoContent.Builder()
                            .setVideo(video)
                            .build();
                    ShareDialog.show(activity, content);
                } else {
                    new AlertDialog.Builder(activity)
                            .setMessage(R.string.no_facebook)
                            .setPositiveButton(R.string.ok, null)
                            .create().show();
                }
            }
        });
    }*/

    public static void showShareVideoDialog(final Activity activity, final Uri videoUri) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
                shareIntent.setType("video/mp4");
                activity.startActivity(Intent.createChooser(shareIntent, activity.getText(R.string.share_video)));
            }
        });
    }

    public static void verifyCalenadrPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED
        ) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CALENDAR,
                    REQUEST_CALENDAR
            );
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void verifyCameraStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static long timeDifference = 0;

    public static void checkServerTime() {

        FirebaseDatabase.getInstance().getReference("curTimestamp").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference("curTimestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            long now = (Long) dataSnapshot.getValue();

                            timeDifference = System.currentTimeMillis() - now;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            timeDifference = 0;
                        }
                    });
                } else {
                    timeDifference = 0;
                }
            }
        });
    }

    public static long getCurTime() {
        if (timeDifference < Utils.TIME_MINUTE_IN_MILLISECONDS * 10) {
            return System.currentTimeMillis();
        } else {
            return System.currentTimeMillis() - timeDifference;
        }
    }

    public static String getTimeFromNow(long j) {
        long diff = j - getDayStart(getCurTime());
        if (j < TIME_DAY_IN_MILLISECONDS && j > -TIME_DAY_IN_MILLISECONDS) {
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            return format.format(new Date(j));
        } else if (j < getNextYearStart(getCurTime()) && j >= getYearStart(getCurTime())) {
            SimpleDateFormat format = new SimpleDateFormat("dd MMM");
            return format.format(new Date(j));
        } else {
            SimpleDateFormat format = new SimpleDateFormat("MM, yyyy");
            return format.format(new Date(j));
        }
    }

    public static long getWeekStart(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        instance.set(Calendar.DAY_OF_WEEK, 1);
        return instance.getTimeInMillis();
    }

    public static long getDayStart(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTimeInMillis();
    }

    public static long getYearStart(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        instance.set(Calendar.MONTH, 0);
        instance.set(Calendar.DAY_OF_MONTH, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTimeInMillis();
    }

    public static long getNextYearStart(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(getYearStart(j));
        instance.set(Calendar.YEAR, instance.get(Calendar.YEAR) + 1);

        return instance.getTimeInMillis();
    }

    public static long getLastYearStart(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(getYearStart(j));
        instance.set(Calendar.YEAR, instance.get(Calendar.YEAR) - 1);

        return instance.getTimeInMillis();
    }

    public static long getSundayTime(long j) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        instance.set(Calendar.DAY_OF_WEEK, 1);
        return instance.getTimeInMillis();
    }

    public static long copyTime(long toCopy, long fromTime) {
        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(fromTime);
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        from.set(Calendar.MILLISECOND, 0);

        long diff = fromTime - from.getTimeInMillis();

        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(toCopy);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTimeInMillis() + diff;
    }

    public static long copyHour(long toCopy, long fromTime) {
        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(fromTime);

        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(toCopy);
        instance.set(Calendar.HOUR_OF_DAY, from.get(Calendar.HOUR_OF_DAY));

        return instance.getTimeInMillis();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String round(double value) {
        int places = 0;
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return String.valueOf(tmp / factor);
    }

    public static int getIntegerFromMap(HashMap<String, Integer> map, String uid) {
        if (map != null && map.containsKey(uid)) {
            return map.get(uid);
        }
        return 0;
    }

    public static double getDoubleFromMap(HashMap<String, Double> map, String uid) {
        if (map != null && map.containsKey(uid)) {
            return map.get(uid);
        }
        return 0;
    }

    public static long getLongFromMap(HashMap<String, Long> map, String uid) {
        if (map != null && map.containsKey(uid)) {
            return map.get(uid);
        }
        return 0;
    }

    public static void setImageUri(Context context, ImageView view, String uri, int resId) {
        if (!TextUtils.isEmpty(uri)) {
            Picasso.with(context).load(uri).resize(TARGET_WIDTH_NORMAL, 0).placeholder(resId).into(view);
        } else {
            view.setImageResource(resId);
        }
    }

    public static void setImageUri(Context context, ImageView view, String uri) {
        if (!TextUtils.isEmpty(uri)) {
            Picasso.with(context).load(uri).resize(TARGET_WIDTH_NORMAL, 0).into(view);
        }
    }

    public static void setLargeImageUri(Context context, ImageView view, String uri, int resId) {
        if (!TextUtils.isEmpty(uri)) {
            Picasso.with(context).load(uri).resize(TARGET_WIDTH_LARGE, 0).placeholder(resId).into(view);
        } else {
            view.setImageResource(resId);
        }
    }

    public static void setLargeImageUri(Context context, ImageView view, String uri) {
        Picasso.with(context).load(uri).resize(TARGET_WIDTH_LARGE, 0).into(view);
    }

    public static void setSmallImageUri(Context context, ImageView view, String uri, int resId) {
        if (!TextUtils.isEmpty(uri)) {
            Picasso.with(context).load(uri).resize(TARGET_WIDTH_SMALL, 0).placeholder(resId).into(view);
        } else {
            view.setImageResource(resId);
        }
    }

    public static void updateTabLayout(TabLayout tabLayout, int index, int count) {
        if (count == 0) {
            tabLayout.getTabAt(index).removeBadge();
        } else {
            tabLayout.getTabAt(index).getOrCreateBadge().setNumber(count);
        }
    }

    public static String getDistanceStr(double distance) {
        if (distance < 1000) {
            return String.format("%.2f", distance) + "m";
        } else {
            return String.format("%.2f", distance / 1000) + "km";
        }
    }

    public static interface DialogListener {
        public void onStart();
        public void onSuccess();
        public void onFailure(String error);
    }

    public static long getFileSize(Context context, Uri fileUri) {
        Cursor returnCursor = context.getContentResolver().
                query(fileUri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        return returnCursor.getLong(sizeIndex);
    }

    public static ArrayList<String> tagsFromStr(String tags) {
        ArrayList<String> result = new ArrayList<>();

        try {
            String[] tagArr = tags.split(",");
            for (int i = 0; i < tagArr.length; i ++) {
                result.add(tagArr[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String tagsToStr(ArrayList<String> tags) {
        if (tags.size() == 0) {
            return null;
        }
        String result = "";

        for (String item : tags) {
            result += (result.equals("") ? "" : ",") + item;
        }
        return result;
    }

    public static boolean checkTags(String categoryTags, String toCheck) {
        if (TextUtils.isEmpty(categoryTags)) {
            return true;
        }
        ArrayList<String> catTags = tagsFromStr(categoryTags);
        for (String item: tagsFromStr(toCheck)) {
            if (catTags.contains(item)) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkIds(ArrayList<String> toCheck, ArrayList<String> ids) {
        for (String item: toCheck) {
            if (ids.contains(item)) {
                return true;
            }
        }

        return false;
    }

    public static String refTimeString(Context context, long time) {

        Date pasTime = new Date(time);

        Date nowTime = new Date();

        long dateDiff = nowTime.getTime() - pasTime.getTime();

        long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
        long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
        long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
        long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

        String convTime;

        if (second < 60) {
            convTime = String.format(context.getString(R.string.time_sec_ago), second);
        } else if (minute < 60) {
            convTime = String.format(context.getString(R.string.time_min_ago), minute);
        } else if (hour < 24) {
            convTime = String.format(context.getString(R.string.time_hour_ago), hour);
        } else if (day >= 7) {
            if (day > 30) {
                convTime = Utils.getNumberDateString(time);
            } else {
                convTime = String.format(context.getString(R.string.time_week_ago), day / 7);
            }
        } else {
            convTime = String.format(context.getString(R.string.time_day_ago), day);
        }

        return convTime;
    }

    public static String refTimeString(Context context, long time, long original) {

        Date pasTime = new Date(original);

        Date nowTime = new Date(time);

        long dateDiff = nowTime.getTime() - pasTime.getTime();

        long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
        long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
        long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
        long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

        String convTime;

        if (second < 60) {
            convTime = String.format(context.getString(R.string.time_sec_long), second);
        } else if (minute < 60) {
            convTime = String.format(context.getString(R.string.time_min_long), minute);
        } else if (hour < 24) {
            convTime = String.format(context.getString(R.string.time_hour_long), hour);
        } else if (day >= 7) {
            if (day > 30) {
                convTime = Utils.getNumberDateString(time);
            } else {
                convTime = String.format(context.getString(R.string.time_week_long), day / 7);
            }
        } else {
            convTime = String.format(context.getString(R.string.time_day_long), day);
        }

        return convTime;
    }

    public static String getYouTubeId(String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static @NonNull HashMap<String, Object> getDataMap(HttpsCallableResult result) {
        if (result.getData() instanceof HashMap) {
            return (HashMap<String, Object>) result.getData();
        } else {
            return new HashMap<>();
        }
    }

    public static HashMap<String, Object> getDataMapForKey(HashMap<String, Object> result, String key) {
        if (result.get(key) instanceof HashMap) {
            return (HashMap<String, Object>) result.get(key);
        } else {
            return new HashMap<>();
        }
    }

    public static HashMap<String, Object> toDataMap(Object result) {
        if (result instanceof HashMap) {
            return (HashMap<String, Object>) result;
        } else {
            return new HashMap<>();
        }
    }

    public static boolean checkSuccess(HashMap<String, Object> map) {
        Object item = map.get("success");
        if (item instanceof Boolean) {
            return (boolean) item;
        }
        return false;
    }

    public static String getCountDown(long deadline, long cur) {
        long seconds = deadline - cur;

        return Utils.getSecString(seconds);
    }
}
