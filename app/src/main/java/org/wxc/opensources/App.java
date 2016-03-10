package org.wxc.opensources;

import android.app.Application;

import com.facebook.stetho.Stetho;

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
