package com.crm.server.renderer

import zio.http._

object ViewRenderer {

  def render(content: String): Response = Response(
    Response.ok.status,
    Headers(Header.ContentType(MediaType.text.html).untyped),
    Body.fromString(content),
  )
}
