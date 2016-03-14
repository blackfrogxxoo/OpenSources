package org.wxc.opensources;

import android.app.Application;
import android.os.Environment;

import com.facebook.stetho.Stetho;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

/**
 * Created by black on 2016/3/2.
 */
public class App extends Application {
    private static App mContext;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .diskCache(new UnlimitedDiskCache(new File(Environment.getExternalStorageDirectory().getPath() + "/OpenSources")))
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    public static App getInstance() {
        return mContext;
    }

    public static DaoMaster getDaoMaster() {
        if(daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(mContext, "notes-db", null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public static DaoSession getDaoSession() {
        if(daoSession == null) {
            if(daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
