#include "TerminalHandle.h"
#include <conio.h>
#include <Windows.h>
#include <iostream>

std::string parseStr(JNIEnv* env, jstring jStr) {
    if (!jStr)
        return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray)env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

    size_t length = (size_t)env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char*)pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

jintArray toIntArray(int l[], JNIEnv* env) {
    jintArray newArray = env->NewIntArray(2);
    env->SetIntArrayRegion(newArray, 0, 2, reinterpret_cast<jint*>(l));
    return newArray;
}


JNIEXPORT jint JNICALL Java_net_pascal_terminal_TerminalHandle_getch
(JNIEnv*, jobject) {
    int i = _getch();
    return i;
}

/*
 * Class:     net_pascal_terminal_TerminalHandle
 * Method:    getSize
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_net_pascal_terminal_TerminalHandle_getSize
(JNIEnv* e, jobject) {
    CONSOLE_SCREEN_BUFFER_INFO csbi;
    int columns, rows;
    GetConsoleScreenBufferInfo(GetStdHandle(STD_OUTPUT_HANDLE), &csbi);
    rows = csbi.srWindow.Bottom - csbi.srWindow.Top + 1;
    columns = csbi.srWindow.Right - csbi.srWindow.Left + 1;
    int i[] = { columns, rows };
    return toIntArray(i, e);
}

/*
 * Class:     net_pascal_terminal_TerminalHandle
 * Method:    resetInputBuffer
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_pascal_terminal_TerminalHandle_resetInputBuffer
(JNIEnv*, jobject) {
    FlushConsoleInputBuffer(GetStdHandle(STD_INPUT_HANDLE));
}

/*
 * Class:     net_pascal_terminal_TerminalHandle
 * Method:    setTitle
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_net_pascal_terminal_TerminalHandle_setTitle
(JNIEnv* e, jobject, jstring s) {
    std::string str = parseStr(e, s);
    SetConsoleTitleA(str.c_str());
}

/*
 * Class:     net_pascal_terminal_TerminalHandle
 * Method:    setCursorVisible
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_net_pascal_terminal_TerminalHandle_setCursorVisible
(JNIEnv*, jobject, jboolean b) {
    HANDLE consoleHandle = GetStdHandle(STD_OUTPUT_HANDLE);
    CONSOLE_CURSOR_INFO info;
    info.dwSize = 100;
    info.bVisible = b;
    SetConsoleCursorInfo(consoleHandle, &info);
}
