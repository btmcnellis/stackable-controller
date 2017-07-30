package controllers.stack

import com.jaroop.play.stackc.{ RequestAttributeKey, RequestWithAttributes, StackableController }
import play.api.mvc.{ BaseController, Request, Result }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scalikejdbc._

trait DBSessionElement extends StackableController { self: BaseController =>

  case object DBSessionKey extends RequestAttributeKey[DBSession]

  override def proceed[A](req: RequestWithAttributes[A])(f: RequestWithAttributes[A] => Future[Result]): Future[Result] = {
    import TxBoundary.Future._
    DB.localTx { session =>
      super.proceed(req.set(DBSessionKey, session))(f)
    }
  }

  implicit def dbSession[A](implicit req: Request[A]): DBSession = req.attrs(DBSessionKey)

}
