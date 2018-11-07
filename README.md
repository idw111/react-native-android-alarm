
# react-native-android-alarm

## Getting started

`$ npm install react-native-android-alarm --save`

### Mostly automatic installation

`$ react-native link react-native-android-alarm`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNAndroidAlarmPackage;` to the imports at the top of the file
  - Add `new RNAndroidAlarmPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-android-alarm'
  	project(':react-native-android-alarm').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-android-alarm/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-android-alarm')
  	```


## Usage
```javascript
import RNAndroidAlarm from 'react-native-android-alarm';

// TODO: What to do with the module?
RNAndroidAlarm;
```
  