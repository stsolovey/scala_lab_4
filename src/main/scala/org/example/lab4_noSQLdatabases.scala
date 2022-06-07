package org.example
object lab4_noSQLdatabases {
  def main(args: Array[String]) {
    val funcs: MongoDBFunctions = new MongoDBFunctions
    funcs.writeFilesToDatabase()
    funcs.numberOfPresidentsOfTheUnitedStates()
    funcs.top5OldestPresidentsOfTheUnitedStates()
    funcs.closeConnection()
  }
}



