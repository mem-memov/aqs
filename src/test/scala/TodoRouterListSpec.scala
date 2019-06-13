import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.StatusCodes
import org.scalatest.{Matchers, WordSpec}

class TodoRouterListSpec extends WordSpec with Matchers with ScalatestRouteTest with TodoMocks {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private val doneTodo = Todo("1", "Buy milk!", "The cat is thursty", true)
  private val pendingTodo = Todo("1", "Buy eggs!", "Run out of egss, buy a dozen", false)

  private val todos = Seq(doneTodo, pendingTodo)

  "TodoRouter" should {
    "return all the todos" in {
      val repository = new InMemoryTodoRepository(todos)
      val router = new TodoRouter(repository)

      Get("/todos") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Seq[Todo]]
        response shouldBe todos
      }
    }

    "return all done todos" in {
      val repository = new InMemoryTodoRepository(todos)
      val router = new TodoRouter(repository)

      Get("/todos/done") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Seq[Todo]]
        response shouldBe Seq(doneTodo)
      }
    }

    "return all pending todos" in {
      val repository = new InMemoryTodoRepository(todos)
      val router = new TodoRouter(repository)

      Get("/todos/pending") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Seq[Todo]]
        response shouldBe Seq(pendingTodo)
      }
    }

    "handle repository failure in todos route" in {
      val repository = new FailingRepository()
      val router = new TodoRouter(repository)

      Get("/todos") ~> router.route ~> check {
        status shouldBe StatusCodes.InternalServerError
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }

    "handle repository failure in done todos route" in {
      val repository = new FailingRepository()
      val router = new TodoRouter(repository)

      Get("/todos/done") ~> router.route ~> check {
        status shouldBe StatusCodes.InternalServerError
      }
    }

    "handle repository failure in pending todos route" in {
      val repository = new FailingRepository()
      val router = new TodoRouter(repository)

      Get("/todos/pending") ~> router.route ~> check {
        status shouldBe StatusCodes.InternalServerError
      }
    }
  }
}