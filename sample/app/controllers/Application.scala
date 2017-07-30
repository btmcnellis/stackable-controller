package controllers

import play.api.mvc._
import models._
import views._
import controllers.stack._
import com.jaroop.play.stackc.RequestWithAttributes

object Application extends Controller with DBSessionElement with LoggingElement {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def messages = StackAction { implicit req =>
    val messages = Message.findAll
    Ok(views.html.messages(messages))
  }

  def editMessage(id: MessageId) = StackAction { implicit req =>
    val messages = Message.findAll
    Ok(views.html.messages(messages))
  }

}
