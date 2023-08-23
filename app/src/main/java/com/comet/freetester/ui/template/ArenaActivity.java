package com.comet.freetester.ui.template;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.comet.freetester.R;
import com.comet.freetester.model.DeliveryDataModel;
import com.comet.freetester.model.FirebaseDatabaseListener;
import com.comet.freetester.util.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;

import java.lang.ref.WeakReference;

public class ArenaActivity extends AppCompatActivity {
    protected DeliveryDataModel dataModel;

    protected TabLayout topTab;
    protected TabLayout.OnTabSelectedListener topListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            onTopTabUpdated();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    protected void onTopTabUpdated() {

    }

    protected void onSearchUpdated() {
        updateContents();
    }

    protected int topTabIndex = 0;

    protected String searchStr;

    protected void initSearchBar() {
        EditText search_text = findViewById(R.id.search_text);
        if (search_text != null) {
            search_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    searchStr = search_text.getText().toString();
                    onSearchUpdated();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }

    protected void initViews() {
        dataModel = DeliveryDataModel.getInstance();
        uiHandler = new UIHandler(this);
        uiHandler.sendEmptyMessageDelayed(UIHandler.MSG_NEXT_TICK, getNextTickDelay());

        //topTab = findViewById(R.id.top_navigation);
        if (topTab != null) {
            TabLayout.Tab tab = topTab.getTabAt(topTabIndex);
            if (tab != null) {
                tab.select();
            }
            topTab.addOnTabSelectedListener(topListener);
        }
        initSearchBar();
    }

    public String triggerPath = null;
    private ChildEventListener listener = null;
    public boolean blockAddReload = false;

    protected void onTriggered() {}
    @Override
    public void onResume() {
        super.onResume();
        if (triggerPath != null && listener == null) {
            listener = dataModel.listenDb(triggerPath, blockAddReload, new FirebaseDatabaseListener() {
                @Override
                public void onSuccess() {
                    loadContents();
                    onTriggered();
                }

                @Override
                public void onFailure(DatabaseError error) {

                }
            });
        }
        loadContents();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (triggerPath != null && listener != null) {
            dataModel.cancelDbListen(listener, triggerPath);
            listener = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHandler.removeMessages(UIHandler.MSG_NEXT_TICK);
    }

    protected void loadContents() {

    }

    protected void updateContents() {

    }

    public void updateTickTimer() {

    }

    protected static class UIHandler extends Handler {
        public static final int MSG_SHOW_PROGRESS = 1000;
        public static final int MSG_HIDE_PROGRESS = 1001;
        public static final int MSG_UPDATE_CONTENTS = 1002;
        public static final int MSG_SHOW_INVALID = 1003;
        public static final int MSG_SHOW_FAIL = 1004;
        public static final int MSG_PHOTO_FAILURE = 1005;
        public static final int MSG_INIT_PLAYER = 1006;
        public static final int MSG_HIDE_PLAYER = 1007;
        public static final int MSG_UPDATE_RECENT = 1008;
        public static final int MSG_PHOTO_PROMPT = 1009;
        public static final int MSG_VIDEO_PROMPT = 1010;
        public static final int MSG_INVALID_PARAM = 1011;
        public static final int MSG_FINISH = 1012;
        public static final int MSG_NEXT_TICK = 1013;
        public static final int MSG_UPDATE_TABS = 1014;
        public static final int MSG_LOCATION_UPDATE = 1015;
        public static final int MSG_SIZE_CHECK = 1016;
        public static final int MSG_CATEGORY_CHECK = 1017;
        public static final int MSG_TAGS_CHECK = 1018;
        public static final int MSG_SHOW_DIALOG = 1019;
        public static final int MSG_SHOW_FAILURE = 1020;
        public static final int MSG_SHOW_NEXT = 1021;
        public static final int MSG_SHOW_MAIN = 1022;
        public static final int MSG_SHOW_NET_ERROR = 1023;
        public static final int MSG_SHOW_SHARE = 1024;
        public static final int MSG_SIGN_OUT = 1025;

        private WeakReference<ArenaActivity> obj;

        public UIHandler(ArenaActivity object) {
            obj = new WeakReference<ArenaActivity>(object);
        }

        @Override
        public void handleMessage(Message msg) {
            ArenaActivity object = obj.get();
            switch (msg.what) {
                case MSG_SHOW_PROGRESS:
                    object.showProgressDialog();
                    break;
                case MSG_HIDE_PROGRESS:
                    object.hideProgressDialog();
                    break;
                case MSG_UPDATE_CONTENTS:
                    object.updateContents();
                    break;
                case MSG_NEXT_TICK:
                    object.nextTick();
                    break;
                case MSG_UPDATE_TABS:
                    object.updateTabs();
                    break;
                case MSG_FINISH:
                    object.finish();
                    break;
                case MSG_SHOW_DIALOG:
                    Utils.showDialog(object, (String) msg.obj);
                    break;
                default:
                    object.handleMessage(msg.what, msg.obj);
                    break;
            }
        }
    }

    protected UIHandler uiHandler;
    protected ProgressDialog progressDialog;

    protected void showProgressDialog() {
        if(progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }

        progressDialog = new ProgressDialog(this, R.style.ProgressStyle);
        Utils.showProgressDialog(progressDialog);
    }

    protected void hideProgressDialog() {

        Utils.hideProgressDialog(progressDialog);
    }

    protected void handleMessage(int message, Object object) {}

    protected void nextTick() {
        updateTickTimer();
        uiHandler.sendEmptyMessageDelayed(UIHandler.MSG_NEXT_TICK, getNextTickDelay());
    }

    private long getNextTickDelay() {
        return 1000 - (System.currentTimeMillis() % 1000);
    }

    protected void updateTabs() {
    }

    public void reloadContents() {}
}
