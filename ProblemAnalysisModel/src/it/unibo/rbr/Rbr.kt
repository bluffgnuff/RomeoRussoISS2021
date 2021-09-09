/* Generated by AN DISI Unibo */ 
package it.unibo.rbr

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Rbr ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 	var FOOD_PRESENCE = false  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("RBR | STARTS and it's placed in RH position")
					}
					 transition(edgeName="t00",targetState="exPrepare",cond=whenDispatch("prepare"))
				}	 
				state("exPrepare") { //this:State
					action { //it:State
						println("RBR | executing task 'Prepare the room':")
						println("RBR | going to pantry...")
						println("RBR | ...reached pantry. Going to table...")
						println("RBR | ...reached table. Going to fridge...")
						println("RBR | ...reached fridge. Going to table...")
						println("RBR | ...reached table. Coming back to RH...")
						println("RBR | ...reached RH. Finished executing task")
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("wait") { //this:State
					action { //it:State
						println("RBR | is placed in RH position and it's waiting for a command...")
					}
					 transition(edgeName="t11",targetState="checkFood",cond=whenDispatch("addFood"))
					transition(edgeName="t12",targetState="exClear",cond=whenDispatch("clear"))
				}	 
				state("checkFood") { //this:State
					action { //it:State
						 var FOOD_CODE = -1  
						if( checkMsgContent( Term.createTerm("addFood(FOODE_CODE)"), Term.createTerm("addFood(ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 FOOD_CODE = payloadArg(0).toInt()  
						}
						forward("askFood", "askFood($FOOD_CODE)" ,"fridge" ) 
						println("RBR | asked fridge if it contains the food with food-code = $FOOD_CODE")
					}
					 transition(edgeName="t23",targetState="handleReply",cond=whenDispatch("answer"))
				}	 
				state("handleReply") { //this:State
					action { //it:State
						 var FOOD_PRESENCE = false  
						if( checkMsgContent( Term.createTerm("answer(ARG)"), Term.createTerm("answer(ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 FOOD_PRESENCE = payloadArg(0).toBoolean()  
						}
						println("RBR | received answer from fridge: $FOOD_PRESENCE")
					}
					 transition( edgeName="goto",targetState="fail", cond=doswitchGuarded({ FOOD_PRESENCE == false  
					}) )
					transition( edgeName="goto",targetState="exAddFood", cond=doswitchGuarded({! ( FOOD_PRESENCE == false  
					) }) )
				}	 
				state("fail") { //this:State
					action { //it:State
						forward("warning", "warning(w)" ,"maitre" ) 
						println("RBR | send warning to maitre")
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("exAddFood") { //this:State
					action { //it:State
						println("RBR | executing task 'Add food':")
						println("RBR | going to fridge...")
						println("RBR | ...reached fridge. Going to table...")
						println("RR | ...reached table. Coming back to RH...")
						println("RBR | ...reached RH. Finished executing task")
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("exClear") { //this:State
					action { //it:State
						println("RBR | executing task 'Clear the room':")
						println("RBR | going to table...")
						println("RBR | ...reached table. Going to fridge...")
						println("RBR | ...reached fridge. Going to table...")
						println("RBR | ...reached table. Going to dishwasher...")
						println("RBR | ...reached dishwasher. Coming back to RH...")
						println("RBR | ...reached RH. Finished executing task")
						terminate(0)
					}
				}	 
			}
		}
}
