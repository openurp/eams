package org.openurp.edu.eams.teach.election.model.exception




@SerialVersionUID(-2937891371653678238L)
class ElectLessonLimitCountException extends Exception() {

  def this(message: String, cause: Throwable) {
    super(message, cause)
  }

  def this(message: String) {
    super(message)
  }

  def this(cause: Throwable) {
    super(cause)
  }
}
