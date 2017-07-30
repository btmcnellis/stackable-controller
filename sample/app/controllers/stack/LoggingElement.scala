package controllers.stack

import com.jaroop.play.stackc.{ RequestWithAttributes, StackableController }
import play.api.Logger
import play.api.mvc.{ BaseController, Result }

trait LoggingElement extends StackableController { self: BaseController =>

  override def cleanupOnSucceeded[A](req: RequestWithAttributes[A], res: Option[Result]): Unit = {
      res.map { result =>
        Logger.debug(Array(result.header.status, req.toString(), req.body).mkString("\t"))
      }
      super.cleanupOnSucceeded(req, res)
  }

}
