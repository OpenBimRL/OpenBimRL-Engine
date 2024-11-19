package de.rub.bi.inf.openbimrl.functions.annotations


@Target(AnnotationTarget.CLASS)
annotation class OpenBIMRLFunction(val name: String = "", val packageName: String = "")