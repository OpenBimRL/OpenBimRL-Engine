package de.rub.bi.inf.openbimrl.functions.script

/**
 * Collects script [println]/System.out lines during a check or compile session
 * so the REST API / CreatorTool console can display them.
 */
object ScriptConsole {
    private val lines = ThreadLocal.withInitial { mutableListOf<String>() }

    fun clear() {
        lines.get().clear()
    }

    fun append(line: String) {
        if (line.isEmpty()) return
        lines.get().add(line)
    }

    fun appendPrefixed(prefix: String, stdout: String) {
        if (stdout.isBlank()) return
        stdout.lineSequence().forEach { line ->
            // Keep empty lines from println() as prefixed blank output markers
            append("$prefix: $line")
        }
    }

    fun drain(): List<String> {
        val out = lines.get().toList()
        lines.get().clear()
        return out
    }

    fun snapshot(): List<String> = lines.get().toList()
}
