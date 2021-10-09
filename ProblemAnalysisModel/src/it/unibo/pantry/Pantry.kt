/* Generated by AN DISI Unibo */ 
package it.unibo.pantry

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Pantry ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("PANTRY| loading initial state ")
						solve("consult('PantryInit.pl')","") //set resVar	
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("PANTRY| working")
					}
					 transition(edgeName="t04",targetState="exposeState",cond=whenDispatch("consult"))
					transition(edgeName="t05",targetState="handleChangeState",cond=whenDispatch("add"))
					transition(edgeName="t06",targetState="handleChangeState",cond=whenDispatch("remove"))
				}	 
				state("exposeState") { //this:State
					action { //it:State
						var State = "c" 
						println("PANTRY | exposed content to maitre")
						updateResourceRep("State:$State" 
						)
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("handleChangeState") { //this:State
					action { //it:State
						 var Nd = ""  
						if( checkMsgContent( Term.createTerm("add(X)"), Term.createTerm("add(X)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 	Nd = payloadArg(0)  
								println("PANTRY | add $Nd...")
								 //Dishes = Dishes + Nd   
						}
						if( checkMsgContent( Term.createTerm("remove(X)"), Term.createTerm("remove(X)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 	Nd = payloadArg(0)  
								println("PANTRY| remove $Nd...")
								 //Dishes = Dishes - Nd   
						}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
			}
		}
}
