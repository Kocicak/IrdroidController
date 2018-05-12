package mourovo.homeircontroller.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mourovo.homeircontroller.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditControllerFragment extends Fragment {



    public EditControllerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_controller, container, false);
    }

}
