package com.crm.server.routes
import examples.html.clickToEdit
import com.crm.server.renderer.ViewRenderer
import zio.ZLayer
import zio.http.{HttpApp, Method, Response, Routes, Status, handler}

class ExampleRoutes {

  val clickToEdit = Method.GET / "click-to-edit" -> handler {
    val content = examples.html.clickToEdit()
    ViewRenderer.render(content.body)
  }

  val contactForm = Method.GET / "contact-form" -> handler {
    val content = examples.snippets.html.contact()
    ViewRenderer.render(content.body)
  }

  val contactFormPut = Method.PUT / "contact-form" -> handler {
    val content = examples.snippets.html.contact()
    ViewRenderer.render(content.body)
  }

  val editContactForm = Method.GET / "edit-contact-from" -> handler {
    val content = examples.snippets.html.editcontact()
    ViewRenderer.render(content.body)
  }

  val apps: HttpApp[Any] =
    Routes(clickToEdit, contactForm, editContactForm, contactFormPut)
    .handleError { t: Throwable => Response.text("The error is " + t).status(Status
      .InternalServerError) }
    .toHttpApp

}

object ExampleRoutes {
  val layer: ZLayer[Any, Nothing, ExampleRoutes] = ZLayer.succeed(apply)

  def apply = new ExampleRoutes()

}
