# TalesDB - ProGuard Rules
# Reglas específicas para proteger las clases importantes de la app

# ==================== MODELOS DE DATOS ====================
# Mantener todas las clases de datos para serialización JSON y SQLite
-keep class com.example.myapplication.data.Book { *; }
-keep class com.example.myapplication.data.Serie { *; }
-keep class com.example.myapplication.data.Movie { *; }

# ==================== GSON ====================
# Reglas para que Gson funcione correctamente con serialización
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ==================== VIEW BINDING ====================
# Proteger clases generadas por ViewBinding
-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(android.view.LayoutInflater);
    public static *** inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
}

# ==================== SQLITE ====================
# Mantener clases de base de datos SQLite
-keep class android.database.** { *; }
-keep class android.database.sqlite.** { *; }

# ==================== DATABASE HELPER ====================
# Proteger DatabaseHelper y sus métodos
-keep class com.example.myapplication.data.DatabaseHelper { *; }

# ==================== FRAGMENTS ====================
# Mantener constructores de fragments
-keep public class * extends androidx.fragment.app.Fragment {
    public <init>();
}

# ==================== ADAPTERS ====================
# Proteger adapters de RecyclerView
-keep class com.example.myapplication.adapters.** { *; }

# ==================== PREFERENCES ====================
# Mantener PreferencesManager y DateFormatHelper
-keep class com.example.myapplication.PreferencesManager { *; }
-keep class com.example.myapplication.DateFormatHelper { *; }

# ==================== DEBUGGING ====================
# Preservar información de líneas para debugging de crashes
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ==================== GENERAL ====================
# Mantener anotaciones
-keepattributes *Annotation*

# Mantener enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Mantener Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Mantener Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}