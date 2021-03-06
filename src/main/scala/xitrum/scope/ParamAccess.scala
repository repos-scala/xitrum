package xitrum.scope

import scala.collection.JavaConversions
import scala.collection.mutable.{Map => MMap}

import xitrum.Action
import xitrum.exception.MissingParam

trait ParamAccess {
  this: Action =>

  import Env.Params

  /**
   * text (uriParams, bodyParams, pathParams) vs file upload (fileParams)
   *
   * Lazily initialized, so that bodyParams can be
   * changed by ValidatorCaller. Because this is a lazy val, once this is accessed,
   * the 3 params should not be changed, because the change will not be reflected
   * by this val.
   *
   * Not a function ("def") so that the calculation is done only once.
   */
  lazy val textParams: Params = {
    val ret = MMap[String, Array[String]]()

    // The order is important because we want the later to overwrite the former
    ret ++= henv.uriParams
    ret ++= henv.bodyParams
    ret ++= henv.pathParams

    ret
  }

  /**
   * Returns a singular element.
   */
  def param(key: String, coll: Params = null): String = {
    val coll2 = if (coll == null) textParams else coll
    if (coll2.contains(key)) coll2.apply(key)(0) else throw new MissingParam(key)
  }

  def paramo(key: String, coll: Params = null): Option[String] = {
    val coll2 = if (coll == null) textParams else coll
    val values = coll2.get(key)
    if (values.isEmpty) None else Some((values.get)(0))
  }

  /**
   * Returns a list of elements.
   */
  def params(key: String, coll: Params = null): Array[String] = {
    val coll2 = if (coll == null) textParams else coll
    if (coll2.contains(key))
      coll2.apply(key)
    else
      throw new MissingParam(key)
  }

  def paramso(key: String, coll: Params = null): Option[Array[String]] = {
    val coll2 = if (coll == null) textParams else coll
    coll2.get(key)
  }

  //----------------------------------------------------------------------------

  def tparam[T](key: String, coll: Params = null)(implicit m: Manifest[T]): T = {
    val value = param(key, coll)
    convert[T](value)
  }

  def tparamo[T](key: String, coll: Params = null)(implicit m: Manifest[T]): Option[T] = {
    val valueo = paramo(key, coll)
    valueo.map(convert[T](_))
  }

  def tparams[T](key: String, coll: Params = null)(implicit m: Manifest[T]): Array[T] = {
    val values = params(key, coll)
    values.map(convert[T](_))
  }

  def tparamso[T](key: String, coll: Params = null)(implicit m: Manifest[T]): Option[Array[T]] = {
    paramso(key, coll) match {
      case None         => None
      case Some(values) => Some(values.map(convert[T](_)))
    }
  }

  private def convert[T](value: String)(implicit m: Manifest[T]): T = {
    val v = m.toString match {
      case "int"    => value.toInt
      case "short"  => value.toShort
      case "long"   => value.toLong
      case "float"  => value.toFloat
      case "double" => value.toDouble
      case unknown  => throw new Exception("Cannot covert String to " + unknown)
    }
    v.asInstanceOf[T]
  }
}
