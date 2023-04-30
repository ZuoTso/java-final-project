package com.example.nckujavafinalproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Restaurant.class,Label.class}, version = 3, exportSchema = false)
public abstract class AppRoomDatabase extends RoomDatabase {

    public abstract RestaurantDao restaurantDao();
    public abstract LabelDao labelDao();

    private static volatile AppRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppRoomDatabase.class, "database")
                            .addCallback(sRoomDatabaseCallback)
// un-comment this line to migrate new version of database
//                             .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more restaurants, just add them.
                RestaurantDao restaurantDao = INSTANCE.restaurantDao();
                restaurantDao.deleteAll();

                // default restaurants
                Restaurant restaurant = new Restaurant("炒飯", "");
                restaurantDao.insert(restaurant);
                restaurant = new Restaurant("麥當勞","");
                restaurantDao.insert(restaurant);
                restaurant = new Restaurant("肯德基","");
                restaurantDao.insert(restaurant);

                // default labels
                LabelDao labelDao= INSTANCE.labelDao();
                labelDao.deleteAll();

                String[]defaultLabels={"<100塊", "100~200塊", ">200塊", "近", "遠", "等很久", "等很快"};
                for(int i=0;i<defaultLabels.length;i++){
                    labelDao.insert(new Label(defaultLabels[i]));
                }
            });
        }
    };
}