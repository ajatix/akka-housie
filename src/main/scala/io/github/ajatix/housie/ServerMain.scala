package io.github.ajatix.housie

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import io.github.ajatix.housie.service.GameService

import scala.io.StdIn

/**
  * Created by ajay on 25/03/17.
  */
object ServerMain extends App {

  val config = ConfigFactory.load()
  val httpConfig = config.getConfig("http")

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val gameService = new GameService()

  lazy val interface = httpConfig.getString("interface")
  lazy val port = httpConfig.getInt("port")

  val bindingFuture = Http().bindAndHandle(gameService.webSocketRoute, interface, port)
  println(s"Server online at ws://$interface:$port/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(x => x.unbind())
    .onComplete(_ => system.terminate())

}
