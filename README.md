# Color Picker PreferenceX
[ ![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat) ](https://android-arsenal.com/api?level=14)
[![](https://jitpack.io/v/7Koston/pref-color-picker.svg)](https://jitpack.io/#7Koston/pref-color-picker)

This is color picker for AndroidX Preference Library rewritten by using `DialogPreference` and `PreferenceDialogFragmentCompat`.</br>

The original HoloColorPicker was written by [Marie Schweiz](https://github.com/LarsWerkman/HoloColorPicker). </br>
I was using fork of [Simon Kaiser](https://github.com/sikaiser/HoloColorPicker), and fixed a lot of bugs, also added new futures such as: HEX implementation of color and DayNight theming support.

<p align="center">
  <img src="https://github.com/7Koston/pref-color-picker/blob/master/screenshots/1.png" height="350">
  <img src="https://github.com/7Koston/pref-color-picker/blob/master/screenshots/2.png" height="350">
</p>
<p align="center">
  <img src="https://github.com/7Koston/pref-color-picker/blob/master/screenshots/3.png" width="350">
  <img src="https://github.com/7Koston/pref-color-picker/blob/master/screenshots/4.png" width="350">
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
  implementation 'com.github.7Koston:HSVColorPicker:1.1.5'
}
```

## How to use
Add the `ColorPreference` to your preference xml.

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.preference.PreferenceScreen
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

  <com.github.koston.preference.ColorPreference
    style="@style/AppTheme.PreferenceDialog"
    android:defaultValue="@color/colorAccent"
    android:key="color_first"
    android:summary="Some summary"
    android:title="Color 1"
    app:hueWheelRadius="@dimen/defaultWheelRadius"
    app:hueWheelThickness="@dimen/defaultWheelThickness"
    app:centerCircleRadius="@dimen/defaultCenterRadius"
    app:centerCircleHaloRadius="@dimen/defaultCenterHaloRadius"
    app:huePointerRadius="@dimen/defaultPointerRadius"
    app:huePointerHaloRadius="@dimen/defaultPointerHaloRadius"
    app:barThickness="@dimen/defaultBarThickness"
    app:barLength="@dimen/defaultBarLength"
    app:barPointerRadius="@dimen/defaultBarPointerRadius"
    app:barPointerHaloRadius="@dimen/defaultBarPointerHaloRadius"
    app:pointersHaloColor="@color/defaultPointerHaloColor"
    app:indicatorColorPreview="true"/>
  ...

</androidx.preference.PreferenceScreen>
```

You have to extend `ColorPreferenceFragmentCompat` to your fragment, or just override `onDisplayPreferenceDialog` method like in `ColorPreferenceFragmentCompat`.

```java
public class MyPreferenceFragment extends ColorPreferenceFragmentCompat {

    @Override public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.my_pref);
    }
}
```

Customization:

```xml
<!-- styles-night.xml -->
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <style name="Base.Theme.DayNight" parent="Theme.MaterialComponents.NoActionBar"/>
</resources>

<!-- styles.xml -->
<resources>

  <style name="Base.Theme.DayNight" parent="Theme.MaterialComponents.Light.NoActionBar"/>

  <style name="AppTheme.Base" parent="Base.Theme.DayNight">
    <!--BASE-->
    <item name="colorPrimary">@color/colorPrimary</item>
    <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
    <item name="colorAccent">@color/colorAccent</item>
    <item name="android:fitsSystemWindows">true</item>
    <item name="preferenceTheme">@style/PreferenceThemeOverlay</item>
    <item name="alertDialogTheme">@style/AppTheme.AlertDialog</item>
  </style>

  <!-- Style for a DialogPreference Entry -->
  <style name="AppTheme.PreferenceDialog" parent="Theme.MaterialComponents.DayNight.Dialog">
    <item name="positiveButtonText">@android:string/ok</item>
    <item name="negativeButtonText">@android:string/cancel</item>
  </style>

  <style name="AppTheme.AlertDialog" parent="Theme.MaterialComponents.DayNight.Dialog.Alert">
    <item name="buttonBarButtonStyle">@style/ButtonBarButtonStyle</item>
  </style>

  <style name="ButtonBarButtonStyle" parent="Widget.MaterialComponents.Button.TextButton.Dialog">
    <item name="android:textColor">@color/colorAccent</item>
  </style>

</resources>
```

## License

```
Copyright 2012 Marie Schweiz
Copyright 2018 Simon Kaiser
Copyright 2022 7Koston

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
