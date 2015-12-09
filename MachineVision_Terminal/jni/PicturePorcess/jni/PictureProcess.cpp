#include <jni.h>
#include "PictureProcess.h"

#define LEN 307200

JNIEXPORT jintArray JNICALL Java_com_machineversion_terminal_NetReceiveThread_pictureProcess(
		JNIEnv *env, jobject obj, jbyteArray data)
{
	int i;
	jintArray image = env->NewIntArray(LEN);
	jbyte *src = env->GetByteArrayElements(data, 0);
	jint *dst = env->GetIntArrayElements(image, 0);

	for (i = 0; i < LEN; i++)
	{
		jbyte temp = 0;
		temp = src[i] & 0xff;
		dst[i] = (0xFF000000 | temp << 16 | temp << 8 | temp);
	}

	env->ReleaseByteArrayElements(data, src, 0);
	env->ReleaseIntArrayElements(image, dst, 0);
	return image;
}
