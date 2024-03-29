package it.unibo.test

import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.GlobalScope
import org.junit.BeforeClass
import org.junit.AfterClass
import org.junit.After
import org.junit.Assert.*
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.channels.Channel
import util.CoapObserverForTest

class TableTest {
		
	companion object {
		var tableActor : ActorBasic? = null
		var systemStarted = false
		var testingObserverTable : CoapObserverForTest ? = null
		val channelSyncStart = Channel<String>()
		var ip = "localhost"
//		var ip = "127.0.0.1"
		var ctx = "ctxsystem"
//		var ctx = "ctxtable"
		var actname = "table"
		var port = "8040"
//		var port = "8060"

		@JvmStatic
		@BeforeClass
		fun systemSetUp() {
			println ("TEST Init | Running context")

			GlobalScope.launch { 
				it.unibo.ctxsystem.main()
			}
			
			println ("TEST Init | Activating Observers")

			GlobalScope.launch {
				tableActor = QakContext.getActor("table")
				while(  tableActor == null ) {
					delay(500)
					tableActor = QakContext.getActor("table")
				}
				if( testingObserverTable == null) testingObserverTable = CoapObserverForTest("testingObserverTable","$ip", "$ctx", "$actname", "$port")
				println ("testingObserverTable=$testingObserverTable")
				delay(500)
				channelSyncStart.send("starttesting")
			}
		}
			
		@JvmStatic
	    @AfterClass
		fun terminate() {
			println ("TEST | terminate the testing")				
		}
	}
	
	@Before
	fun checkSystemStarted() {
		
		if( ! systemStarted ) {
			runBlocking {
				channelSyncStart.receive()
				systemStarted = true
				println ("TEST | checkSystemStarted resumed")
			}
		}
  	}	
	
	@After
	fun removeObs() {
		println ("TEST | AFTERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR  ${testingObserverTable!!.name}")
		testingObserverTable!!.terminate()
	}

	@Test
	fun AddFoodTableTest() {
		var	Food = arrayListOf(arrayListOf("s034", "cheddar", "10"))
		var TablePrevision = "Added [[s034,cheddar,10]] with success!"
		var msg = MsgUtil.buildDispatch("tester", "changeState", "changeState(add, $Food)", "table")
		var TableState = ""
		var expected = TablePrevision
		val channelForObserver = Channel<String>()		

		testingObserverTable!!.addObserver( channelForObserver,expected )
		runBlocking {
			println ("TEST | sending $msg")
			MsgUtil.sendMsg(msg, tableActor!!)
			TableState = channelForObserver.receive()			
			
			println ("TEST | RESULT=$TableState for $msg")
			assertEquals(TablePrevision,TableState)
		}
		channelForObserver.close()
	}

	@Test
	fun RemoveFoodTableTest() {
		var	FoodAdd = arrayListOf(arrayListOf("s035", "cocacola", "1"))
		var	FoodRemove = arrayListOf(arrayListOf("s035", "cocacola", "1"))
		var PrevisionRemove= "Removed [[s035,cocacola,1]] with success!"
		var PrevisionAdd= "Added [[s035,cocacola,1]] with success!"
		var msgAdd = MsgUtil.buildDispatch("tester", "changeState", "changeState(add, $FoodAdd)", "table")
		var msgRemove = MsgUtil.buildDispatch("tester", "changeState", "changeState(remove, $FoodRemove)", "table")
		var TableState = ""
		var expectedRemove= PrevisionRemove
		var expectedAdd= PrevisionAdd
		val channelForObserver = Channel<String>()		

		testingObserverTable!!.addObserver( channelForObserver,expectedRemove)		
		runBlocking {
			delay(200)
			println ("TEST | sending $msgAdd")
			MsgUtil.sendMsg(msgAdd, tableActor!!)
//			channelForObserver.receive()
//			testingObserverTable!!.terminate()
			println ("TEST | sending $msgRemove")
//			testingObserverTable!!.addObserver( channelForObserver,expectedRemove )
			MsgUtil.sendMsg(msgRemove, tableActor!!)
			TableState = channelForObserver.receive()			
			println ("TEST | RESULT=$TableState for $msgRemove")
			assertEquals(PrevisionRemove,TableState)
		}
		channelForObserver.close()
	}

	@Test
	fun RemoveFoodTableFailTest() {
		var	Food = arrayListOf(arrayListOf("x999", "mysteryfood", "2"))
		var Prevision = "Fail removing [[x999,mysteryfood,2]]!"
		var msg = MsgUtil.buildDispatch("tester", "changeState", "changeState(remove, $Food)", "table")
		var State = ""
		var expected = Prevision		
		val channelForObserver = Channel<String>()		

		testingObserverTable!!.addObserver( channelForObserver,expected )
		
		runBlocking {
			delay(200)
			println ("TEST | sending $msg")
			MsgUtil.sendMsg(msg, tableActor!!)
			State = channelForObserver.receive()			
			
			println ("TEST | RESULT=$State for $msg")
			assertEquals(Prevision,State)
		}
		channelForObserver.close()		
	}
	
	@Test
	fun RemoveFoodTableFailQuantityTest() {
		var	FoodAdd = arrayListOf(arrayListOf("s099", "sprite", "1"))
		var	FoodRemove = arrayListOf(arrayListOf("s099", "sprite", "2"))
		var PrevisionRemove = "Fail removing [[s099,sprite,2]]!"
		var PrevisionAdd= "Added [[s099,sprite,1]] with success!"
		var msgAdd = MsgUtil.buildDispatch("tester", "changeState", "changeState(add, $FoodAdd)", "table")
		var msgRemove = MsgUtil.buildDispatch("tester", "changeState", "changeState(remove, $FoodRemove)", "table")
		var State = ""
		var expectedAdd= PrevisionAdd
		var expectedRemove = PrevisionRemove
		val channelForObserver = Channel<String>()			

		testingObserverTable!!.addObserver( channelForObserver,expectedRemove)
		runBlocking {
			delay(200)
			println ("TEST | sending $msgAdd")
			MsgUtil.sendMsg(msgAdd, tableActor!!)			
//			channelForObserver.receive()
//			testingObserverTable!!.terminate()
//			testingObserverTable!!.addObserver( channelForObserver,expectedRemove )			
			println ("TEST | sending $msgRemove")
			MsgUtil.sendMsg(msgRemove, tableActor!!)
	
			State = channelForObserver.receive()			
			println ("TEST | RESULT=$State for $msgRemove")

			assertEquals(PrevisionRemove,State)
		}
		channelForObserver.close()		
	}
	
	@Test
	fun AddRemoveDishTableTest() {
		var	DishesAdd = arrayListOf(arrayListOf("dishes", "10"))
		var	DishesRemove = arrayListOf(arrayListOf("dishes", "10"))
		var PrevisionRemove = "Removed [[dishes,10]] with success!"
		var expectedAdd = "Added [[dishes,10]] with success!"
		var msgAdd = MsgUtil.buildDispatch("tester", "changeState", "changeState(add, $DishesAdd)", "table")
		var msgRemove = MsgUtil.buildDispatch("tester", "changeState", "changeState(remove, $DishesRemove)", "table")
		var State = ""
		var expectedRemove = PrevisionRemove
		val channelForObserver = Channel<String>()		
		

		testingObserverTable!!.addObserver( channelForObserver,expectedRemove)
		runBlocking {
			delay(200)
			println ("TEST | sending $msgAdd")
			MsgUtil.sendMsg(msgAdd, tableActor!!)
//			channelForObserver.receive()
			
			println ("TEST | sending $msgRemove")

//			testingObserverTable!!.terminate()
//			testingObserverTable!!.addObserver( channelForObserver,expectedRemove)
			MsgUtil.sendMsg(msgRemove, tableActor!!)
			State = channelForObserver.receive()		
		
			println ("TEST | RESULT=$State for $msgRemove")
			assertEquals(PrevisionRemove,State)
		}
		channelForObserver.close()		
	}
 

	@Test
	fun RemoveDishTableFailTest() {
		var	Dishes = arrayListOf(arrayListOf("cristal_glasses", "10"))
		var Prevision = "Fail removing [[cristal_glasses,10]]!"
		var msg = MsgUtil.buildDispatch("tester", "changeState", "changeState(remove, $Dishes)", "table")
		var State = ""
		var expected = Prevision
	
		val channelForObserver = Channel<String>()				
		testingObserverTable!!.addObserver( channelForObserver,expected )		
		runBlocking {
			delay(200)
			
			println ("TEST | sending $msg")
			MsgUtil.sendMsg(msg, tableActor!!)
			State = channelForObserver.receive()			
			
			println ("TEST | RESULT=$State for $msg")
			assertEquals(Prevision,State)
		}
		channelForObserver.close()		
	}

	@Test
	fun RemoveDishTableFailQuantityTest() {
		var	DishesAdd = arrayListOf(arrayListOf("glass", "10"))
		var	DishesRemove = arrayListOf(arrayListOf("glass", "11"))
		var PrevisionRemove = "Fail removing [[glass,11]]!"
		var PrevisionAdd = "Added [[glass,10]] with success!"
		var msgAdd = MsgUtil.buildDispatch("tester", "changeState", "changeState(add, $DishesAdd)", "table")
		var msgRemove = MsgUtil.buildDispatch("tester", "changeState", "changeState(remove, $DishesRemove)", "table")
		var State = ""
		var expectedRemove = PrevisionRemove
		var expectedAdd= PrevisionAdd

		val channelForObserver = Channel<String>()		
		testingObserverTable!!.addObserver( channelForObserver,expectedRemove)
		runBlocking {
			delay(200)
			println ("TEST | sending $msgAdd")
			MsgUtil.sendMsg(msgAdd, tableActor!!)
//			channelForObserver.receive()
			println ("TEST | sending $msgAdd")
//			testingObserverTable!!.terminate()
//			testingObserverTable!!.addObserver( channelForObserver,expectedRemove )		
			MsgUtil.sendMsg(msgRemove, tableActor!!)
			State = channelForObserver.receive()			
			
			println ("TEST | RESULT=$State for $msgRemove")
			assertEquals(PrevisionRemove,State)
		}
		channelForObserver.close()
	}
 }