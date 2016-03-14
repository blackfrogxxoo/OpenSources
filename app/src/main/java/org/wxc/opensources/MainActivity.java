package org.wxc.opensources;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;
import org.wxc.opensources.event.MessageEvent;
import org.wxc.opensources.event.NetworkEvent;
import org.wxc.opensources.event.RefreshEvent;
import org.wxc.opensources.event.UrlEvent;
import org.wxc.opensources.retrofit.UserModel;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName();
    private SQLiteDatabase db;

    private EditText editText;
    private Button btnAdd;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private UserDao userDao;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // test branch

        // test user --> blackfrogxxoo

        EventBus.getDefault().register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView listView = (ListView) findViewById(R.id.list_view);
        editText = (EditText) findViewById(R.id.edit_text);
        btnAdd = (Button) findViewById(R.id.btn_add);

        daoMaster = App.getDaoMaster();
        daoSession = App.getDaoSession();
        db = daoMaster.getDatabase();
        userDao = daoSession.getUserDao();


        String loginColumns = UserDao.Properties.Login.columnName;
        String orderBy = loginColumns + " COLLATE LOCALIZED ASC";
        cursor = db.query(userDao.getTablename(), userDao.getAllColumns(), null, null, null, null, orderBy);

        UserListAdapter adapter = new UserListAdapter(this);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                new Thread() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            try {
                                this.wait(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        TextView tv = (TextView) listView.getChildAt(position).findViewById(android.R.id.text2);
                        EventBus.getDefault().post(new UrlEvent(tv.getText().toString()));
                    }
                }.start();
            }
        });

        addUiListeners();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MessageEvent("add_" + editText.getText().toString()));
                EventBus.getDefault().post(new NetworkEvent());
            }
        });


    }

    private class UserListAdapter extends BaseAdapter {

        private final Context mContext;
        private String[] logins;
        private String[] avatarUrls;

        private DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        public UserListAdapter(Context context) {
            this.mContext = context;
            if(cursor != null) {
                logins = new String[cursor.getCount()];
                avatarUrls = new String[cursor.getCount()];
            }
            int i=0;
            while(cursor.moveToNext()) {
                logins[i] = cursor.getString(
                        cursor.getColumnIndex(UserDao.Properties.Login.columnName));
                avatarUrls[i] = cursor.getString(
                        cursor.getColumnIndex(UserDao.Properties.AvatarUrl.columnName));
                i++;
            }
        }

        @Override
        public int getCount() {
            return logins.length;
        }

        @Override
        public Object getItem(int position) {
            return logins[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;
            if(view == null) {
                view = View.inflate(mContext, R.layout.layout_user_item, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
                viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            bindImageUrl(viewHolder.imageView, avatarUrls[position]);
            viewHolder.textView.setText(logins[position]);
            return view;
        }

        private void bindImageUrl(ImageView imageView, String url) {
            ImageLoader.getInstance().displayImage(url, imageView);
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUrlEvent(UrlEvent event) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(event.url));
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventException(SubscriberExceptionEvent event) {

    }

    protected void addUiListeners() {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    EventBus.getDefault().post(new MessageEvent("add_" + v.getText().toString()));
                    return true;
                }
                return false;
            }
        });

        final View button = findViewById(R.id.btn_add);
        button.setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;
                button.setEnabled(enable);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void addNote(MessageEvent event) {
        if (!event.message.startsWith("add")) {
            return;
        }

        String[] message = event.message.split("_");
        if (message.length < 2) {
            return;
        }

        // 网络请求
        String BASE_URL = "https://api.github.com";
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

        OkHttpClient client = new OkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        UserModel.ICreator userCreator = retrofit.create(UserModel.ICreator.class);
        Call<List<UserModel>> userCall = userCreator.contributors("square", "retrofit");

        userCall.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                List<UserModel> models = response.body();

                for(UserModel model:models) {
                    Log.i(TAG, model.toString());
                    User user = model.toUserEntity();
                    try{
                        userDao.insert(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                EventBus.getDefault().post(new RefreshEvent());

            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {

            }
        });


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshDB(RefreshEvent event) {
        editText.setText("");
        cursor.requery();
    }
}
