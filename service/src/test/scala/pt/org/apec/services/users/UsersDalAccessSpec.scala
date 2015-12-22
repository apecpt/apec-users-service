package pt.org.apec.services.users

import org.scalatest._
import pt.org.apec.services.users.common._
import dal._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.IntegrationPatience

class UsersDalAccessSpec extends FlatSpec with Matchers with CleanDatabaseSpec with ScalaFutures with IntegrationPatience {
  val rui = UserRegistration("ragb", "godiva", "ruiandrebatista@gmail.com", "Rui", "Batista", "Rui Batista", "pt")
  "dal" should "create users" in {
    dal.registerUser(rui).futureValue shouldBe a[UserRegistrationSuccess]
  }

  it should "get users by username and email" in {
    dal.registerUser(rui).futureValue shouldBe a[UserRegistrationSuccess]
    dal.getUserEntityByIdentification("ragb").futureValue shouldBe defined
    dal.getUserEntityByIdentification("ruiandrebatista@gmail.com").futureValue shouldBe defined
  }

  it should "successfully activate users" in {
    val id = dal.registerUser(rui).futureValue.asInstanceOf[UserRegistrationSuccess].id
    dal.activateUser(id).futureValue shouldBe true
    dal.activateUser(id).futureValue shouldBe false

  }

  it should "successfully authenticate users by username and email" in {
    val id = dal.registerUser(rui).futureValue.asInstanceOf[UserRegistrationSuccess].id
    dal.activateUser(id).futureValue shouldBe true
    dal.authenticate("ruiandrebatista@gmail.com", "godiva").futureValue shouldBe AuthenticationSuccess(id)
    dal.authenticate("ragb", "godiva").futureValue shouldBe AuthenticationSuccess(id)

  }

  it should "Not authenticate users when credentials are wrong" in {
    dal.authenticate("ragb", "buga").futureValue shouldBe AuthenticationFailure
    val id = dal.registerUser(rui).futureValue.asInstanceOf[UserRegistrationSuccess].id
    dal.activateUser(id).futureValue shouldBe true
    dal.authenticate("ragb", "buga").futureValue shouldBe AuthenticationFailure
  }
}