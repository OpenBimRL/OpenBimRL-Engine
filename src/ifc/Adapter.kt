package engine.openbimrl.inf.bi.rub.de.ifc

abstract class Adapter protected constructor() {
    companion object {
        private var adapterInstance: Adapter? = null

        @get:JvmStatic
        @get:Synchronized
        val instance: Adapter
            get() = adapterInstance ?: throw SingletonException("Singleton not initialized... Aborting!")
    }


    /**
     * caution: Singleton has to be initialized BEFORE any form of multithreading can be initiated
     */
    init {
        if (adapterInstance != null) throw SingletonException("tried to create multiple instances of singleton... Aborting!")
        setInstance()
    }

    private fun setInstance() {
        adapterInstance = this
    }

    abstract val IFC4: IIFC4

    interface IIFC4 {
        fun <T: IIFCClass> create(clazz: Class<T>, vararg params: Any) :  T
    }

    class SingletonException(message: String) : RuntimeException(message)
}
