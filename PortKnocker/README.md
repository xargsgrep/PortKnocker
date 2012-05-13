# Port Knocker

## Description
A basic port knocker client for Android. Similar to the other port knocker apps but with an ICS style and more intuitive interface. Also includes a widget and the ability to choose an application to launch after knocking. The (relatively) larger size is due to the dependency on the ActionBarSherlock library by Jake Wharton (http://actionbarsherlock.com). The currently included version of ABS is 4.0.2.

## Usage
First create a host. You must specify a label, hostname and at least one port. The delay (in ms) is the amount of time it will wait in between sending packets. The default value of 1000ms is recommended to prevent the host from receiving the packets out of sequence. You can optionally choose an app to launch after the knock is complete. Once the host has been created you can initiate the knock sequence by simply clicking on the host. The same thing applies to the widget.

## Compiling from Command Line
#### Debug apk
Create a local.properties file (copy from sample) in both the PortKnocker and ActionBarSherlock directories. Update the sdk.dir property to point to the location of the Android SDK. Run 'ant debug' to create a debug apk.
#### Signed apk
For creating a signed release apk, create a build.properties file (copy from sample) in the PortKnocker directory. Update the properties accordingly. Increment the versionCode and versionName in the manifest and run 'ant release'.
