# Add project specific ProGuard rules here.
# For more details, see http://developer.android.com/guide/developing/tools/proguard.html

# Сохранять номера строк в трейсах (для разбора крашей в RuStore Console / Crashlytics)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ========= Ktor =========
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# ========= kotlinx.serialization =========
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class **$$serializer { *; }
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.umschool.umtasktracker.**$$serializer { *; }

# ========= Koin =========
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# ========= Kotlin Coroutines =========
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ========= DataStore =========
-keep class androidx.datastore.** { *; }

# ========= Общее — модели API в проекте =========
-keepclassmembers class com.umschool.umtasktracker.** {
    public <init>(...);
}
