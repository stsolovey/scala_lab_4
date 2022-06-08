package org.example

import org.example.Helpers.GenericObservable
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.mongodb.scala.model.{Accumulators, Aggregates, Filters}
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections.{excludeId, fields, include}
import org.mongodb.scala.model.Sorts.ascending

import java.io.File

class MongoDBFunctions {

  val uri: String = "mongodb+srv://<username>:<password>@cluster0.ldu6h.mongodb.net/?retryWrites=true&w=majority"
  val databaseName = "lab4"
  val collectionName = "presidents"
  val dirPath = "C:\\Projects\\presidents"
  val client: MongoClient = MongoClient(uri)
  val db: MongoDatabase = client.getDatabase(databaseName)
  val collection: MongoCollection[Document] = db.getCollection(collectionName)

  def writeFilesToDatabase(): Unit ={

    val listOfFiles: List[File] = getListOfFiles(dirPath)
    val listOfPaths: List[String] = listOfFiles.map(file => file.getAbsolutePath)
    val listOfStrings: List[String] = listOfPaths.map(filepath => readFileAsString(filepath))
    val listOfMaps: List[Map[String, String]] = listOfStrings.map(string => convertJsonStringToMap(string)).filter(_!=null)

    collection.drop().printResults()
    writeToDatabaseOneByOne(collection, listOfMaps)

  }
  def readFileAsString(filepath: String): String = {
    val source = scala.io.Source.fromFile(filepath)
    val str = source.mkString
    source.close()
    str
  }

  def convertJsonStringToMap(jsonStr: String): Map[String, String] = {
    implicit val formats: DefaultFormats.type = DefaultFormats
    val result = parse(jsonStr).extract[Map[String, String]]
    result
  }

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def writeToDatabaseOneByOne(col: MongoCollection[Document], maps: List[Map[String, String]]): Unit = {
    for (map<-maps){
      col.insertOne(Document(map.filter(m => m._2!=null))).results()
    }
  }

  def writeToDatabaseAllAtOnce(col: MongoCollection[Document], maps: List[Map[String, String]]): Unit = {
    val seqOfDocs: List[Document] = maps.map(
      m => {
        println(m)
        Document(m.filter(m => m._2!=null))
      })
    col.insertMany(seqOfDocs).results()
  }

  def top5OldestPresidentsOfTheUnitedStates(/*collection: MongoCollection[Document]*/): Unit = {
    collection.find(equal("country", "United States"))
      .projection(fields(include("name", "country", "Born", "Died"), excludeId()))
      .sort(ascending("Born"))
      .limit(5)
      .printResults()
    println()
  }

  def numberOfPresidentsOfTheUnitedStates(/*collection: MongoCollection[Document]*/): Unit = {
    collection.aggregate(Seq(
      Aggregates.filter(Filters.equal("country", "United States")),
      Aggregates.group("nop", Accumulators.sum("number of presidents", 1))
    )).printResults()
  }

  def closeConnection(): Unit ={
    client.close()
  }
}

