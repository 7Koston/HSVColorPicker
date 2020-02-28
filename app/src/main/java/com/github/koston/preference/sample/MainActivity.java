package com.github.koston.preference.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    SharedPreferences prefs =
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    if (prefs.getBoolean("theme", false)) {
      getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    } else {
      getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }
}
