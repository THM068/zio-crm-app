package com.crm.middleware

import zio.Exit
import zio.http.{Handler, HandlerAspect, Header, Middleware, Request, Response}

object HtmxMiddleware {
//@HxRequest
//  @@HxLocation
//  @@HxPushUrl
//  @@HxRedirect
//  @@HxRefresh
//  @@HxReplaceUrl
//  @@HxReselect
//  @@HxReswap
//  @@HxRetarget
//  @@HxTrigger
//  @@HxTriggerAfterSettle
//  @@HxTriggerAfterSwap

  //  @@HxPushUrl

  def hxPushUrl(url: String): HandlerAspect[Any, Unit] =
    Middleware.interceptOutgoingHandler {
      Handler.fromFunction[Response] { response =>
        response.addHeader("HX-Push-Url", url)
      }
    }
    
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

}
