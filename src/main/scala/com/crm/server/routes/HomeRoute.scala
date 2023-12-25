package com.crm.server.routes
import com.crm.model.{AppError, Contact}
import zio.prelude.Validation
import com.crm.server.renderer.ViewRenderer
import zio._
import zio.http._

class HomeRoute() {

  val index = Method.GET / "" -> handler {
    val content = html.IndexPage(List("Apple", "Oranges", "Mangoes"), "Home")
    ViewRenderer.render(content.body)
  }

  val about = Method.GET / "about" -> handler {
    val content = html.about()
    ViewRenderer.render(content.body)
  }

  val contact = Method.GET / "contact" -> handler {
    val content = html.contact()
    ViewRenderer.render(content.body)
  }

  val contactPost = Method.POST / "contact" -> handler { (request: Request) =>
    (for {
      contactFormMap <- Contact.getFormAsMap(request)
      validationForm <- ZIO.fromOption(Contact.validateInput(contactFormMap))
      contact = validationForm.getOrElseWith(error => error.mkString(","))

    } yield {
      contact match {
        case c @ Contact(name, surname,email ) =>  Response.text(s"success ${c}")
        case msg => Response.text(s"error ${msg}").status(Status.BadRequest)
      }

    }).catchSome {
      case AppError.MissingBodyError(message) =>
        ZIO.succeed(Response.text(s"error ${message}"))
    }
  }

  val apps: HttpApp[Any] = Routes(index, about, contact, contactPost)
    .handleError { t => Response.text("The error is " + t).status(Status
      .InternalServerError) }
    .toHttpApp

    def handle(throwable: Throwable) = {
      Response.text("The error is " + throwable).status(Status.InternalServerError)
    }

}

object HomeRoute {
  val layer: ZLayer[Any, Nothing, HomeRoute] = ZLayer.succeed(apply)

  def apply = new HomeRoute()

}

