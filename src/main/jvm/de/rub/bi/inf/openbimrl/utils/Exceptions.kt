package de.rub.bi.inf.openbimrl.utils

class InvalidFunctionInputException : RuntimeException {
    constructor(m: String) : super(m)
    constructor(m: String, e: Exception) : super(m, e)
}