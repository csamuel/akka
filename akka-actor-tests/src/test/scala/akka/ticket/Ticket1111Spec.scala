package akka.ticket

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import akka.routing._
import akka.actor.Actor._
import akka.actor.{ ActorRef, Actor }
import collection.mutable.LinkedList
import akka.routing.Routing.Broadcast
import java.util.concurrent.{ CountDownLatch, TimeUnit }

class Ticket1111Spec extends WordSpec with MustMatchers {

  "Scatter-gather router" must {

    def newActor(id: Int) = actorOf(new Actor {
      def receive = {
        case _id: Int if (_id == id) ⇒
        case _                       ⇒ Thread sleep 100 * id; self reply id
      }
    }).start()

    "return the first response from connections, when all of them replied" in {

      val actor = Routing.actorOf("foo", List(newActor(0), newActor(1)), new ScatterGatherFirstResponseRouter()).start()

      (actor ? Broadcast("Hi!")).get.asInstanceOf[Int] must be(0)

    }

    "return the first response from connections, when some of them failed to reply" in {

      val actor = Routing.actorOf("foo", List(newActor(0), newActor(1)), new ScatterGatherFirstResponseRouter()).start()

      (actor ? Broadcast(0)).get.asInstanceOf[Int] must be(1)

    }

  }

}
