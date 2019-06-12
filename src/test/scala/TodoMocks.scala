import scala.concurrent.Future

trait TodoMocks {
  class FailingRepository extends TodoRepository {

    override def all = Future.failed(new Exception("Mocked exception"))
    override def done = Future.failed(new Exception("Mocked exception"))
    override def pending = Future.failed(new Exception("Mocked exception"))
  }
}