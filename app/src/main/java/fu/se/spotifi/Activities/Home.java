package fu.se.spotifi.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fu.se.spotifi.R;

public class Home extends BaseActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeLayout), (v, insets) -> {
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBar.left, systemBar.top, systemBar.right, systemBar.bottom);
            return insets;
        });

        setupBottomNavigation();
    }
}

