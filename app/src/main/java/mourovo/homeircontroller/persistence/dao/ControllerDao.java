package mourovo.homeircontroller.persistence.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import mourovo.homeircontroller.persistence.entities.Controller;

@Dao
public interface ControllerDao {

    @Insert
    void insertController(Controller ctrl);

    @Update
    void updateController(Controller ctrl);

    @Query("SELECT * FROM controller")
    List<Controller> getAll();
}
