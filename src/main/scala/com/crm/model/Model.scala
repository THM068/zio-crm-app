package com.crm.model

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

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
case class ContactInfo(name: String, surname: String, email: String)
case class Contact(id: String, name: String, email: String, status: Status) {
  def toContactDTO =
    ContactDTO(id, name, email, if (status == Active) "Active"
    else "Inactive")
}

case class ContactDTO(id: String, name: String, email: String, status: String) {
  def toContact =
    Contact(id = id, name = name, email = email, status = if (status == "Active") Active else Inactive)
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


object ContactInfo {
  val input =
    """"|Venus	Grimes	lectus.rutrum@Duisa.edu
      |Fletcher	Owen	metus@Aenean.org
      |William	Hale	eu.dolor@risusodio.edu
      |TaShya	Cash	tincidunt.orci.quis@nuncnullavulputate.co.uk
      |Kevyn	Hoover	tristique.pellentesque.tellus@Cumsociis.co.uk
      |Jakeem	Walker	Morbi.vehicula.Pellentesque@faucibusorci.org
      |Malcolm	Trujillo	sagittis@velit.edu
      |Wynne	Rice	augue.id@felisorciadipiscing.edu
      |Evangeline	Klein	adipiscing.lobortis@sem.org
      |Jennifer	Russell	sapien.Aenean.massa@risus.com
      |Rama	Freeman	Proin@quamPellentesquehabitant.net
      |Jena	Mathis	non.cursus.non@Phaselluselit.com
      |Alexandra	Maynard	porta.elit.a@anequeNullam.ca
      |Tallulah	Haley	ligula@id.net
      |Timon	Small	velit.Quisque.varius@gravidaPraesent.org
      |Randall	Pena	facilisis@Donecconsectetuer.edu
      |Conan	Vaughan	luctus.sit@Classaptenttaciti.edu
      |Dora	Allen	est.arcu.ac@Vestibulumante.co.uk
      |Aiko	Little	quam.dignissim@convallisest.net
      |Jessamine	Bauer	taciti.sociosqu@nibhvulputatemauris.co.uk
      |Gillian	Livingston	justo@atiaculisquis.com
      |Laith	Nicholson	elit.pellentesque.a@diam.org
      |Paloma	Alston	cursus@metus.org
      |Freya	Dunn	Vestibulum.accumsan@metus.co.uk
      |Griffin	Rice	justo@tortordictumeu.net
      |Catherine	West	malesuada.augue@elementum.com
      |Jena	Chambers	erat.Etiam.vestibulum@quamelementumat.net
      |Neil	Rodriguez	enim@facilisis.com
      |Freya	Charles	metus@nec.net
      |Anastasia	Strong	sit@vitae.edu
      |Bell	Simon	mollis.nec.cursus@disparturientmontes.ca
      |Minerva	Allison	Donec@nequeIn.edu
      |Yoko	Dawson	neque.sed@semper.net
      |Nadine	Justice	netus@et.edu
      |Hoyt	Rosa	Nullam.ut.nisi@Aliquam.co.uk
      |Shafira	Noel	tincidunt.nunc@non.edu
      |Jin	Nunez	porttitor.tellus.non@venenatisamagna.net
      |Barbara	Gay	est.congue.a@elit.com
      |Riley	Hammond	tempor.diam@sodalesnisi.net
      |""".stripMargin

  def contactList: List[ContactInfo] = {
    input.split("\n")
      .map(_.split("\\s"))
      .map(f => ContactInfo(f(0).trim, f(1).trim, f(2).trim))
      .toList

  }
}

case class Todo(name: String, status: Boolean)

object TodoStore {
  import scala.collection.mutable.ListBuffer;

  val todos: ListBuffer[Todo] = ListBuffer[Todo](
    Todo("Buy Milk",status = false),
    Todo("Buy Bread", status = false),
    Todo("Buy Eggs", status = false),
    Todo("Buy Butter", status = false)
  )

  def add(todo: Todo): Unit = {
    todos.append(todo)
  }

  def changeStatus(todo: Todo): Unit = {
    todos.indexOf(todo) match {
      case -1 => println("Todo not found")
      case i => todos(i) = todo.copy(status = !todo.status)
    }
  }

  def remove(todo: Todo): Unit = {
    todos.indexOf(todo) match {
      case -1 => println("Todo not found")
      case i => todos.remove(i)
    }
  }
}
