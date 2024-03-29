/* Generated by AN DISI Unibo */ 
package it.unibo.rbrmapper

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Rbrmapper ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
				var CurrMov = "empty"
				var CurrEdge = 0
				var NameFile = "roomMap"
				var Table = false
				var Step = 650 //290 //647
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("MAPPER | STARTS...")
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("wait") { //this:State
					action { //it:State
						println("MAPPER | waits...")
					}
					 transition(edgeName="t135",targetState="doStep",cond=whenRequest("map"))
					transition(edgeName="t136",targetState="endState",cond=whenDispatch("end"))
				}	 
				state("doStep") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("map(ARG)"), Term.createTerm("map(ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								itunibo.planner.plannerUtil.initAI(  )
								println("MAPPER | starts mapping room...")
								itunibo.planner.plannerUtil.showMap(  )
						}
						request("step", "step($Step)" ,"basicrobot" )  
						delay(1000) 
					}
					 transition(edgeName="t237",targetState="succesStep",cond=whenReply("stepdone"))
					transition(edgeName="t238",targetState="obstacleFound",cond=whenReply("stepfail"))
				}	 
				state("succesStep") { //this:State
					action { //it:State
						itunibo.planner.plannerUtil.updateMap( "w"  )
						itunibo.planner.plannerUtil.showMap(  )
					}
					 transition( edgeName="goto",targetState="doStep", cond=doswitchGuarded({ !Table  
					}) )
					transition( edgeName="goto",targetState="visitInternalCell", cond=doswitchGuarded({! ( !Table  
					) }) )
				}	 
				state("obstacleFound") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("stepfail(DURATION,CAUSE)"), Term.createTerm("stepfail(D,C)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 	
												var D = payloadArg(0)
											 	var C = payloadArg(1)
								request("step", "step($D)" ,"basicrobot" )  
						}
						delay(1000) 
					}
					 transition(edgeName="t339",targetState="turnLeft",cond=whenReplyGuarded("stepdone",{ !Table  
					}))
					transition(edgeName="t340",targetState="tableFound",cond=whenReplyGuarded("stepdone",{ Table  
					}))
					transition(edgeName="t341",targetState="obstacleFound",cond=whenReply("stepfail"))
				}	 
				state("turnLeft") { //this:State
					action { //it:State
						println("MAPPER | Found a wall")
						forward("cmd", "cmd(l)" ,"basicrobot" ) 
						itunibo.planner.plannerUtil.updateMap( "l"  )
						delay(1000) 
						 CurrEdge++  
					}
					 transition( edgeName="goto",targetState="doStep", cond=doswitchGuarded({ CurrEdge < 4  
					}) )
					transition( edgeName="goto",targetState="saveMap", cond=doswitchGuarded({! ( CurrEdge < 4  
					) }) )
				}	 
				state("saveMap") { //this:State
					action { //it:State
						itunibo.planner.plannerUtil.showMap(  )
						itunibo.planner.plannerUtil.saveRoomMap( NameFile  )
						itunibo.planner.plannerUtil.loadRoomMap( NameFile  )
					}
					 transition( edgeName="goto",targetState="planNextDirty", cond=doswitch() )
				}	 
				state("planNextDirty") { //this:State
					action { //it:State
						 Table = true  
						itunibo.planner.plannerUtil.planForNextDirty(  )
						 CurrMov = itunibo.planner.plannerUtil.getNextPlannedMove()  
						itunibo.planner.plannerUtil.showMap(  )
					}
					 transition( edgeName="goto",targetState="turnHome", cond=doswitchGuarded({ CurrMov == ""  
					}) )
					transition( edgeName="goto",targetState="doMove", cond=doswitchGuarded({! ( CurrMov == ""  
					) }) )
				}	 
				state("visitInternalCell") { //this:State
					action { //it:State
						 CurrMov = itunibo.planner.plannerUtil.getNextPlannedMove()  
					}
					 transition( edgeName="goto",targetState="planNextDirty", cond=doswitchGuarded({ CurrMov == ""  
					}) )
					transition( edgeName="goto",targetState="doMove", cond=doswitchGuarded({! ( CurrMov == ""  
					) }) )
				}	 
				state("doMove") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="doStep", cond=doswitchGuarded({ CurrMov == "w"  
					}) )
					transition( edgeName="goto",targetState="doTurn", cond=doswitchGuarded({! ( CurrMov == "w"  
					) }) )
				}	 
				state("doTurn") { //this:State
					action { //it:State
						forward("cmd", "cmd($CurrMov)" ,"basicrobot" ) 
						itunibo.planner.plannerUtil.updateMap( CurrMov  )
						delay(1000) 
					}
					 transition( edgeName="goto",targetState="visitInternalCell", cond=doswitch() )
				}	 
				state("tableFound") { //this:State
					action { //it:State
						itunibo.planner.plannerUtil.updateMapObstacleOnCurrentDirection(  )
					}
					 transition( edgeName="goto",targetState="visitInternalCell", cond=doswitch() )
				}	 
				state("turnHome") { //this:State
					action { //it:State
						println("MAPPER | coming back to RH...")
						request("setGoal", "setGoal(0,0,downDir)" ,"rbrwalker" )  
					}
					 transition(edgeName="t442",targetState="endState",cond=whenReply("goalState"))
				}	 
				state("endState") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("goalState(X)"), Term.createTerm("goalState(X)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("MAPPER | ...reached RH")
								itunibo.planner.plannerUtil.saveRoomMap( NameFile  )
								answer("map", "mapdone", "mapdone(0)"   )  
						}
						println("MAPPER | terminate...")
						terminate(1)
					}
				}	 
			}
		}
}
