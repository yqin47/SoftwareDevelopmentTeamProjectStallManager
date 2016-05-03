# StallManager

## Eclipse

To run on Eclipse, use the following versions in the Android SDK Manager (same as shown in lecture videos and used
for individual assignments previously):

* Android Build Tools, latest
* Android SDK Tools, latest
* Android SDK Platform-tools, latest
* Android Support Library, latest
* Android SDK 19 (4.4.2):
 * SDK Platform 19 rev 4
 * Samples for SDK 19 rev 6
 * ARM EABI v7a System Image 19 rev 2
 * Intel x86 Atom System Image 19 rev 2
 * Google APIs (x86 System Image) 19 rev 10
 * Google APIs (ARM System Image) 19 rev 10
 * Source for Android SDK 19 rev 2

## Android SDK on Linux

Roughly followed this guide: https://www.digitalocean.com/community/tutorials/how-to-build-android-apps-with-jenkins

Here are some adaptions to make that work on the Amazon EC2 instance.

Updated and installed some prerequisites:

    # apt-get update
    # sudo apt-get install libc6-i386 lib32stdc++6 lib32gcc1 lib32ncurses5 lib32z1

Retrieved list of available items to install with:
    android list sdk --all

We need the following for our Jenkins build server.

````
   4- Android SDK Build-tools, revision 21.1.2
   1- Android SDK Tools, revision 24.1.2
   2- Android SDK Platform-tools, revision 22
  24- SDK Platform Android 4.4.2, API 19, revision 4
  45- Samples for SDK API 19, revision 6
  72- ARM EABI v7a System Image, Android API 19, revision 2
  73- Intel x86 Atom System Image, Android API 19, revision 2
  89- Google APIs (x86 System Image), Android API 19, revision 10
  90- Google APIs (ARM System Image), Android API 19, revision 10
 119- Sources for Android SDK, API 19, revision 2
 125- Android Support Repository, revision 12
````

They were installed using the following command:

    android update sdk -u -a --filter 1,2,4,24,45,72,73,89,90,119,125

