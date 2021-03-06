package qq.droste
package tests

import org.scalacheck.Properties
import org.scalacheck.Prop._

import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import eu.timepit.refined.scalacheck.numeric._

import cats.Eval
import cats.instances.option._
import cats.syntax.eq._

import prelude._
import data.prelude._
import data.Attr
import data.Fix
import data.Mu
import data.Nu
import data.list._
import syntax.attr._

class SchemePartialBasisTests extends Properties("SchemePartialBasis") {
  val sumListFIntAlgebra: Algebra[ListF[Int, ?], Int]  = Algebra {
    case ConsF(x, y) => x + y
    case NilF => 0
  }

  def sumListInt(l: List[Int]): Int = l.foldLeft(0)(_ + _)

  property("scheme[Fix].ana") = {

    val f = scheme[Fix].ana(
      Coalgebra((n: Int) => if (n > 0) Some(n - 1) else None))

    def expected(n: Int): Fix[Option] =
      if (n > 0) Fix(Some(expected(n - 1)))
      else Fix(None: Option[Fix[Option]])

    forAll((n: Int Refined Less[W.`100`.T]) => f(n) ?= expected(n))
  }

  property("scheme[Attr[?[_], Int]].ana") = {

    val f = scheme[Attr[?[_], Int]].ana(
      Coalgebra((n: Int) => (if (n > 0) Some(n - 1) else None) attr n))

    def expected(n: Int): Attr[Option, Int] =
      if (n > 0) Attr(n, Some(expected(n - 1)))
      else Attr(n, None: Option[Attr[Option, Int]])

    forAll((n: Int Refined Less[W.`100`.T]) => f(n) ?= expected(n))
  }

  property("scheme[cats.free.Cofree[?[_], Int]].ana") = {

    val f = scheme[cats.free.Cofree[?[_], Int]].ana(
      Coalgebra((n: Int) => (if (n > 0) Some(n - 1) else None) attr n))

    def expected(n: Int): cats.free.Cofree[Option, Int] =
      if (n > 0) cats.free.Cofree(n, Eval.now(Some(expected(n - 1))))
      else cats.free.Cofree(n, Eval.now(None: Option[cats.free.Cofree[Option, Int]]))

    forAll((n: Int Refined Less[W.`100`.T]) => f(n) ?= expected(n))
  }

  property("scheme[Mu].ana") = {

    val f = scheme[Mu].ana(
      Coalgebra((n: Int) => if (n > 0) Some(n - 1) else None))

    def expected(n: Int): Mu[Option] =
      if (n > 0) Mu(Some(expected(n - 1)))
      else Mu(None: Option[Mu[Option]])

    forAll((n: Int Refined Less[W.`100`.T]) => f(n) ?= expected(n))
  }

  property("scheme[Nu].ana") = {

    val f = scheme[Nu].ana(
      Coalgebra((n: Int) => if (n > 0) Some(n - 1) else None))

    def expected(n: Int): Nu[Option] =
      if (n > 0) Nu(Some(expected(n - 1)))
      else Nu(None: Option[Nu[Option]])

    forAll((n: Int Refined Less[W.`100`.T]) => f(n) === expected(n))
  }

  property("scheme[Mu].cata") = {

    val f = scheme[Mu].cata(sumListFIntAlgebra)

    forAll((l: List[Int]) => f(ListF.fromScalaList[Int, Mu](l)) ?= sumListInt(l))
  }

  property("scheme[Mu].gcata") = {

    val f = scheme[Mu].gcata(sumListFIntAlgebra.gather(Gather.cata))

    forAll((l: List[Int]) => f(ListF.fromScalaList[Int, Mu](l)) ?= sumListInt(l))
  }

  property("scheme[Mu].gcataM") = {

    val f = scheme[Mu].gcataM(sumListFIntAlgebra.lift[Eval].gather(Gather.cata))

    forAll((l: List[Int]) => f(ListF.fromScalaList[Int, Mu](l)).value ?= sumListInt(l))
  }

  property("scheme[Mu].gana") = {

    val f = scheme[Mu].gana(
      Coalgebra((n: Int) => if (n > 0) Some(n - 1) else None).scatter(Scatter.ana))

    def expected(n: Int): Mu[Option] =
      if (n > 0) Mu(Some(expected(n - 1)))
      else Mu(None: Option[Mu[Option]])

    forAll((n: Int Refined Less[W.`100`.T]) => f(n) ?= expected(n))
  }

  property("scheme[Mu].ganaM") = {

    val f = scheme[Mu].ganaM(
      Coalgebra((n: Int) => if (n > 0) Some(n - 1) else None).lift[Eval].scatter(Scatter.ana))

    def expected(n: Int): Mu[Option] =
      if (n > 0) Mu(Some(expected(n - 1)))
      else Mu(None: Option[Mu[Option]])

    forAll((n: Int Refined Less[W.`100`.T]) => f(n).value ?= expected(n))
  }
}
