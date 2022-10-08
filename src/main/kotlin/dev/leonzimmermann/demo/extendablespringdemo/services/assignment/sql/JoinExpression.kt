package dev.leonzimmermann.demo.extendablespringdemo.services.assignment.sql

import simplenlg.framework.NLGElement
import simplenlg.framework.NLGFactory

class JoinExpression(
  private val otherTable: SQLTable,
  private val fromTable: SQLTable,
  private val otherTableProperty: SQLProperty,
  private val fromTableProperty: SQLProperty,
  private val joinType: JoinType = JoinType.INNER_JOIN
) : SQLExpression() {
  enum class JoinType(val sql: String) {
    INNER_JOIN("JOIN"),
    FULL_JOIN("FULL JOIN"),
    LEFT_JOIN("LEFT JOIN"),
    RIGHT_JOIN("RIGHT JOIN")
  }

  override fun toSQLString(): String =
    "${joinType.sql} ${otherTable.toSQLString()} ON ${otherTable.toSQLString()}.${otherTableProperty.toSQLString()}=${fromTable.toSQLString()}.${fromTableProperty.toSQLString()}"

  /**
   * The Join-Statements are technical details and should not be mentioned in the stem.
   */
  override fun toStemText(nlgFactory: NLGFactory): NLGElement {
    return nlgFactory.createStringElement("")
  }
}