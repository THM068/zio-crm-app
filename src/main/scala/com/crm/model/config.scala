package com.crm.model

import zio._
import zio.config.magnolia.deriveConfig

case class JWTConfig(secret: String)
object JWTConfig {
  val config: zio.Config[JWTConfig] = deriveConfig[JWTConfig].nested("jwt")
}

