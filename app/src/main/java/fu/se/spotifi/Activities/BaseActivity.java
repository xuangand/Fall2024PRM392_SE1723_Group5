package fu.se.spotifi.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home && !(this instanceof Home)) {
                startActivity(new Intent(this, Home.class));
                return true;
            } else if (itemId == R.id.library && !(this instanceof Library)) {
                startActivity(new Intent(this, Library.class));
                return true;

            }else if (itemId == R.id.search && !(this instanceof SearchActivity)) {
                startActivity(new Intent(this, SearchActivity.class));

                return true;
            }
            // Add other menu items here
            return false;
        });

        // Set the correct item as selected
        setSelectedNavigationItem();
    }

    protected void setSelectedNavigationItem() {
        if (bottomNavigationView != null) {
            int itemId;
            if (this instanceof Home) {
                itemId = R.id.home;
            } else if (this instanceof Library) {
                itemId = R.id.library;

            } else if(this instanceof SearchActivity){
                itemId = R.id.search;
            }else {
                // Default or handle other activities
                return;
            }

            bottomNavigationView.setSelectedItemId(itemId);
        }
    }
}
