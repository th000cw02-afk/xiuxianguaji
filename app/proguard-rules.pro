# 代码混淆规则 - 增强代码保护

# 保留必要的类和方法
-keep class com.idle.wenzixiuxian.** { *; }
-keepclassmembers class com.idle.wenzixiuxian.** {
    public <methods>; 
    public <fields>; 
}

# 保留Activity、Service、BroadcastReceiver等组件
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Application

# 保留View相关的类和方法
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# 保留 WebView JavaScript 桥接
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# 保留Compose相关的类
-keep class androidx.compose.** { *; }
-keep class kotlinx.coroutines.** { *; }

# 保留Kotlin相关特性
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# 保留序列化相关的类
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保留资源文件
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 保留注解
-keepattributes *Annotation*

# 优化配置
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# 混淆字典（使用无意义字符增强混淆效果）
-obfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt

# 删除调试信息
-renamesourcefileattribute SourceFile

# 日志抑制
-dontwarn **

# 字符串加密（如果使用字符串加密库）
#-keep class com.google.crypto.** { *; }

# 资源压缩配置
-dontshrink
-dontoptimize
-dontobfuscate