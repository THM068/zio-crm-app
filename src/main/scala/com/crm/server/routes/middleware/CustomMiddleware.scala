package com.crm.server.routes.middleware

import com.crm.server.renderer.ViewRenderer
import zio.Exit
import zio.http.{Handler, HandlerAspect, Header, Middleware, Request, Response};

object CustomMiddleware {

  def hxRequest(): HandlerAspect[Any, Unit] =
    Middleware.interceptIncomingHandler {
      Handler.fromFunctionExit[Request] { request =>
       val hxRequestHeader = Header.Custom("HX-Request", "true")
        request.header(hxRequestHeader.headerType) match {
          case Some(headerValue) =>
            Exit.succeed(request -> ())
          case None =>
            Exit.fail(Response.text("Content cannot be viewed via this method")
            )
        }
      }
    }

  def hxTrigger(event: String):HandlerAspect[Any, Unit] =
    Middleware.interceptOutgoingHandler {
      Handler.fromFunction[Response] { response =>
        response.addHeader("HX-Trigger", event)
      }
    }
  def cookieBearer(): HandlerAspect[Any, Unit] =
    Middleware.interceptIncomingHandler {
      Handler.fromFunctionExit[Request] { request =>
        println("Inside cookieBearer")
        request.cookie("jwt") match {
          case Some(token) =>
            Console.println(s"Token: ${token}")
            Exit.succeed(request -> ())
          case None =>
            println("No token found")
            val content = html.login()
            Exit.fail(ViewRenderer.render(content.body))
        }

      }
    }
}
