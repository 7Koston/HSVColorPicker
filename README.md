# Color Picker PreferenceX
[ ![API](https://img.shields.io/badge/API-9%2B-blue.svg?style=flat) ](https://android-arsenal.com/api?level=14)

This is color picker for AndroidX Preference Library.</br>
The original ColorPickerView was written by [Daniel Nilsson](https://github.com/danielnilsson9/color-picker-view)
Rewritten for Support Lib by [u1aryz](https://github.com/u1aryz/ColorPickerPreferenceCompat)


## Including in project

```gradle
dependencies {

}
```

## How to use
Add the `ColorPreference` to your preference xml.

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.preference.PreferenceScreen
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

  <com.pref.color.picker.ColorPreference
      android:defaultValue="0xFF4183C4"
      android:key="color1"
      android:persistent="false"
      android:title="Color 1"
      app:alphaSlider="true" />
  ...

</androidx.preference.PreferenceScreen>
```

You have to extend `ColorPreferenceFragmentCompat` to your fragment.

```java
public class MyPreferenceFragment extends ColorPreferenceFragmentCompat {

    @Override public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.my_pref);
    }
}
```

## License

```
Copyright 2017 Daniel Nilsson
Copyright 2017 u1aryz
Copyright 2019 7Koston

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```