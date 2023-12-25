package com.crm.model

import zio.Task
import zio.http.Request
import zio.prelude._

import java.util.Objects

case class Contact(name: String, surname: String, email: String)

object Contact {
    def getFormAsMap(request: Request): Task[Map[String, String]] = for {
      formString <- request.body.asString
      formMap = formString.split("&").map { params =>
        val Array(key, value) = params.split("=")
        key -> java.net.URLDecoder.decode(value, "UTF-8")
      }.toMap
    } yield formMap

  def validateInput(formMap: Map[String, String]) = for {
    name <- formMap.get("name")
    surname <- formMap.get("surname")
    email <- formMap.get("email")
    contactValidator = ContactValidator.validateContact(name, surname, email)
  } yield contactValidator
}
sealed trait AppError extends Throwable

object AppError {
  final case class AccountError(message: String) extends AppError
  case class MissingBodyError(message: String) extends AppError
}

object ContactValidator {
  def validateName(name: String): Validation[String, String] =
    if (name.nonEmpty) Validation.succeed(name)
    else Validation.fail("Name must not be empty")

  def validateSurname(surname: String): Validation[String, String] =
    if (surname.nonEmpty) Validation.succeed(surname)
    else Validation.fail("Surname must not be empty")

  def validateEmail(email: String): Validation[String, String] =
    if (Objects.nonNull(email) && email.nonEmpty && email.contains("@")) Validation.succeed(email)
    else Validation.fail("Email must not be empty")

  def validateContact(name: String, surname: String, email: String): Validation[String, Contact] =
    Validation.validateWith(validateName(name), validateSurname(surname), validateEmail(email))(Contact.apply)
}