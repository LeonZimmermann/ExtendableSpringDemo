package dev.leonzimmermann.bachelorprojekt.services.database.scheme.valueGenerators

import dev.leonzimmermann.bachelorprojekt.services.database.scheme.Datatype
import dev.leonzimmermann.bachelorprojekt.services.database.scheme.PropertyValueGenerator
import kotlin.random.Random

internal class ValueGeneratorFromStringList(private vararg val values: String):
  PropertyValueGenerator {
  override val datatype: Datatype
    get() = Datatype.STRING

  override fun generateValue(random: Random): String = values[random.nextInt(values.size)]
}