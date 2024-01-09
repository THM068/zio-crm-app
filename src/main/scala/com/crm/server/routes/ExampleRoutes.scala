package com.crm.server.routes

import com.crm.server.renderer.ViewRenderer
import com.crm.server.renderer.ViewRenderer._
import com.crm.services.ContactService
import zio.ZLayer
import zio.http.{HttpApp, Method, Request, Response, Routes, Status, handler, string}

import java.util.UUID

class ExampleRoutes {

  val clickToEdit = Method.GET / "click-to-edit" -> handler {
    val content = examples.html.clickToEdit()
    render(content.body)
  }

  val contactForm = Method.GET / "contact-form" -> handler {
    val content = examples.snippets.html.contact()
    render(content.body)
  }

  val contactFormPut = Method.PUT / "contact-form" -> handler {
    val content = examples.snippets.html.contact()
    render(content.body)
  }

  val editContactForm = Method.GET / "edit-contact-from" -> handler {
    val content = examples.snippets.html.editcontact()
    render(content.body)
  }

  val websocketDadJokeExample = Method.GET / "dad-joke-websocket-example" -> handler {
    val id = UUID.randomUUID().toString
    val content = examples.html.websocketExample(id)
    render(content.body)
  }

  val bulkUpdate = Method.GET / "bulk-update" -> handler {
    val content = examples.html.bulkupdate()
    render(content.body)
  }
  val loadBulkContacts = Method.GET / "load-bulk-contacts" -> handler {
    val content = examples.snippets.html.loadbulkcontacts(ContactService.contacts())
    render(content.body)
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
      render(content.body)
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
      render(content.body)
    }
  }

  val deleteRowPage = Method.GET / "delete-row" -> handler {
    val content = examples.html.deleterow()
    render(content.body)
  }

  val loadDeleteRows = Method.GET / "load-delete-rows" -> handler {
    val content = examples.snippets.html.loaddeleterows(ContactService.contacts())
    render(content.body)
  }

  val deleteRow = Method.DELETE / "contact" / string("id") -> handler { (id: String, _: Request) =>
    ContactService.deleteContact(id)
    render("")
  }

  val editRowPage = Method.GET / "edit-row" -> handler {
    val content = examples.html.editrow()
    render(content.body)
  } //

  val loadEditRows = Method.GET / "load-edit-rows" -> handler {
    val content = examples.snippets.html.loadeditrows(ContactService.contacts())
    render(content.body)
  }

  val getContactByIdForm = Method.GET / "contact" / string("id") / "edit" -> handler { (id: String,
                                                                                        _: Request) =>
    val contact = ContactService.getContact(id).get
    val content = examples.snippets.html.editcontactform(contact)
    render(content.body)
  }

  val updateContact = Method.PUT / "contact" / string("id") -> handler { (id: String,
                                                                          request: Request) =>
    for {
      payloadForm <- request.body.asURLEncodedForm
      contactDTO = (payloadForm.get("name"), payloadForm.get("email")) match {
        case (Some(nameField), Some(emailField)) =>
          val contactDTO = ContactService.getContact(id)
          val contact = contactDTO.get.toContact.copy(name = nameField.stringValue.get, email = emailField
            .stringValue.get)
          ContactService.updateContact(contact).toContactDTO
      }
    } yield {
      val content = examples.snippets.html.updaterow(contactDTO)
      ViewRenderer.render(content.body)
    }
  }

  val getContactRow = Method.GET / "contact" / string("id") -> handler { (id: String,
                                                                          request: Request) =>
    val contactDTO = ContactService.getContact(id)
    contactDTO match {
      case Some(c) =>
        val content = examples.snippets.html.updaterow(c)
        ViewRenderer.render(content.body)
      case _ =>
        ViewRenderer.render("")
    }

  }

  val apps: HttpApp[Any] =
    Routes(clickToEdit, contactForm, editContactForm, contactFormPut, websocketDadJokeExample,
      bulkUpdate, loadBulkContacts, activateContact, deActivateContact, deleteRowPage,
      loadDeleteRows, deleteRow, editRowPage, loadEditRows, getContactByIdForm, updateContact, getContactRow)
      .handleError { t: Throwable =>
        Response.text("The error is " + t).status(Status
          .InternalServerError)
      }
      .toHttpApp

}

object ExampleRoutes {
  val layer: ZLayer[Any, Nothing, ExampleRoutes] = ZLayer.succeed(apply)

  def apply = new ExampleRoutes()

}
