package com.phatlabs.ninja

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.phatlabs.ninja.model.Todo
import com.phatlabs.ninja.repository.InMemoryTodoRepository

import scala.concurrent.Await
import scala.util.{Failure, Success}

object Main extends App {

  val host = "0.0.0.0"
  val port = 9000

  implicit val system: ActorSystem = ActorSystem(name = "todoapi")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  val todoRepository = new InMemoryTodoRepository(
    Seq(
      Todo("1", "Do thing 1", "Didn't do thing 1 yet, so do it", done = false),
      Todo("2", "Do thing 2", "Thing 2 needs to be done.", done = true)
    ))
  val router = new TodoRouter(todoRepository)
  val server = new Server(router, host, port)

  val binding = server.bind()
  binding.onComplete {
    case Success(_) => println("Started server...")
    case Failure(error) =>
      println(s"Failed to start server: ${error.getMessage}")
  }

  import scala.concurrent.duration._
  Await.result(binding, 3.seconds)

}
