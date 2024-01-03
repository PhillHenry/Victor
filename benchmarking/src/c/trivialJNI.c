#include <stdio.h>
#include <jni.h>
#include <time.h>
#include <stdlib.h>

__attribute__((constructor))
void func()
{
  puts(__func__);
  srand(time(NULL));   // Initialization, should only be called once.
}


JNIEXPORT int JNICALL Java_trivialJNI_randInt(JNIEnv *env, jobject obj) {
    return rand();
}
