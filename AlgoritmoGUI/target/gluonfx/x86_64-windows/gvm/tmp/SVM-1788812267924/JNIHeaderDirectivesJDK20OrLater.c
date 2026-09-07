#include <stdio.h>
#include <stddef.h>
#include <memory.h>

#include "C:\INSTALL\graalvm-jdk-21.0.7+8.1\include\jni.h"

int JNIHeaderDirectivesJDK20OrLater() {
    printf("NativeCodeInfo:JNIHeaderDirectivesJDK20OrLater:ConstantInfo:JNI_VERSION_20:PropertyInfo:size=%llu\n", ((unsigned long long)sizeof(JNI_VERSION_20)));
    printf("NativeCodeInfo:JNIHeaderDirectivesJDK20OrLater:ConstantInfo:JNI_VERSION_20:PropertyInfo:signedness=$%s$\n", ((JNI_VERSION_20>=0 ? 1 : 0)) ? "UNSIGNED" : "SIGNED");
    printf("NativeCodeInfo:JNIHeaderDirectivesJDK20OrLater:ConstantInfo:JNI_VERSION_20:PropertyInfo:value=%llX\n", ((unsigned long long)JNI_VERSION_20));
    return 0;
}

int main(void) {
    return JNIHeaderDirectivesJDK20OrLater();
}
