/*
 * Distributed as part of mchange-commons-scala-macro 0.0.1
 *
 * Copyright (C) 2013 Machinery For Change, Inc.
 *
 * Author: Steve Waldman <swaldman@mchange.com>
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of EITHER:
 *
 *     1) The GNU Lesser General Public License (LGPL), version 2.1, as 
 *        published by the Free Software Foundation
 *
 * OR
 *
 *     2) The Eclipse Public License (EPL), version 1.0
 *
 * You may choose which license to accept if you wish to redistribute
 * or modify this work. You may offer derivatives of this work
 * under the license you have chosen, or you may provide the same
 * choice of license which you have been offered here.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received copies of both LGPL v2.1 and EPL v1.0
 * along with this software; see the files LICENSE-EPL and LICENSE-LGPL.
 * If not, the text of these licenses are currently available at
 *
 * LGPL v2.1: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  EPL v1.0: http://www.eclipse.org/org/documents/epl-v10.php 
 * 
 */

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
