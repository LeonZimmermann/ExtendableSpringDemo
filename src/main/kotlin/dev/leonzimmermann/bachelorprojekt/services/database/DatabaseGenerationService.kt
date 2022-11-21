package dev.leonzimmermann.bachelorprojekt.services.database

import dev.leonzimmermann.bachelorprojekt.services.database.scheme.DatabaseScheme

interface DatabaseGenerationService {
    fun getDatabaseGenerationQueriesForScheme(databaseScheme: DatabaseScheme): Array<String>
}
