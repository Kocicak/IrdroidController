package mourovo.homeircontroller.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import mourovo.homeircontroller.persistence.dao.ControllerDao;
import mourovo.homeircontroller.persistence.entities.Controller;

@Database(entities = {Controller.class}, version = 1)
public abstract class IRDatabase extends RoomDatabase {
    public abstract ControllerDao controllerDao();
}