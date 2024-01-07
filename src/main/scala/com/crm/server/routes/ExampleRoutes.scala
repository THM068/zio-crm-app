package com.crm.server.routes
import examples.html.clickToEdit
import com.crm.server.renderer.ViewRenderer
import com.crm.services.ContactService
import zio.ZLayer
import zio.http.{HttpApp, Method, Request, Response, Routes, Status, handler}

import java.util.UUID

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

  val websocketDadJokeExample = Method.GET / "dad-joke-websocket-example" -> handler {
    val id = UUID.randomUUID().toString
    val content = examples.html.websocketExample(id)
    ViewRenderer.render(content.body)
  }

  val bulkUpdate = Method.GET / "bulk-update" -> handler {
    val content = examples.html.bulkupdate()
    ViewRenderer.render(content.body)
  }
  val loadBulkContacts = Method.GET / "load-bulk-contacts" -> handler {
    val content = examples.snippets.html.loadbulkcontacts(ContactService.contacts())
    ViewRenderer.render(content.body)
  }

  val activateContact = Method.PUT / "activate" -> handler { (request: Request) =>
    for {
      payloadForm <- request.body.asURLEncodedForm
      idsString = payloadForm.get("ids") match {
        case Some(formfield) => formfield.stringValue.getOrElse("")
        case _ => ""
      }
      idList = idsString.split(",").toList
      list = ContactService.activatefor(idList)
    } yield {
      val content = examples.snippets.html.contactlistbody(list, idList, "activate")
      ViewRenderer.render(content.body)
    }
  }

  val deActivateContact = Method.PUT / "deactivate" -> handler { (request: Request) =>
    for {
      payloadForm <- request.body.asURLEncodedForm
      idsString = payloadForm.get("ids") match {
        case Some(formfield) => formfield.stringValue.getOrElse("")
        case _ => ""
      }
      idList = idsString.split(",").toList
      list = ContactService.deActivatefor(idList)
    } yield {
      val content = examples.snippets.html.contactlistbody(list, idList, "deactivate")
      ViewRenderer.render(content.body)
    }
  }


  val apps: HttpApp[Any] =
    Routes(clickToEdit, contactForm, editContactForm, contactFormPut, websocketDadJokeExample,
      bulkUpdate, loadBulkContacts, activateContact, deActivateContact)
    .handleError { t: Throwable => Response.text("The error is " + t).status(Status
      .InternalServerError) }
    .toHttpApp

}

object ExampleRoutes {
  val layer: ZLayer[Any, Nothing, ExampleRoutes] = ZLayer.succeed(apply)

  def apply = new ExampleRoutes()

}
