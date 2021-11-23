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
		
				val FridgeObserver = util.ActorCoapObserver("localhost",8040,"ctxsystem","fridge")
		//		val FridgeObserver = util.ActorCoapObserver("127.0.0.1",8060,"ctxfridge","fridge")
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("FRIDGE | STARTS and it's embedded with the proper set of food...")
						solve("consult('FridgeState.pl')","") //set resVar	
						println("FRIDGE | loaded initial state")
						 FridgeObserver.activate(myself, arrayListOf("Added", "Removed", "Fail"))  
						println("FRIDGE | activated FridgeObserver")
						delay(300) 
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("wait") { //this:State
					action { //it:State
						println("FRIDGE | is waiting for a command...")
					}
					 transition(edgeName="t150",targetState="answerFood",cond=whenDispatch("askFood"))
					transition(edgeName="t151",targetState="exposeState",cond=whenDispatch("consult"))
					transition(edgeName="t152",targetState="handleChangeState",cond=whenDispatch("changeState"))
				}	 
				state("answerFood") { //this:State
					action { //it:State
						 
									var FoodCode = ""
									var FoodPresence = false
						if( checkMsgContent( Term.createTerm("askFood(FOOD_CODE)"), Term.createTerm("askFood(FOOD_CODE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 FoodCode = payloadArg(0)  
						}
						println("FRIDGE | searching food with Food_Code = $FoodCode...")
						solve("checkFoodByCode($FoodCode,Food)","") //set resVar	
						if( currentSolution.isSuccess() ) { FoodPresence = true  
						println("FRIDGE |  found existing food with Food_Code = $FoodCode : ${getCurSol("Food")}")
						updateResourceRep( "$FoodPresence;${getCurSol("Food")}"  
						)
						}
						else
						{println("FRIDGE | Error searching food : not found existing or available food with Food_Code = $FoodCode...")
						updateResourceRep( "$FoodPresence"  
						)
						}
						println("FRIDGE | answered to RBR about food presence via CoAP")
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("exposeState") { //this:State
					action { //it:State
						solve("getAllEl(Foods)","") //set resVar	
						if( currentSolution.isSuccess() ) {println("FRIDGE | Foods = ${getCurSol("Foods")} ")
						updateResourceRep( "${getCurSol("Foods")}"  
						)
						}
						else
						{println("FRIDGE | Error consulting fridge...")
						updateResourceRep( "ERROR"  
						)
						}
						println("FRIDGE | sending state informations/exposed content to maitre...")
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("handleChangeState") { //this:State
					action { //it:State
						 var Food = ""  
						if( checkMsgContent( Term.createTerm("changeState(X,ARG)"), Term.createTerm("changeState(add,ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Food = payloadArg(1)  
								solve("add($Food)","") //set resVar	
								if( currentSolution.isSuccess() ) {println("FRIDGE | added Food : $Food...")
								updateResourceRep( "Added Food $Food with success!"  
								)
								}
								else
								{println("FRIDGE | Error adding Food : $Food...")
								updateResourceRep( "Fail adding Food $Food!"  
								)
								}
						}
						if( checkMsgContent( Term.createTerm("changeState(X,ARG)"), Term.createTerm("changeState(remove,ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Food = payloadArg(1)  
								solve("remove($Food)","") //set resVar	
								if( currentSolution.isSuccess() ) {println("FRIDGE | removed Food : $Food...")
								updateResourceRep( "Removed Food $Food with success!"  
								)
								}
								else
								{println("FRIDGE | Error removing Food : $Food...")
								updateResourceRep( "Fail removing Food $Food!"  
								)
								}
						}
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
			}
		}
}
