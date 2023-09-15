package engine.openbimrl.inf.bi.rub.de.ifc

abstract class Adapter protected constructor(adapter: Companion) {
    companion object {
        private var instance: Companion? = null

        @JvmStatic
        fun getInstance(): Companion? {
            return instance
        }
    }

    init {
        if (instance != null) throw SingletonException()
        instance = adapter
    }

    abstract fun createIFCLabel(): IIFCLabel

    class SingletonException : RuntimeException("Tried to create multiple singletons of type Adapter")
}
