package com.mchange.sc.v1.mappable.scalamacro;

import org.specs2.Specification;

object PackageSpec {
  val Str : String         = "Hallo!";
  val Col : java.awt.Color = java.awt.Color.blue;
  val Num : Int            = 9;

  case class TestCase(  val str : String, val col : java.awt.Color, val num : Int );

  val defaultTestCase = TestCase( Str, Col, Num );
  val defaultMap      : Map[String,Any] = Map[String,Any]( "str" -> Str, "col" -> Col, "num" -> Num );
  
}

class PackageSpec extends Specification {
  import PackageSpec._

  def is = {
    "Testing a package object containing macros for extracting values as maps."                    ^
    "defaultTestCase object should extract to the expected Map"                 ! extractsToMap    ^
    "defaultMap should construct into defaultTestCase"                          ! constructsToCase ;
  }

  def extractsToMap    = extractMap( defaultTestCase ) mustEqual defaultMap;
  def constructsToCase = constructFromMap[TestCase]( defaultMap ) mustEqual defaultTestCase;
}
