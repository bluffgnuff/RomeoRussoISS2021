/* Generated by AN DISI Unibo */ 
package it.unibo.ctxMaitre
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	QakContext.createContexts(
	        "192.168.1.211", this, "standingbuffetservice.pl", "sysRules.pl"
	)
}
