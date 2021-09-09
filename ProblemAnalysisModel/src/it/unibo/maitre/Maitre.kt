/* Generated by AN DISI Unibo */ 
package it.unibo.maitre

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Maitre ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 var AddFoodtime = 3000L  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						delay(2000) 
						println("MAITRE | STARTS")
					}
					 transition( edgeName="goto",targetState="sendPrepare", cond=doswitch() )
				}	 
				state("sendPrepare") { //this:State
					action { //it:State
						forward("prepare", "prepare(0)" ,"rbr" ) 
						println("MAITRE | send prepare command to RBR")
					}
					 transition( edgeName="goto",targetState="sendAddFood", cond=doswitch() )
				}	 
				state("sendAddFood") { //this:State
					action { //it:State
						forward("addFood", "addFood(1500)" ,"rbr" ) 
						println("MAITRE | send addFood(food_code) command to RBR")
						stateTimer = TimerActor("timer_sendAddFood", 
							scope, context!!, "local_tout_maitre_sendAddFood", AddFoodtime )
					}
					 transition(edgeName="t16",targetState="sendConsult",cond=whenTimeout("local_tout_maitre_sendAddFood"))   
					transition(edgeName="t17",targetState="handleWarning",cond=whenDispatch("warning"))
				}	 
				state("handleWarning") { //this:State
					action { //it:State
						println("MAITRE | received warning from RBR")
					}
					 transition( edgeName="goto",targetState="sendConsult", cond=doswitch() )
				}	 
				state("sendConsult") { //this:State
					action { //it:State
						forward("consult", "consult(0)" ,"fridge" ) 
						println("MAITRE | send consult command to Fridge")
					}
					 transition(edgeName="t28",targetState="handleExpose",cond=whenDispatch("expose"))
				}	 
				state("handleExpose") { //this:State
					action { //it:State
						 var ansExpose = " "  
						if( checkMsgContent( Term.createTerm("expose(ARG)"), Term.createTerm("expose(ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 ansExpose= payloadArg(0)  
						}
						println("MAITRE | received expose from fridge: $ansExpose")
					}
					 transition( edgeName="goto",targetState="sendClear", cond=doswitch() )
				}	 
				state("sendClear") { //this:State
					action { //it:State
						forward("clear", "clear(0)" ,"rbr" ) 
						terminate(0)
					}
				}	 
			}
		}
}