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
				var Nexp = 0
				var AnsExpose1 = ""
				var AnsExpose2 = ""
				var ClearDish = ""
				var ClearFood = ""
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("MAITRE | STARTS...")
						solve("consult('Prepare.pl')","") //set resVar	
						delay(2000) 
					}
					 transition( edgeName="goto",targetState="sendPrepare", cond=doswitch() )
				}	 
				state("sendPrepare") { //this:State
					action { //it:State
						solve("getAllEl(Crockery,Foods)","") //set resVar	
						if( currentSolution.isSuccess() ) {forward("prepare", "prepare(${getCurSol("Crockery")},${getCurSol("Foods")})" ,"rbr" ) 
						println("MAITRE | send prepare command to RBR: ${getCurSol("Crockery")}, ${getCurSol("Foods")}")
						}
						else
						{println("MAITRE | Error getting 'Prepare the room' elements...")
						}
						delay(6000) 
					}
					 transition( edgeName="goto",targetState="sendAddFood", cond=doswitch() )
				}	 
				state("sendAddFood") { //this:State
					action { //it:State
						solve("getFoodCodeEl(FoodCode)","") //set resVar	
						if( currentSolution.isSuccess() ) {request("addFood", "addFood(${getCurSol("FoodCode")})" ,"rbr" )  
						println("MAITRE | send addFood(${getCurSol("FoodCode")}) command to RBR")
						}
						else
						{println("MAITRE | Error getting Food_Code for 'Add Food' task elements...")
						}
						delay(6000) 
						stateTimer = TimerActor("timer_sendAddFood", 
							scope, context!!, "local_tout_maitre_sendAddFood", AddFoodtime )
					}
					 transition(edgeName="t10",targetState="sendConsult",cond=whenTimeout("local_tout_maitre_sendAddFood"))   
					transition(edgeName="t11",targetState="handleWarning",cond=whenReply("warning"))
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
						forward("consult", "consult(0)" ,"dishwasher" ) 
						println("MAITRE | send consult command to Dishwasher")
						forward("consult", "consult(0)" ,"pantry" ) 
						println("MAITRE | send consult command to Pantry")
						forward("consult", "consult(0)" ,"table" ) 
						println("MAITRE | send consult command to Table")
					}
					 transition( edgeName="goto",targetState="waitExpose", cond=doswitch() )
				}	 
				state("waitExpose") { //this:State
					action { //it:State
						println("MAITRE | waiting answers from resources...")
					}
					 transition(edgeName="t22",targetState="handleExpose",cond=whenEvent("observerdishwasher"))
					transition(edgeName="t23",targetState="handleExpose",cond=whenEvent("observerfridge"))
					transition(edgeName="t24",targetState="handleExpose",cond=whenEvent("observerpantry"))
					transition(edgeName="t25",targetState="handleExpose",cond=whenEvent("observertable"))
				}	 
				state("handleExpose") { //this:State
					action { //it:State
						  
									var Sender = currentMsg.msgSender().removePrefix("observer")
									Nexp++ 
						if( checkMsgContent( Term.createTerm("observerdishwasher(X)"), Term.createTerm("observerdishwasher(X)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 AnsExpose1 = payloadArg(0)  
						}
						if( checkMsgContent( Term.createTerm("observerfridge(X)"), Term.createTerm("observerfridge(X)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 AnsExpose1 = payloadArg(0)  
						}
						if( checkMsgContent( Term.createTerm("observerpantry(X)"), Term.createTerm("observerpantry(X)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 AnsExpose1 = payloadArg(0)  
						}
						if( checkMsgContent( Term.createTerm("observertable(X)"), Term.createTerm("observertable(X)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 
									 			var Temp = payloadArg(0).split(";")
									 			AnsExpose1 = Temp.get(0)
									 			AnsExpose2 = Temp.get(1)
						}
						if(  Sender == "table"  
						 ){println("MAITRE | status of $Sender: $AnsExpose1 $AnsExpose2")
						}
						else
						 {println("MAITRE | status of $Sender: $AnsExpose1")
						 }
					}
					 transition( edgeName="goto",targetState="preSendClear", cond=doswitchGuarded({	Nexp == 4  
					}) )
					transition( edgeName="goto",targetState="waitExpose", cond=doswitchGuarded({! (	Nexp == 4  
					) }) )
				}	 
				state("preSendClear") { //this:State
					action { //it:State
						delay(6000) 
						forward("consult", "consult(0)" ,"table" ) 
						println("MAITRE | send consult command to Table for 'Clear the room' task")
						delay(10000) 
					}
					 transition(edgeName="t36",targetState="sendClear",cond=whenEvent("observertable"))
				}	 
				state("sendClear") { //this:State
					action { //it:State
						println("MAITRE | sono in sendClear")
						if( checkMsgContent( Term.createTerm("observertable(X)"), Term.createTerm("observertable(X)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("${payloadArg(0)}")
								 
									 			var Temp = payloadArg(0).split(";")
									 			ClearDish = Temp.get(0)
									 			ClearFood = Temp.get(1)
								println("MAITRE | $Temp")
						}
						println("MAITRE | status of Table: Crockery = $ClearDish and Food = $ClearFood")
						forward("clear", "clear($ClearDish,$ClearFood)" ,"rbr" ) 
					}
				}	 
			}
		}
}
