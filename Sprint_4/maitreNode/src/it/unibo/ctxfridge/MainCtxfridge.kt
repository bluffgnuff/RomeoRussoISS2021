/* Generated by AN DISI Unibo */ 
package it.unibo.ctxfridge
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	QakContext.createContexts(
	        "192.168.43.211", this, "maitre.pl", "sysRules.pl"
	)
}

