#include "TerminalHandle.h"
#include <termios.h>
#include <iostream>
#include <stdio.h>
#include <sys/ioctl.h>
#include <unistd.h>

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

static struct termios old, current;

void initTermios(int echo) 
{
  tcgetattr(0, &old);
  current = old; 
  current.c_lflag &= ~ICANON;
  if (echo) {
      current.c_lflag |= ECHO;
  } else {
      current.c_lflag &= ~ECHO;
  }
  tcsetattr(0, TCSANOW, &current);
}

void resetTermios(void) 
{
  tcsetattr(0, TCSANOW, &old);
}

char getch_(int echo) 
{
  char ch;
  initTermios(echo);
  ch = getchar();
  resetTermios();
  return ch;
}

char getch(void) 
{
  return getch_(0);
}

JNIEXPORT jint JNICALL Java_net_pascal_terminal_TerminalHandle_getch
  (JNIEnv *, jobject) {
	  return getch();
}

/*
 * Class:     net_pascal_terminal_TerminalHandle
 * Method:    getSize
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_net_pascal_terminal_TerminalHandle_getSize
  (JNIEnv* e, jobject) {
	  
	  struct winsize w;
	  ioctl(STDOUT_FILENO, TIOCGWINSZ, &w);
	  int i[] = { w.ws_col, w.ws_row };
	  return toIntArray(i, e);
  }

/*
 * Class:     net_pascal_terminal_TerminalHandle
 * Method:    resetInputBuffer
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_pascal_terminal_TerminalHandle_resetInputBuffer
  (JNIEnv *, jobject) {
	  ioctl(0, TCFLSH, 0);
  }

/*
 * Class:     net_pascal_terminal_TerminalHandle
 * Method:    setTitle
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_net_pascal_terminal_TerminalHandle_setTitle
  (JNIEnv* e, jobject, jstring s) {
	  std::string str = parseStr(e, s);
      std::cout << "\033]0;" << str << "\007";
  }

/*
 * Class:     net_pascal_terminal_TerminalHandle
 * Method:    setCursorVisible
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_net_pascal_terminal_TerminalHandle_setCursorVisible
  (JNIEnv *, jobject, jboolean b) {
	  if(b) {
			printf("\e[?25h");
	  } else {
			printf("\e[?25l");
	  }
  }