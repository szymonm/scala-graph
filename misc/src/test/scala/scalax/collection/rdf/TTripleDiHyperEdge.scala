package scalax.collection
package rdf

import org.scalatest.Suite
import org.scalatest.matchers.ShouldMatchers

import org.scalatest.junit.JUnitRunner

import org.junit.runner.RunWith

/** Demonstrates a triple store based on the custom directed edge `TripleDiHyperEdge`.
 * 
 *  @author Peter Empen
 */
@RunWith(classOf[JUnitRunner])
class TTripleDiHyperEdge extends Suite with ShouldMatchers {

  type Triple[N] = TripleDiHyperEdge[N]
  val Triple = TripleDiHyperEdge

  val (subj_1, subj_2) = (IRI("s-1"), IRI("s-2"))
  val pred             = IRI("p-1")
  val (obj_1, obj_2)   = (Label("l-1"), BlankNode(1))
  val initialTriples = List[Triple[RdfNode]](
      Triple(subj_1, pred, obj_1),
      Triple(subj_2, pred, obj_2)
  )
  def test_creation {
    val g: Graph[RdfNode,Triple] = Graph.from(edges = initialTriples)

    g.order should be (5)
    g.graphSize should be (initialTriples.size)
  }
  def test_outerEdge {
    val stmt = initialTriples.head
    
    stmt.subject   should be (subj_1)
    stmt.predicate should be (pred)
    stmt.`object`  should be (obj_1)
  }
  def test_innerEdge {
    val g = Graph.from(edges = initialTriples)
    val stmt = g.get(initialTriples.head)
    
    stmt.subject   should be (subj_1)
    stmt.predicate should be (pred)
    stmt.`object`  should be (obj_1)
  }
}