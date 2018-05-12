package mourovo.homeircontroller.persistence.entities;

import android.arch.persistence.room.*;

@Entity
public class Controller {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
