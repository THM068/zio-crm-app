package com.crm.server.routes.middleware

import com.crm.server.renderer.ViewRenderer
import zio.Exit
import zio.http.{Handler, HandlerAspect, Header, Middleware, Request, Response};

object CustomMiddleware {


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
