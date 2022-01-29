/* Generated by AN DISI Unibo */ 
package it.unibo.rbrwalker

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Rbrwalker ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
				var Dir = ""
				var CurrMov = "empty"
				var X = ""
				var Y = ""
				var StopTimer = 1000L
				var FirstStart = true
				var Step = 650 //647 //290
				var ObstGoal = false	
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("WALKER | STARTS...")
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("wait") { //this:State
					action { //it:State
						println("WALKER | waits a goal...")
						if( checkMsgContent( Term.createTerm("stop(ARG)"), Term.createTerm("stop(ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								answer("stop", "stopped", "stopped(false)"   )  
								updateResourceRep( "Stop Fail"  
								)
						}
					}
					 transition(edgeName="t043",targetState="wait",cond=whenRequestGuarded("stop",{ itunibo.planner.plannerUtil.atHome()  
					}))
					transition(edgeName="t044",targetState="goToGoal",cond=whenRequest("setGoal"))
					transition(edgeName="t045",targetState="terminateWalker",cond=whenDispatch("end"))
				}	 
				state("goToGoal") { //this:State
					action { //it:State
						if(  FirstStart  
						 ){itunibo.planner.plannerUtil.initAI(  )
						itunibo.planner.plannerUtil.loadRoomMap( "roomMap"  )
						itunibo.planner.plannerUtil.showMap(  )
						 FirstStart = false  
						}
						if( checkMsgContent( Term.createTerm("setGoal(X,Y,DIR)"), Term.createTerm("setGoal(X,Y,DIR)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												X = payloadArg(0)
												Y = payloadArg(1)
												Dir = payloadArg(2)
								println("WALKER | received the goal ($X, $Y)...")
								itunibo.planner.plannerUtil.planForGoal( X, Y  )
						}
						if( checkMsgContent( Term.createTerm("reactivate(ARG)"), Term.createTerm("reactivate(ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("WALKER | reactivated...")
								updateResourceRep( "Reactivated"  
								)
						}
						 CurrMov = itunibo.planner.plannerUtil.getNextPlannedMove()  
					}
					 transition( edgeName="goto",targetState="correctDirection", cond=doswitchGuarded({ CurrMov == ""  
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
				state("doStep") { //this:State
					action { //it:State
						request("step", "step($Step)" ,"basicrobot" )  
					}
					 transition(edgeName="t146",targetState="handleAnswer",cond=whenReply("stepdone"))
					transition(edgeName="t147",targetState="handleAnswer",cond=whenReply("stepfail"))
				}	 
				state("doTurn") { //this:State
					action { //it:State
						forward("cmd", "cmd($CurrMov)" ,"basicrobot" ) 
						itunibo.planner.plannerUtil.updateMap( CurrMov  )
						stateTimer = TimerActor("timer_doTurn", 
							scope, context!!, "local_tout_rbrwalker_doTurn", StopTimer )
					}
					 transition(edgeName="t248",targetState="goToGoal",cond=whenTimeout("local_tout_rbrwalker_doTurn"))   
					transition(edgeName="t249",targetState="handleStop",cond=whenRequest("stop"))
				}	 
				state("handleAnswer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("stepdone(V)"), Term.createTerm("stepdone(V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								itunibo.planner.plannerUtil.updateMap( CurrMov  )
								 ObstGoal = false  
						}
						if( checkMsgContent( Term.createTerm("stepfail(DURATION,CAUSE)"), Term.createTerm("stepfail(D,C)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 
												var Duration = payloadArg(0)
												var Cause = payloadArg(1)
								println("WALKER | failed execution move: found $Cause")
								
												var CurDir = itunibo.planner.plannerUtil.getDirection()
												var CurPosX = itunibo.planner.plannerUtil.getPosX()
												var CurPosY = itunibo.planner.plannerUtil.getPosY()
								if(  (CurDir == "upDir" && CurPosX == X.toInt() && (CurPosY - 1) == Y.toInt())
											   || (CurDir == "downDir" && CurPosX == X.toInt() && (CurPosY + 1) == Y.toInt())
											   || (CurDir == "leftDir" && (CurPosX - 1) == X.toInt() && CurPosY == Y.toInt())
											   || (CurDir == "rightDir" && (CurPosX + 1) == X.toInt() && CurPosY == Y.toInt())  
								 ){println("WALKER | found obstacle in goal cell. Waiting and try again to do the step...")
								delay(1000) 
								 ObstGoal = true  
								}
								else
								 {itunibo.planner.plannerUtil.updateMapObstacleOnCurrentDirection(  )
								 itunibo.planner.plannerUtil.showMap(  )
								 println("WALKER | finding a new path for the goal ($X, $Y)...")
								 itunibo.planner.plannerUtil.planForGoal( X, Y  )
								 itunibo.planner.plannerUtil.updateMap( "w"  )
								 itunibo.planner.plannerUtil.updateMap( "l"  )
								 itunibo.planner.plannerUtil.updateMap( "l"  )
								 itunibo.planner.plannerUtil.updateMap( "w"  )
								 itunibo.planner.plannerUtil.updateMap( "r"  )
								 itunibo.planner.plannerUtil.updateMap( "r"  )
								 itunibo.planner.plannerUtil.showMap(  )
								 }
								updateResourceRep( "Obstacle Detected"  
								)
						}
						stateTimer = TimerActor("timer_handleAnswer", 
							scope, context!!, "local_tout_rbrwalker_handleAnswer", StopTimer )
					}
					 transition(edgeName="t350",targetState="handleFail",cond=whenTimeout("local_tout_rbrwalker_handleAnswer"))   
					transition(edgeName="t351",targetState="handleStop",cond=whenRequest("stop"))
				}	 
				state("handleFail") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="doStep", cond=doswitchGuarded({ ObstGoal  
					}) )
					transition( edgeName="goto",targetState="goToGoal", cond=doswitchGuarded({! ( ObstGoal  
					) }) )
				}	 
				state("handleStop") { //this:State
					action { //it:State
						println("WALKER | stopped. Waiting for a reactivate command...")
						answer("stop", "stopped", "stopped(true)"   )  
						updateResourceRep( "Stopped"  
						)
					}
					 transition(edgeName="t452",targetState="doStep",cond=whenDispatchGuarded("reactivate",{ ObstGoal  
					}))
					transition(edgeName="t453",targetState="goToGoal",cond=whenDispatchGuarded("reactivate",{ !ObstGoal  
					}))
				}	 
				state("correctDirection") { //this:State
					action { //it:State
						
									var CurPos : Pair<Int, Int> ?= null
									var CurDir = itunibo.planner.plannerUtil.getDirection()
									
									while (CurDir != Dir) {
						if(  (Dir == "leftDir" && CurDir == "upDir") || (Dir == "rightDir" && CurDir == "downDir") 
										   || (Dir == "upDir" && CurDir == "rightDir") || (Dir == "downDir" && CurDir == "leftDir")  
						 ){forward("cmd", "cmd(l)" ,"basicrobot" ) 
						itunibo.planner.plannerUtil.updateMap( "l"  )
						}
						else
						 {forward("cmd", "cmd(r)" ,"basicrobot" ) 
						 itunibo.planner.plannerUtil.updateMap( "r"  )
						 }
						delay(1000) 
						
										CurDir = itunibo.planner.plannerUtil.getDirection()
									}
										CurPos = itunibo.planner.plannerUtil.get_curPos()		
						itunibo.planner.plannerUtil.showMap(  )
						println("WALKER | arrived to the goal $CurPos")
						answer("setGoal", "goalState", "goalState($CurPos)"   )  
						updateResourceRep( "$CurPos"  
						)
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("terminateWalker") { //this:State
					action { //it:State
						println("RBR | terminating Basic Robot...")
						forward("end", "end(0)" ,"basicrobot" ) 
						println("WALKER | terminating...")
						terminate(1)
					}
				}	 
			}
		}
}
