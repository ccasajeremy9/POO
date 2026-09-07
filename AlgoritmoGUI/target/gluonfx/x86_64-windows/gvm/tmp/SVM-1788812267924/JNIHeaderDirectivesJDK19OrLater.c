#include <stdio.h>
#include <stddef.h>
#include <memory.h>

#include "C:\INSTALL\graalvm-jdk-21.0.7+8.1\include\jni.h"

int JNIHeaderDirectivesJDK19OrLater() {
    printf("NativeCodeInfo:JNIHeaderDirectivesJDK19OrLater:ConstantInfo:JNI_VERSION_19:PropertyInfo:size=%llu\n", ((unsigned long long)sizeof(JNI_VERSION_19)));
    printf("NativeCodeInfo:JNIHeaderDirectivesJDK19OrLater:ConstantInfo:JNI_VERSION_19:PropertyInfo:signedness=$%s$\n", ((JNI_VERSION_19>=0 ? 1 : 0)) ? "UNSIGNED" : "SIGNED");
    printf("NativeCodeInfo:JNIHeaderDirectivesJDK19OrLater:ConstantInfo:JNI_VERSION_19:PropertyInfo:value=%llX\n", ((unsigned long long)JNI_VERSION_19));
    printf("NativeCodeInfo:JNIHeaderDirectivesJDK19OrLater:StructInfo:struct_JNINativeInterface_:PropertyInfo:size=%llu\n", ((unsigned long long)sizeof(struct JNINativeInterface_)));
    printf("NativeCodeInfo:JNIHeaderDirectivesJDK19OrLater:StructInfo:struct_JNINativeInterface_:StructFieldInfo:IsVirtualThread:PropertyInfo:size=%llu\n", ((unsigned long long)sizeof(((struct JNINativeInterface_ *) 0)->IsVirtualThread)));
    printf("NativeCodeInfo:JNIHeaderDirectivesJDK19OrLater:StructInfo:struct_JNINativeInterface_:StructFieldInfo:IsVirtualThread:PropertyInfo:offset=%llu\n", ((unsigned long long)offsetof(struct JNINativeInterface_, IsVirtualThread)));
    return 0;
}

int main(void) {
    return JNIHeaderDirectivesJDK19OrLater();
}
