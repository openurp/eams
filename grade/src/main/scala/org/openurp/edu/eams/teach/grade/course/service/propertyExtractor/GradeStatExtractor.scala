package org.openurp.edu.eams.teach.grade.course.service.propertyExtractor


import org.beangle.commons.collection.Collections
import org.beangle.commons.text.i18n.TextResource
import org.beangle.commons.transfer.exporter.DefaultPropertyExtractor
import org.openurp.edu.base.Teacher
import org.openurp.edu.teach.grade.model.CourseGradeState
import org.openurp.edu.teach.lesson.Lesson



class GradeStatExtractor(textResource: TextResource) extends DefaultPropertyExtractor(textResource) {

  def getPropertyValue(target: AnyRef, property: String): AnyRef = {
    if ("teachers" == property) {
      var teacherName = ""
      var teachers = Collections.newBuffer[Any]
      if (target.isInstanceOf[Lesson]) {
        val lesson = target.asInstanceOf[Lesson]
        teachers = lesson.getTeachers
      } else {
        val gradeState = target.asInstanceOf[CourseGradeState]
        teachers = gradeState.getLesson.getTeachers
      }
      if (teachers.size == 0) {
        return "未安排教师"
      }
      for (i <- 0 until teachers.size) {
        if (i > 0) {
          teacherName += ","
        }
        teacherName += teachers.get(i).getName
      }
      teacherName
    } else {
      super.getPropertyValue(target, property)
    }
  }
}
