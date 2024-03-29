/* Generated by AN DISI Unibo */ 
package it.unibo.foodconsumer

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Foodconsumer ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("FOODCONSUMER | STARTS and it's very hungry...")
						solve("consult('Consumer.pl')","") //set resVar	
						delay(70000) 
					}
					 transition( edgeName="goto",targetState="eat", cond=doswitch() )
				}	 
				state("eat") { //this:State
					action { //it:State
						println("FOODCONSUMER | chooses the food he wants to eat")
						solve("getFoodEl(Foods)","") //set resVar	
						if( currentSolution.isSuccess() ) {println("FOODCONSUMER | eating from table ${getCurSol("Foods")}...")
						forward("changeState", "changeState(remove,${getCurSol("Foods")})" ,"table" ) 
						println("FOODCONSUMER | is now happy")
						}
						else
						{println("FOODCONSUMER | Error getting food to consume...")
						}
						terminate(1)
					}
				}	 
			}
		}
}
