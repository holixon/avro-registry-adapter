package io.holixon.avro.adapter.registry.reflection

import bankaccount.event.BankAccountCreated
import bankaccount.event.MoneyDeposited
import bankaccount.event.MoneyWithdrawn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent
import test.fixture.SampleEventWithAdditionalFieldWithDefault
import upcaster.itest.DummyEvent

internal class ClassGraphReflectionTest {

  @Test
  fun `empty for package without classes`() {
    assertThat(ClassGraphReflection.findSpecificRecordBaseClasses("xxx.xxx")).isEmpty()
  }

  @Test
  fun `find sample event in test_fixture`() {
    val result = ClassGraphReflection.findSpecificRecordBaseClasses("test.fixture")

    assertThat(result).containsExactlyInAnyOrder(
      SampleEvent::class,
      SampleEventWithAdditionalFieldWithDefault::class
    )
  }

  @Test
  fun `find all bankaccount and upcaster`() {
    assertThat(ClassGraphReflection.findSpecificRecordBaseClasses(
      "upcaster",
      "bankaccount.event"
    )).containsExactlyInAnyOrder(
      DummyEvent::class,
      BankAccountCreated::class,
      MoneyDeposited::class,
      MoneyWithdrawn::class
    )
  }

  @Test
  fun `find all`() {
    assertThat(ClassGraphReflection.findSpecificRecordBaseClasses()).containsExactlyInAnyOrder(
      DummyEvent::class,
      BankAccountCreated::class,
      MoneyDeposited::class,
      MoneyWithdrawn::class,
      SampleEvent::class,
      SampleEventWithAdditionalFieldWithDefault::class,
    )
  }
}
