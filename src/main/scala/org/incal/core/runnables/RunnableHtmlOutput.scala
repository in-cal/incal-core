package org.incal.core.runnables

trait RunnableHtmlOutput {

  val output = new StringBuilder

  protected def addParagraph(string: String): Unit =
    output ++= s"<p>$string</p>"

  protected def addDiv(string: String): Unit =
    output ++= s"<div>$string</div>"

  protected def bold(string: String): String =
    s"<b>$string</b>"

  protected def it(string: String): String =
    s"<i>$string</i>"
}
