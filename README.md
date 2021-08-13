# SingBike
Bike Sharing Android Application

This is our FYP of UOW.

### Tool Used:
- android studio
- java

### Application SDK Infor
- minimal SDK version -> 26
- targetted SDK version -> 30
- minimal API Level -> 26
- targetted API Level -> 30


### Usage of aditional libraries (Gradle Imports)
- com.google.android.material:1.3.0 (For BottomNavigationMenu and Text Buttons)
```
// import of google material library in Build.gradle (Module:app)
implementation 'com.google.android.material:1.3.0'
```
- androidx.fragment:fragment:1.3.3 (For Fragments and FragmentContainerView)
```
// import FragmentContainerView and Fragments
implementation 'androidx.fragment:fragment:1.3.3'
```
- androidx.swiperefreshlayout:swiperefreshlayout:1.1.0 (For SwipeRefreshLayout)
```
// import SwipeRefreshLayout
implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
```

- androidx.viewpager2:viewpager2:1.0.0
```
// import viewpager2 for intro slides
implementation "androidx.viewpager2:viewpager2:1.0.0"
```

- com.google.code.gson:gson:2.8.6
```
// gson import: to store objects in sharedpreferences
implementation "com.google.code.gson:gson:2.8.6"
```

- androidx.preference:preference:1.1.1
```
// androidx preferences manager
implementation "androidx.preference:preference:1.1.1"
```

- me.dm7.barcodescanner:zxing:1.9
```
// qr code scanner
implementation 'me.dm7.barcodescanner:zxing:1.9'
```

- com.squareup.retrofit2:retrofit:2.5.0
```
// handle networking and files(image) upload/download
implementation 'com.squareup.retrofit2:retrofit:2.5.0'
implementation 'com.squareup.retrofit2:converter-gson:2.5.0'
```

- com.google.android.gms:play-services-maps:17.0.1
```
// adding a map dependency
implementation 'com.google.android.gms:play-services-maps:17.0.1'
```

- com.google.android.gms:play-serices-location:18.0.0
```
// google location api
implementation "com.google.android.gms:play-services-location:18.0.0"
```
