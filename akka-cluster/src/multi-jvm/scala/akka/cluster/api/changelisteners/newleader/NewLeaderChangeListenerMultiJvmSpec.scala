/**
 *  Copyright (C) 2009-2011 Typesafe Inc. <http://www.typesafe.com>
 */

package akka.cluster.api.changelisteners.newleader

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll

import akka.cluster._
import ChangeListener._
import Cluster._

import java.util.concurrent._

object NewLeaderChangeListenerMultiJvmSpec {
  var NrOfNodes = 2
}

class NewLeaderChangeListenerMultiJvmNode1 extends MasterClusterTestNode {
  import NewLeaderChangeListenerMultiJvmSpec._

  val testNodes = NrOfNodes

  "A NewLeader change listener" must {

    "be invoked after leader election is completed" in {
      barrier("start-node1", NrOfNodes) {
        Cluster.node
      }

      barrier("start-node2", NrOfNodes) {
      }

      node.shutdown()
    }
  }
}

class NewLeaderChangeListenerMultiJvmNode2 extends ClusterTestNode {
  import NewLeaderChangeListenerMultiJvmSpec._

  "A NewLeader change listener" must {

    "be invoked after leader election is completed" in {
      val latch = new CountDownLatch(1)

      barrier("start-node1", NrOfNodes) {
      }

      barrier("start-node2", NrOfNodes) {
        node.register(new ChangeListener {
          override def newLeader(node: String, client: ClusterNode) {
            latch.countDown
          }
        })
      }

      latch.await(10, TimeUnit.SECONDS) must be === true

      node.shutdown()
    }
  }
}
