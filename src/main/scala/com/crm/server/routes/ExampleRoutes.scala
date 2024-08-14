package com.crm.server.routes
import com.crm.model.DateFormater.toLocalDate
import com.crm.model.LocalDateUtil.getDaysOfCurrentWeek
import com.crm.model.{CarModelStore, ContactInfo, Todo, TodoStore}
import com.crm.server.renderer.ViewRenderer
import com.crm.server.renderer.ViewRenderer._
import com.crm.server.routes.LoginValidation.validateLogin
import com.crm.server.routes.middleware.CustomMiddleware.{cookieBearer, hxRequest}
import com.crm.services.{ContactService, ProjectService, TimeRegistrationService}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import zio.http.endpoint.EndpointNotFound
import zio.http.{Cookie, FormField, HttpApp, Method, Request, Response, RoutePattern, Routes, Status, handler, int, string}
import zio.prelude.Validation
import zio.{Chunk, ZIO, ZLayer}

import java.time.format.DateTimeFormatter
import java.time.{Clock, Duration}
import java.util.{Locale, UUID}

class ExampleRoutes {

  // Secret Authentication key
  val SECRET_KEY = "secretKey"

  implicit val clock: Clock = Clock.systemUTC

  // Helper to encode the JWT token
  def jwtEncode(username: String): String = {
    val json  = s"""{"user": "${username}"}"""
    val claim = JwtClaim {
      json
    }.issuedNow.expiresIn(300)
    Jwt.encode(claim, SECRET_KEY, JwtAlgorithm.HS512)
  }

  // Helper to decode the JWT token
  def jwtDecode(token: String): Option[JwtClaim] = {
    Jwt.decode(token, "secretKey", Seq(JwtAlgorithm.HS512)).toOption
  }

  val notFound = RoutePattern.any

  val clickToEdit = Method.GET / "click-to-edit" -> handler { (request: Request) =>
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

  val add_todo = Method.POST / "add-todo" -> handler { (request: Request) =>
    for {
      payloadForm <- request.body.asURLEncodedForm
      todo = payloadForm.get("name") match {
        case Some(formfield) => formfield.stringValue.getOrElse("")
        case _ => ""
      }
      newTodo = Todo(todo, false)
      _ = TodoStore.add(newTodo)
    } yield {
      val content = examples.snippets.html.todoItem(newTodo, TodoStore.todos.length)
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
  } @@ hxRequest()  @@ cookieBearer() // hxTrigger("custom-event")

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
    val formPayloadRequest = for {
      formPayLoad <- request.body.asURLEncodedForm
    } yield formPayLoad

    formPayloadRequest.flatMap {fr =>
      val loginDTO = new LoginDTO()
      val validation =  validateLogin(fr.get("email"),
        fr.get("password"), loginDTO)

      validation.fold (
        failure =>{
          val content = examples.snippets.html.validatedform(failure, loginDTO)
          ZIO.succeed(render(content.body))
        },
        login => {
          if(login.email == "username@email.com" && login.password == "password") {
            val jwt = jwtEncode(login.email)
            val cookie = Cookie.Response("jwt", jwt)

            //.addHeader("HX-Refresh", "true")
            ZIO.succeed(Response.text(s"***Form entries are valid ${login}").addCookie(cookie))
          }
          else{
            val content = examples.snippets.html.validatedform(Chunk("Incorrect username or " +
              "password"), loginDTO)
            ZIO.succeed(render(content.body))
          }
        }
      )
    }
  }

  val activeSearch = Method.GET / "active-search" -> handler {
    val content = examples.html.activeSearch()
    render(content.body)
  }

  val search = Method.POST / "search" -> handler { (request: Request) =>
    for {
      form <- request.body.asURLEncodedForm
      search = form.get("search")
      result = search match {
        case Some(s) =>
            s.stringValue match {
              case Some(formField) =>
                ContactInfo.contactList.filter(
                  c => c.name.toLowerCase().contains(formField.toLowerCase()) ||
                       c.surname.toLowerCase().contains(formField.toLowerCase())
                )
              case _ => List[ContactInfo]()
            }
        case _ => List[ContactInfo]()
      }
    } yield {
      val content = examples.snippets.html.searchResults(result)
      render(content.body)
    }

  }

  val jwt_as_cookie_page = Method.GET / "jwt-cookie" -> handler {
    val content = examples.html.jwtAsCookie()
    render(content.body)
  }

  val oob_example_page = Method.GET / "oob-todo-example" -> handler {
    val content = examples.html.oobExample(TodoStore.todos)
    render(content.body)
  }

  val timeSheet = Method.GET / "timesheet" -> handler {
    val daysOfCurrentWeek = getDaysOfCurrentWeek(Locale.ENGLISH)
    println(daysOfCurrentWeek)
    val content = html.timesheet(projects = ProjectService.getProjects, daysOfCurrentWeek)
    render(content.body)
  }

  val updateTimesheet = Method.PUT / "projects" / int("projectId") / string("date") -> handler { ( projectId: Int, date: String, request: Request) =>
    val localDate = toLocalDate(date)

    for {
      payloadForm <- request.body.asURLEncodedForm
      value = payloadForm.get("value").flatMap(_.stringValue).map(_.toLong).getOrElse(0L)
      duration = if(value == 0) Duration.ZERO else  java.time.Duration.ofMinutes((value * 60.0).toLong)
      _ = TimeRegistrationService.addOrUpdateRegistration(projectId, localDate, duration)
      total = TimeRegistrationService.getTotal(ProjectService.getProjectIds, getDaysOfCurrentWeek(Locale.ENGLISH).toSet)
    } yield {
      val dateSuffix = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
      val content=  examples.snippets.html.projectTotals(total, Map(
        s"dayTotal_${dateSuffix}" -> TimeRegistrationService.getTotal(ProjectService.getProjectIds, Set(localDate))
      ))
      render(content.body)
    }
  }

  val cascading_selects = Method.GET / "cascading-selects" -> handler {
    val content = examples.html.cascadingSelects()
    render(content.body)
  }

  val getCarModels = Method.GET / "models" -> handler {(request: Request) =>
    val make = request.url.queryParams.get("make")
    val carModels = make match {
      case Some(c) => CarModelStore.getModel(c)
      case _ => List()
    }
    val content = examples.snippets.html.modelCarList(modelList = carModels)
    render(content.body)
  }


  val apps: HttpApp[Any] =
    Routes(clickToEdit, contactForm, editContactForm, contactFormPut, websocketDadJokeExample,
      bulkUpdate, loadBulkContacts, activateContact, deActivateContact, deleteRowPage,
      loadDeleteRows, deleteRow, editRowPage, loadEditRows, getContactByIdForm, updateContact,
      getContactRow, validateMultiplefields, loadValidateMultipleFields, login, activeSearch,
      search, jwt_as_cookie_page, oob_example_page, add_todo, timeSheet, updateTimesheet, cascading_selects,
      getCarModels)
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
class LoginDTO {
  var email: String = ""
}

object LoginValidation {
  def validateEmail(emailField: Option[FormField], loginDTO: LoginDTO): Validation[String, String] = emailField
  match {
    case Some(emailFormField) =>
      emailFormField.stringValue match {
        case Some(value) =>
          loginDTO.email = value
          if (value.isEmpty) Validation.fail("Email is empty")
          else if(!value.contains("@"))
            Validation.fail("This is not a valid email")
          else Validation.succeed(value)
        case _ =>  Validation.fail("Please email field is blank")
      }
    case _ =>   Validation.fail("Please enter an email field")
  }
  def validatePassword(passwordField: Option[FormField]): Validation[String, String] =
    passwordField match {
      case Some(passwordFormField) =>
        passwordFormField.stringValue match {
          case Some(value) =>
            if (value.isEmpty) Validation.fail(s"Password is empty")
            else Validation.succeed(value)
          case _ => Validation.fail("Please password field is blank")

        }
      case _ => Validation.fail("Please enter an password field")
    }
  def validateLogin(email: Option[FormField], password: Option[FormField], loginDTO: LoginDTO)
  : Validation[String, Login] =
    Validation.validateWith(validateEmail(email, loginDTO), validatePassword(password))(Login)

}
