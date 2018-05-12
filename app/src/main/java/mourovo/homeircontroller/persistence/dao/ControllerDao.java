package mourovo.homeircontroller.persistence.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import mourovo.homeircontroller.persistence.entities.Controller;

@Dao
public interface ControllerDao {

    @Insert
    public void insertController(Controller ctrl);

    @Update
    public void updateController(Controller ctrl);

    @Query("SELECT * FROM controller")
    public Controller[] getAll();
}
