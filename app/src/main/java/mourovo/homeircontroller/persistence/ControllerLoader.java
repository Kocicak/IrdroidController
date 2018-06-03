package mourovo.homeircontroller.persistence;

import java.util.List;

import mourovo.homeircontroller.persistence.entities.Controller;

public interface ControllerLoader {

    void controllersLoaded(List<Controller> controllers);

}
