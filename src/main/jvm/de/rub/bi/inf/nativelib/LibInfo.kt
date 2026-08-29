package de.rub.bi.inf.nativelib

/** Metadata compiled into libopenbimrl_jni.so (version, build toolchain, offload flags). */
data class LibInfo(
    val version: String,
    val buildDate: String,
    val buildCompiler: String,
    val gpuOffloadEnabled: Boolean,
    val gpuOffloadArch: String?,
)
