package scalax.collection
package generator

import scala.language.higherKinds
import scala.util.Random

import org.scalacheck.{Arbitrary, Gen}
import Arbitrary.arbitrary
import org.scalacheck.Prop.forAll
import org.scalatest.Spec
import org.scalatest.Matchers
import org.scalatest.prop.PropertyChecks
import org.scalatest.prop.Checkers._

import GraphPredef._, GraphEdge._
import mutable.{Graph => MGraph}
import generic.GraphCompanion

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class TGraphGenTest
    extends Spec
       with Matchers
       with PropertyChecks {

  val minSuccessful = 5
  implicit val config = PropertyCheckConfig(minSuccessful = minSuccessful, maxDiscarded = 5)
  
  object `nr of minimum successful tests` {
    def `should be met` {
      var count = 0
      forAll { (i: Int) => count += 1 
      }
      count should be (minSuccessful)
    }
  }
  
  object `outer node set` {
    val order = 5 
    implicit val arbitraryOuterNodes: Arbitrary[Set[Int]] =
      new GraphGen[Int,DiEdge,Graph](
        Graph, order, Gen.choose(0, 10 * order), NodeDegreeRange(1,4), Set(DiEdge)
      ).outerNodeSet
          
    def `should conform to the passed size` {
      forAll(arbitrary[Set[Int]]) { (outerNodes: Set[Int]) =>
        outerNodes should have size (order)
      }
    }
  }
  
  type IntDiGraph = Graph[Int,DiEdge]
  
  def checkMetrics(g: IntDiGraph, metrics: GraphGen.Metrics[Int]) {
    import metrics._

    val degrees = g.degreeSeq
    val tolerableMaxExceed: Int = if (g.isHyper) 8 else 1

    g.order       should be (order)
    g.isConnected should be (connected)
    
    degrees.min should be >= (nodeDegrees.min)
    degrees.max should be <= (nodeDegrees.max + tolerableMaxExceed)

    val totalDegree = g.totalDegree
    val deviation = totalDegree - expectedTotalDegree
    totalDegree should (be >= (expectedTotalDegree - maxDegreeDeviation) and
                        be <= (expectedTotalDegree + maxDegreeDeviation))
  }
  
  object `tiny connected graph of [Int,DiEdge]` {
    implicit val arbitraryGraph = GraphGen.tinyConnectedIntDi[Graph](Graph)
        
    def `should conform to tiny metrics` {
      forAll(arbitrary[IntDiGraph]) { g: IntDiGraph =>
        checkMetrics(g, GraphGen.TinyInt)
      }
    }
  }

  object `small connected graph of [Int,DiEdge]` {
    implicit val arbitraryGraph = GraphGen.smallConnectedIntDi[Graph](Graph)
        
    def `should conform to small metrics` {
      forAll(arbitrary[IntDiGraph]) { g: IntDiGraph =>
        checkMetrics(g, GraphGen.SmallInt)
      }
    }
  }
}