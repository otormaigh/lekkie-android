# local
-keep class ie.pennylabs.lekkie.** { *; }

# https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports#android
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception%