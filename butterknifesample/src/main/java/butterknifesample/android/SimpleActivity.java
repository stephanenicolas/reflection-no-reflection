package butterknifesample.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.FindView;

import org.reflection_no_reflection.Class;

public class SimpleActivity extends Activity {

    @FindView(R.id.title) TextView title;
    @FindView(R.id.subtitle) TextView subtitle;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Contrived code to use the bound fields.
        title.setText("Butter Knife");
        subtitle.setText("Field and method binding for Android views.");
    }
}
