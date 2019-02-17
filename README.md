# Color Picker PreferenceX
[ ![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat) ](https://android-arsenal.com/api?level=14)
[![](https://jitpack.io/v/7Koston/pref-color-picker.svg)](https://jitpack.io/#7Koston/pref-color-picker)

This is color picker for AndroidX Preference Library.</br>
The original ColorPickerView was written by [Daniel Nilsson](https://github.com/danielnilsson9/color-picker-view). </br>
Rewritten for Support Lib by [u1aryz](https://github.com/u1aryz/ColorPickerPreferenceCompat).

<p align="center">
  <img src="https://github.com/7Koston/pref-color-picker/blob/master/screenshots/1.png" height="350">
  <img src="https://github.com/7Koston/pref-color-picker/blob/master/screenshots/2.png" width="350">
</p>

## Including in project

Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
    }
   }
```

Add the dependency:

```gradle
dependencies {
  implementation 'com.github.7Koston:pref-color-picker:master-SNAPSHOT'
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
      android:defaultValue="@color/yourColor"
      android:key="color1"
      android:title="Color 1"/>
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
