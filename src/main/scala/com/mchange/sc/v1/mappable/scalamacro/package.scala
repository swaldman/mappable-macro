package com.mchange.sc.v1.mappable;

import scala.language.experimental.macros;
import scala.reflect.macros.Context;

/*
 * Don't forget trick for finding sample trees:
 *
 *    import scala.reflect.runtime.universe._
 *    import scala.tools.reflect.ToolBox
 *    val mirror = runtimeMirror( getClass.getClassLoader )
 *    val tb = mirror.mkToolBox()
 *    showRaw(tb.parse( """new Testy(8)"""))
 *
 * See http://docs.scala-lang.org/overviews/reflection/symbols-trees-types.html
 */ 

package object scalamacro {

  def constructFromMap[T](paramVals : Map[String,Any]) : T = macro constructFromMapImpl[T];

  def constructFromMapImpl[T : c.WeakTypeTag]( c : Context )( 
    paramVals : c.Expr[Map[String,Any]] 
  ) : c.Expr[T] = {
    import c.universe._;

    //println( "Yayyy 1" );

    val tpe         : Type               = implicitly[c.WeakTypeTag[T]].tpe;
    val ctorDecl    : Symbol             = tpe.declaration(nme.CONSTRUCTOR);
    val ctor        : MethodSymbol       = ctorDecl.asMethod;
    val ctorParamss : List[List[Symbol]] = ctor.paramss;

    val ctorParams  : List[Symbol] = ctorParamss match {
      case Nil => {
	println("Warning: Trying to populate a no-arg constructor from a Map. No data will be used.");
	Nil;
      }
      case head :: Nil => head;
      case _ => throw new UnsupportedOperationException("Cannot populate a constructor with multiple param lists from a Map.");
    }
    
    //println( "Yayyy 2" );

    def fullNameToTreeFragment( fullName : String ) : Tree = {
      val parsed = fullName.split("""\.""").toList.reverse;

      //println(s"fullNameParsed: ${parsed}");

      def build( l : List[String] ) : Tree = l match {
	case head :: Nil => Ident(newTypeName(head));
	case head :: tail => Select( build( tail ), newTermName( head ) );
	case Nil => throw new IllegalArgumentException( s"Cannot cast to empty type! ${l}" );
      }
      
      val out = build( parsed );
      //println(s"fullTypeTree: ${out}");
      //println( "raw: " + showRaw( out ) ); 
      out
    }

    val ctorParamNames = ctorParams.map( _.name.toString.trim );
    val ctorParamTypeNames = ctorParams.map( _.typeSignature.baseClasses(0).fullName.toString.trim );
    val ctorParamTypes = ctorParams.map( _.typeSignature );

    //print( ctorParamTypeNames );

    val mapExpr = reify {
      paramVals.splice
    }
    
    //println( "Yayyy 3" );

/*
    val argTreeList = ctorParamNames.zip(ctorParamTypeNames).map( 
      tup => 
      TypeApply(
	Select(
	  Apply(
	    Select(
	      mapExpr.tree, 
	      newTermName("apply")
	    ), 
	    List(
	      Literal(
		Constant(tup._1) 
	      ) 
	    ) 
	  ),
	  newTermName("asInstanceOf")
	),
	List{ 
	  val show = fullNameToTreeFragment(tup._2);
	  println( showRaw( show ) );
	  show
	}
      )
    ).toList;
*/ 

    val argTreeList = ctorParamNames.zip(ctorParamTypes).map( 
      tup => 
      TypeApply(
	Select(
	  Apply(
	    Select(
	      mapExpr.tree, 
	      newTermName("apply")
	    ), 
	    List(
	      Literal(
		Constant(tup._1) 
	      ) 
	    ) 
	  ),
	  newTermName("asInstanceOf")
	),
	List( 
	  TypeTree(tup._2)
	)
      )
    ).toList;



    //println( showRaw( argTreeList ) );

    //println( "Yayyy 4" );

    //println( argTreeList );

    //val argTreeList = ctorParamNames.map( argName => newTermName("apply") ).toList;

    c.Expr[T](
      Apply(Select(New(Ident(newTypeName(tpe.baseClasses(0).name.toString.trim))), nme.CONSTRUCTOR), argTreeList)
    )
  }

  def extractMap[T]( obj : T ) : Map[String,Any] = macro extractMapImpl[T];

  def extractMapImpl[T  : c.WeakTypeTag]( c : Context )( obj : c.Expr[T] ) : c.Expr[Map[String,Any]] = {
    import c.universe._;

    val termSeq = obj.actualType.members
      .filter( _.isTerm )
      .map( _.asTerm );

/*
   // See http://stackoverflow.com/questions/15730155/why-dont-scala-case-class-fields-reflect-as-public

    val valNamesSeq = termSeq 
      .filter( _.isVal )
      .filter( _.isPublic )
      .map( _.name.toString.trim );
*/

    val accessorNamesSeq = termSeq
      .filter( _.isAccessor )
      .filter( _.isPublic )
      .map( _.name.toString.trim );
    


    val objReified = reify {
      obj.splice 
    }

    val orTree = objReified.tree;

    val bindingsList : List[Tree] = accessorNamesSeq.map(
      valName => {
	Apply(Ident(newTermName("Pair")), List(Literal(Constant(valName)), Select(orTree, newTermName(valName))));
      }
    ).toList

    c.Expr[Map[String,Any]](
      Apply(
	Select(
	  build.Ident(rootMirror.staticModule("scala.collection.immutable.Map")),
	  newTermName("apply")
	),
	bindingsList
      )
    )
  }
}





