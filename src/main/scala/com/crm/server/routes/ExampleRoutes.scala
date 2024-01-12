package com.crm.server.routes
import zio.prelude.Validation
import com.crm.server.renderer.ViewRenderer
import com.crm.server.renderer.ViewRenderer._
import com.crm.server.routes.LoginValidation.validateLogin
import com.crm.services.ContactService
import zio.{ZIO, ZLayer}
import zio.http.endpoint.EndpointNotFound
import zio.http.{HttpApp, Method, Request, Response, Route, RoutePattern, Routes, Status, handler, string}
import zio.prelude.ZValidation.Success

import java.util.UUID

class ExampleRoutes {

  val notFound = RoutePattern.any

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



  } //validate-multiple-fields

  val validateMultiplefields = Method.GET / "validate-multiple-fields"  -> handler {
    val content = examples.html.validatemultiplefields()
    render(content.body)
  }

  val loadValidateMultipleFields = Method.GET / "load-validate-multiple-rows" -> handler {
    val content = examples.snippets.html.loadmultiplefields()
    render(content.body)
  }

  val login = Method.POST / "login"  -> handler { (request: Request) =>
    for {
      formPayLoad <- request.body.asURLEncodedForm
      validation =  (formPayLoad.get("email"), formPayLoad.get("password")) match {
        case (Some(email), Some(password)) =>
          validateLogin(email.stringValue.getOrElse(""), password.stringValue.getOrElse(""))
        case (None,Some(password)) =>
          validateLogin("",password.stringValue.getOrElse(""))
        case (Some(email),None) =>
          validateLogin(email.stringValue.getOrElse(""),"")
        case (None, None)  =>
          validateLogin("","")
      }


    } yield validation match {
      case Success(log, value) =>
        Response.text("Form entries are valid")
      case Validation.Failure(log, errors) =>
         println(errors.toString())
         println(log)
        val content = examples.snippets.html.validatedform(errors)
        render(content.body)
    }
  }

  val apps: HttpApp[Any] =
    Routes(clickToEdit, contactForm, editContactForm, contactFormPut, websocketDadJokeExample,
      bulkUpdate, loadBulkContacts, activateContact, deActivateContact, deleteRowPage,
      loadDeleteRows, deleteRow, editRowPage, loadEditRows, getContactByIdForm, updateContact,
      getContactRow, validateMultiplefields, loadValidateMultipleFields, login)
      .handleError { t: Throwable =>
        if(t.isInstanceOf[EndpointNotFound])
          Response.text("Not found")
        else
          Response.text("The error is " + t).status(Status
          .InternalServerError)
      }
      .toHttpApp

}

object ExampleRoutes {
  val layer: ZLayer[Any, Nothing, ExampleRoutes] = ZLayer.succeed(apply)

  def apply = new ExampleRoutes()

}


case class Login(email: String, password: String)

object LoginValidation {
  def validateEmail(email: String): Validation[String, String] =
    if (email.isEmpty) Validation.fail("Email is empty")
    else if(!email.contains("@"))
      Validation.fail("This is not a valid email")
    else Validation.succeed(email)

  def validatePassword(password: String): Validation[String, String] =
    if (password.isEmpty) Validation.fail(s"Password is empty")
    else Validation.succeed(password)

  def validateLogin(email: String, password: String): Validation[String, Login] =
    Validation.validateWith(validateEmail(email), validatePassword(password))(Login)

}
