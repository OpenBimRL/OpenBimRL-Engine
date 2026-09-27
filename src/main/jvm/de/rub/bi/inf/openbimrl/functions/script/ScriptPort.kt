package de.rub.bi.inf.openbimrl.functions.script

/**
 * Mapper port definition for a Code / customScript node.
 */
data class ScriptPort(
    val name: String,
    val typeHint: String? = null,
    val collectionType: String? = null,
    val position: Int = 0,
) {
    val isCollection: Boolean
        get() = !collectionType.isNullOrBlank() ||
            typeHint.equals("Collection", ignoreCase = true) ||
            typeHint.equals("List", ignoreCase = true)
}
