package com.crm.model
import zio._
import zio.http._
import zio.json.{DecoderOps, DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

sealed trait AppError extends Exception

object AppError {
  case class JokeRequestFailureException(message: String) extends AppError
}

case class Joke(id: String, joke: String, status: Int)

object Joke {
  implicit val decoder: JsonDecoder[Joke] = DeriveJsonDecoder.gen[Joke]

  implicit val encoder: JsonEncoder[Joke] = DeriveJsonEncoder.gen[Joke]
}

sealed trait Status

case object Active extends Status
case object Inactive extends Status
case class Contact(id: String, name: String, email: String, status: Status) {
  def toContactDTO =
    ContactDTO(id, name, email, if(status == Active) "Active"
    else "Inactive")
}
case class ContactDTO(id: String, name: String, email: String, status: String) {
  def toContact =
    Contact(id = id, name = name, email = email, status = if(status == "Active") Active else Inactive )
}

object ContactDb {
  import scala.collection.mutable.Map
  val contacts: Map[String, Contact] = Map[String, Contact](
    "1" -> Contact("1", "Joe Smith", "joe@smith.org", Active),
    "2" -> Contact("2", "Angie MacDowell", "angie@macdowell.org", Active),
    "3" -> Contact("3", "Fuqua Tarkenton", "fuqua@tarkenton.org", Active),
    "4" -> Contact("4", "Kim Yee", "kim@yee.org", Inactive),
  )
}


//object SimpleClient extends ZIOAppDefault {
//  //val url = URL.decode("https://icanhazdadjoke.com/").toOption.get
//
//
//  val program = (for {
//    url  <- ZIO.fromOption(URL.decode("https://icanhazdadjoke.com/").toOption)
//    client <- ZIO.service[Client]
//    res    <- client.addHeader("Accept","application/json").url(url).get("/")
//    data   <- res.body.asString
//    joke  <- ZIO.from(data.fromJson[Joke])
//    _      <- Console.printLine(joke.joke)
//  } yield ())
//    .catchSome {
//      case e: java.net.UnknownHostException =>
//        Console.printLine("Unkown host")
//    }
//
//  override val run = program.provide(Client.default, Scope.default)
//
//}
