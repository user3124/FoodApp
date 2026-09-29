# Требование ТЗ: читаемые стектрейсы в release-сборке
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Аннотации и generic-сигнатуры нужны Room, Hilt и корутинам
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Room: сущности не должны переименовываться, иначе поля таблиц разойдутся с кодом
-keep class com.example.foodapp.core.data.local.** { *; }