import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.util.{Failure, Success}
import scala.concurrent.Await

object Main extends App {

  val host = "0.0.0.0"
  val port = 9000

  implicit val system: ActorSystem = ActorSystem(name = "todoapi")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  val todoRepository = new InMemoryTodoRepository(Seq(
    Todo("1", "Buy eggs!", "Run out of egss, buy a dozen", false),
    Todo("2", "Buy milk!", "The cat is thursty", true)
  ))
  val router = new TodoRouter(todoRepository)
  val server = new Server(router, host, port)

  val binding = server.bind

  binding.onComplete {
    case Success(_) => println("Success!")
    case Failure(error) => println(s"Faled: " +  error.getMessage)
  }

  import scala.concurrent.duration._
  Await.result(binding, 3.seconds)
}
