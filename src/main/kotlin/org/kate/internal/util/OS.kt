package org.kate.internal.util

import java.lang.management.ManagementFactory

class OS {
    companion object {
        private var pid: Int? = null
        fun getCurrrentProcessId(): Int {
            if (pid != null) return pid!!
            val runtime = ManagementFactory.getRuntimeMXBean()
            val jvm = runtime.javaClass.getDeclaredField("jvm")
            jvm.isAccessible = true
            val management = jvm.get(runtime)
            val method = management.javaClass.getDeclaredMethod("getProcessId")
            method.isAccessible = true
            pid = method.invoke(management) as Int
            return pid!!
        }

    }
}
