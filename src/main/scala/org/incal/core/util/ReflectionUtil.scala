package org.incal.core.util

import java.lang.reflect.InvocationTargetException
import java.{lang => jl}

import scala.collection.Traversable
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.reflect.runtime.{universe => ru}

/**
  * @author Peter Banda
  * @since 2018
  */
object ReflectionUtil {

  private val defaultMirror = newMirror(getClass.getClassLoader)

  def newMirror(cl: ClassLoader): Mirror = ru.runtimeMirror(cl)

  def currentThreadClassLoader: ClassLoader = Thread.currentThread().getContextClassLoader()

  def getMethodNames[T](implicit tag: ClassTag[T]): Traversable[String] =
    tag.runtimeClass.getMethods.map(_.getName)

  def getCaseClassMemberAndTypeNames[T: TypeTag]: Traversable[(String, String)] =
    getCaseClassMemberAndTypeNames(typeOf[T])

  def getCaseClassMemberAndTypeNames(className: String): Traversable[(String, String)] = {
    val runtimeType = classNameToRuntimeType(className)
    getCaseClassMemberAndTypeNames(runtimeType)
  }

  def getCaseClassMemberNamesAndTypes(
    runType: ru.Type
  ): Traversable[(String, ru.Type)] =
    runType.decls.sorted.collect {
      case m: MethodSymbol if m.isCaseAccessor => (shortName(m), m.returnType)
    }

  def getCaseClassMemberMethods[T: TypeTag]: Traversable[MethodSymbol] =
    typeOf[T].members.collect {
      case m: MethodSymbol if m.isCaseAccessor => m
    }

  def getCaseClassMemberNamesAndValues[T: TypeTag : ClassTag](
    instance: T,
    mirror: Mirror = defaultMirror
  ): Traversable[(String, Any)] = {
    val members = getCaseClassMemberMethods[T]
    getFieldNamesAndValues(instance, members, mirror)
  }

  def getFieldNamesAndValues[T: ClassTag](
    instance: T,
    members: Traversable[MethodSymbol],
    mirror: Mirror = defaultMirror
  ): Traversable[(String, Any)] = {
    val instanceMirror = mirror.reflect(instance)

    members.map { member =>
      val fieldMirror = instanceMirror.reflectField(member.asTerm)
      (member.name.toString, fieldMirror.get)
    }
  }

  private def getCaseClassMemberAndTypeNames(runType: ru.Type): Traversable[(String, String)] =
    getCaseClassMemberNamesAndTypes(runType).map { case (name, ruType) =>
      (name, ruType.typeSymbol.asClass.fullName)
    }

  def isCaseClass(runType: ru.Type): Boolean =
    runType.members.exists( m => m.isMethod && m.asMethod.isCaseAccessor )

  def shortName(symbol: Symbol): String = {
    val paramFullName = symbol.fullName
    paramFullName.substring(paramFullName.lastIndexOf('.') + 1, paramFullName.length)
  }

  def classMirror(
    classSymbol: ClassSymbol,
    mirror: Mirror = defaultMirror
  ): ClassMirror =
    mirror.reflectClass(classSymbol)

  def classNameToRuntimeType(
    name: String,
    mirror: Mirror = defaultMirror
  ): ru.Type = {
    val sym = mirror.staticClass(name)
    sym.selfType
  }

  def typeToClass(
    typ: ru.Type,
    mirror: Mirror = defaultMirror
  ): Class[_] =
    mirror.runtimeClass(typ.typeSymbol.asClass)

  def staticInstance(
    name: String,
    mirror: Mirror = defaultMirror
  ): Any = {
    val module = mirror.staticModule(name)
    mirror.reflectModule(module).instance
  }

  def enumValueNames(typ: ru.Type): Traversable[String] =
    typ match {
      case TypeRef(enumType, _, _) => {
        val values = enumType.members.filter(sym => !sym.isMethod && sym.typeSignature.baseType(typ.typeSymbol) =:= typ)
        values.map(_.fullName.split('.').last)
      }
    }

  def enum(
    typ: ru.Type,
    mirror: Mirror = defaultMirror
  ): Enumeration =
    typ match {
      case TypeRef(enumType, _, _) =>
        mirror.reflectModule(enumType.termSymbol.asModule).instance.asInstanceOf[Enumeration]
    }

  def javaEnumOrdinalValues[E <: Enum[E]](clazz: Class[E]): Map[Int, E] = {
    val enumValues = clazz.getEnumConstants()
    enumValues.map( value => (value.ordinal, value)).toMap
  }

  def construct[T](typ: Type, values: Seq[Any]): T =
    construct(typeToClass(typ).asInstanceOf[Class[T]], values)

  def construct[T](clazz: Class[T], values: Seq[Any]): T = {
    val boxedValues = values.map(box)

    def tryConstruct(index: Int): Option[T] = {
      try {
        val constructor = clazz.getConstructors()(index)
        val instance = constructor.newInstance(boxedValues: _*).asInstanceOf[T]
        Some(instance)
      } catch {
        case e: InstantiationException => None
        case e: IllegalAccessException => None
        case e: IllegalArgumentException => None
        case e: InvocationTargetException => None
      }
    }

    val num = clazz.getConstructors.length
    var instance: Option[T] = None
    var index = 0
    while (instance.isEmpty && index < num) {
      instance = tryConstruct(index)
      index += 1
    }

    instance.getOrElse(throwNoConstructorException(clazz, values))
  }

  private def throwNoConstructorException(clazz: Class[_], values: Seq[Any]) =
    throw new IllegalArgumentException(s"No suitable constructor could be found for the class ${clazz.getName} matching given params ${values.mkString(",")}.")

  def construct2[T](clazz: Class[T], values: Seq[Any]): T =
    try {
      val constructor = clazz.getDeclaredConstructor(values.map(_.getClass): _*)
      constructor.newInstance(values.map(box): _*)
    } catch {
      case e: NoSuchElementException => throwNoConstructorException(clazz, values)
      case e: SecurityException => throwNoConstructorException(clazz, values)
    }

  private def box(value: Any): AnyRef =
    value match {
      case x: AnyRef => x
      case x: Boolean => new jl.Boolean(x)
      case x: Double => new jl.Double(x)
      case x: Float => new jl.Float(x)
      case x: Short => new jl.Short(x)
      case x: Byte => new jl.Byte(x)
      case x: Int => new jl.Integer(x)
      case x: Long => new jl.Long(x)
      case _ => throw new IllegalArgumentException(s"Don't know how to box $value of type ${value.getClass.getName}.")
    }
}

object ReflectionTest extends App {
  case class Person(name: String, age: Int, birthPlace: Option[String])

  val person = Person("John Snow", 36, Some("Winterfell"))

  val members = ReflectionUtil.getCaseClassMemberNamesAndValues(person)
  println(members.mkString("\n"))
}