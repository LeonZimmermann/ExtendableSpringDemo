package dev.leonzimmermann.bachelorprojekt.assignment.impl

import dev.leonzimmermann.bachelorprojekt.assignment.Assignment
import dev.leonzimmermann.bachelorprojekt.assignment.AssignmentService
import dev.leonzimmermann.bachelorprojekt.assignment.rules.NumberOfRowsValidationRule
import dev.leonzimmermann.bachelorprojekt.assignment.rules.ResultIsTheSameValidationRule
import dev.leonzimmermann.bachelorprojekt.assignment.DatabaseSchemeService
import dev.leonzimmermann.bachelorprojekt.assignment.OntologyService
import dev.leonzimmermann.bachelorprojekt.assignment.QueryService
import dev.leonzimmermann.bachelorprojekt.assignment.GenerationOptions
import dev.leonzimmermann.bachelorprojekt.assignment.SQLService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import simplenlg.framework.NLGFactory
import simplenlg.lexicon.Lexicon
import simplenlg.realiser.english.Realiser

@Service
class AssignmentServiceImpl(
  private val ontologyService: OntologyService,
  private val databaseSchemeService: DatabaseSchemeService,
  private val sqlService: SQLService,
  private val queryService: QueryService
) : AssignmentService {

  private val logger = LoggerFactory.getLogger(javaClass.name)

  private var counter = 0L
  private val listOfAssignments = mutableMapOf<Long, Assignment>()

  private val lexicon = Lexicon.getDefaultLexicon()
  private val nlgFactory = NLGFactory(lexicon)
  private val realiser = Realiser(lexicon)

  override fun generateNewAssignment(generationOptions: GenerationOptions): Assignment {
    val sqlExpression = sqlService.generateSQLExpression(
      databaseSchemeService.createDatabaseSchemeFromOntology(ontologyService.createEROntology()),
      generationOptions
    )
    val stem = realiser.realiseSentence(sqlExpression.toStemText(nlgFactory)).trim()
    val assignment = Assignment(
      stem = stem,
      sqlExpression = sqlExpression,
      validationRules = arrayOf(ResultIsTheSameValidationRule, NumberOfRowsValidationRule)
    )
    logger.debug("Generated new assignment: $assignment")
    listOfAssignments[counter++] = assignment
    return assignment
  }

  override fun validateSolution(objectId: Long, answer: String): List<String> {
    logger.debug("Validating solution for assignment with objectId $objectId")
    val assignment = listOfAssignments[objectId]
      ?: throw IllegalArgumentException("Could not find objectId $objectId")
    val solutionResult = queryService.executeQuery(assignment.sqlExpression.toSQLString())
    val usersResult = queryService.executeQuery(answer)
    return assignment.validationRules.flatMap { it.validate(solutionResult, usersResult) }
  }
}