package com.jaroop.play.stackc

import com.jaroop.play.stackc._
import play.api.libs.typedmap.{ TypedEntry, TypedKey, TypedMap }
import play.api.mvc._
import scala.concurrent.{ Future, ExecutionContext }
import scala.language.implicitConversions
import scala.util.{ Failure, Success }
import scala.util.control.{ NonFatal, ControlThrowable }

trait StackableController { self: BaseController =>

  final class StackActionBuilder[B](val parser: BodyParser[B], params: TypedEntry[_]*) extends ActionBuilder[Request, B] {

    def executionContext = defaultExecutionContext

    def invokeBlock[A](req: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      val request = req.withAttrs(TypedMap(params:_*))
      try {
        cleanup(request, proceed(request)(block))(StackActionExecutionContext(request))
      } catch {
        case e: ControlThrowable => cleanupOnSucceeded(request, None); throw e
        case NonFatal(e) => cleanupOnFailed(request, e); throw e
      }
    }
  }

  final def AsyncStack[A](p: BodyParser[A], params: TypedEntry[_]*)(f: Request[A] => Future[Result]): Action[A] = new StackActionBuilder(p, params: _*).async(p)(f)
  final def AsyncStack(params: TypedEntry[_]*)(f: Request[AnyContent] => Future[Result]): Action[AnyContent] = new StackActionBuilder(parse.default, params: _*).async(f)
  final def AsyncStack(f: Request[AnyContent] => Future[Result]): Action[AnyContent] = new StackActionBuilder(parse.default).async(f)

  final def StackAction[A](p: BodyParser[A], params: TypedEntry[_]*)(f: Request[A] => Result): Action[A] = new StackActionBuilder(p, params: _*).apply(p)(f)
  final def StackAction(params: TypedEntry[_]*)(f: Request[AnyContent] => Result): Action[AnyContent] = new StackActionBuilder(parse.default, params: _*).apply(f)
  final def StackAction(f: Request[AnyContent] => Result): Action[AnyContent] = new StackActionBuilder(parse.default).apply(f)

  @deprecated("Replace RequestWithAttributes with play.api.mvc.Request.", "0.8.0")
  def proceed[A](request: RequestWithAttributes[A])(f: RequestWithAttributes[A] => Future[Result]): Future[Result] = f(request)
  def proceed[A](request: Request[A])(f: Request[A] => Future[Result]): Future[Result] = f(request)

  @deprecated("Replace RequestWithAttributes with play.api.mvc.Request.", "0.8.0")
  def cleanupOnSucceeded[A](request: RequestWithAttributes[A], result: Option[Result]): Unit = cleanupOnSucceeded(request)
  def cleanupOnSucceeded[A](request: Request[A], result: Option[Result]): Unit = cleanupOnSucceeded(request)

  @deprecated("Replace RequestWithAttributes with play.api.mvc.Request.", "0.8.0")
  def cleanupOnSucceeded[A](request: RequestWithAttributes[A]): Unit = ()
  def cleanupOnSucceeded[A](request: Request[A]): Unit = ()

  @deprecated("Replace RequestWithAttributes with play.api.mvc.Request.", "0.8.0")
  def cleanupOnFailed[A](request: RequestWithAttributes[A], e: Throwable): Unit = ()
  def cleanupOnFailed[A](request: Request[A], e: Throwable): Unit = ()

  private def cleanup[A](request: Request[A], result: Future[Result])(implicit ctx: ExecutionContext): Future[Result] = result andThen {
    case Success(p) => cleanupOnSucceeded(request, Some(p))
    case Failure(e) => cleanupOnFailed(request, e)
  }

  protected val ExecutionContextKey = TypedKey[ExecutionContext]

  protected def StackActionExecutionContext(implicit req: Request[_]): ExecutionContext =
    req.attrs.get(ExecutionContextKey).getOrElse(defaultExecutionContext)

}

@deprecated("Migrate to play.api.libs.typedmap.TypedKey.", "0.8.0")
trait RequestAttributeKey[A] {

  def ->(value: A): Attribute[A] = Attribute(this, value)

  def →(value: A): Attribute[A] = Attribute(this, value)

}

@deprecated("Migrate to play.api.libs.typedmap.TypedEntry.", "0.8.0")
case class Attribute[A](key: RequestAttributeKey[A], value: A) {

  def toTuple: (RequestAttributeKey[A], A) = (key, value)

}

@deprecated("Migrate to play.api.mvc.Request, which supports typed attributes as of Play 2.6.", "0.8.0")
class RequestWithAttributes[A](private val underlying: Request[A]) extends WrappedRequest[A](underlying) {

  def get[B](key: RequestAttributeKey[B]): Option[B] = underlying.attrs.get(TypedKey.apply[B])

  def set[B](key: RequestAttributeKey[B], value: B): RequestWithAttributes[A] = {
    val newReq = underlying.addAttr(TypedKey.apply[B], value)
    new RequestWithAttributes(newReq)
  }

}

object RequestWithAttributes {

  /** An implicit conversion from RequestWithAttributes to play.api.mvc.Request to ease the migration to Play 2.6.
   *
   *  @todo Remove this and related conversions in version 0.8.0.
   */
  implicit def toPlayRequest[A](req: RequestWithAttributes[A]): Request[A] = req.underlying

}

object Attribute {

  /** An implicit conversion from Attribute to TypedEntry. */
  implicit def toTypedEntry[A](attr: Attribute[A]): TypedEntry[A] = TypedKey.apply[A] -> attr.value

  /** An implicit conversion from a sequence of Attributes to a sequence of TypedEntries. */
  implicit def toTypedEntrySeq(attrs: Seq[Attribute[_]]): Seq[TypedEntry[_]] = attrs.map(toTypedEntry(_))

}

object RequestAttributeKey {

  /** An implicit conversion from RequestAttributeKey to play.api.libs.typedmap.TypedKey. */
  implicit def toTypedKey[A](key: RequestAttributeKey[A]): TypedKey[A] = TypedKey.apply[A]

}
