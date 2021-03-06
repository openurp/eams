package org.openurp.edu.eams.teach.lesson.dao

import java.io.Serializable
import org.beangle.commons.collection.page.Page
import org.openurp.base.Semester
import org.openurp.edu.teach.lesson.Lesson
import org.openurp.edu.eams.teach.lesson.service.LessonFilterStrategy
import org.openurp.edu.teach.plan.MajorPlan
import org.beangle.data.model.dao.EntityDao

trait LessonDao extends EntityDao {

  def getLessonsByCategory(id: Serializable, strategy: LessonFilterStrategy, semesters: Iterable[Semester]): Seq[Lesson]

  def getLessonsByCategory(id: Serializable,
    strategy: LessonFilterStrategy,
    semester: Semester,
    pageNo: Int,
    pageSize: Int): Page[Lesson]

  def updateLessonByCategory(attr: String,
    value: AnyRef,
    id: java.lang.Long,
    strategy: LessonFilterStrategy,
    semester: Semester): Int

  def updateLessonByCriteria(attr: String,
    value: AnyRef,
    task: Lesson,
    stdTypeIds: Array[Integer],
    departIds: Array[Long]): Int

  def countLesson(id: Serializable, strategy: LessonFilterStrategy, semester: Semester): Int

  def remove(lesson: Lesson): Unit

  def saveMergeResult(tasks: Array[Lesson], target: Int): Unit

  def saveGenResult(plan: MajorPlan,
    semester: Semester,
    lessons: collection.mutable.Buffer[Lesson],
    removeExists: Boolean): Unit

}
