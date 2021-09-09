/* Generated by AN DISI Unibo */ 
package it.unibo.fridge

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Fridge ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
			var FOOD_PRESENCE = false
				var STATUS = "Vuoto"
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("FRIDGE | STARTS and it's embedded with the proper set of food")
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("wait") { //this:State
					action { //it:State
						println("FRIDGE | is waiting for a command...")
					}
					 transition(edgeName="t14",targetState="answerFood",cond=whenDispatch("askFood"))
					transition(edgeName="t15",targetState="exposeState",cond=whenDispatch("consult"))
				}	 
				state("answerFood") { //this:State
					action { //it:State
						 var FOOD_CODE = -1  
						if( checkMsgContent( Term.createTerm("askFood(FOODE_CODE)"), Term.createTerm("askFood(ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 FOOD_CODE = payloadArg(0).toInt()  
						}
						println("FRIDGE | searching food_code $FOOD_CODE...")
						forward("answer", "answer($FOOD_PRESENCE)" ,"rbr" ) 
						println("FRIDGE | answered to RBR about food")
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("exposeState") { //this:State
					action { //it:State
						forward("expose", "expose($STATUS)" ,"maitre" ) 
						println("FRIDGE | exposed content to maitre")
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
			}
		}
}