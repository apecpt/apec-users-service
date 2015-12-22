package pt.org.apec.services.users

import org.scalatest._
import pt.org.apec.services.users.common._
import dal._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.IntegrationPatience

class UsersDalAccessSpec extends FlatSpec with Matchers with CleanDatabaseSpec with ScalaFutures with IntegrationPatience {
  "dal" should "create users" in {
    dal.createUser("ragb", "godiva", "ruiandrebatista@gmail.com").futureValue should be
  }

  it should "get users by username and email" in {
    dal.createUser("ragb", "godiva", "ruiandrebatista@gmail.com").futureValue should be
    dal.getUserEntityByIdentification("ragb").futureValue shouldBe defined
    dal.getUserEntityByIdentification("ruiandrebatista@gmail.com").futureValue shouldBe defined
  }
  it should "successfully authenticate users by username and email" in {
    val id = dal.createUser("ragb", "godiva", "ruiandrebatista@gmail.com").futureValue
    dal.authenticate("ruiandrebatista@gmail.com", "godiva").futureValue shouldBe AuthenticationSuccess(id)

    dal.authenticate("ragb", "godiva").futureValue shouldBe AuthenticationSuccess(id)
  }
}