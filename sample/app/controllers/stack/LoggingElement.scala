package controllers.stack

import com.jaroop.play.stackc.StackableController
import play.api.Logger
import play.api.mvc.{ BaseController, Request, Result }

trait LoggingElement extends StackableController { self: BaseController =>

  override def cleanupOnSucceeded[A](req: Request[A], res: Option[Result]): Unit = {
      res.map { result =>
        Logger.debug(Array(result.header.status, req.toString(), req.body).mkString("\t"))
      }
      super.cleanupOnSucceeded(req, res)
  }

}
