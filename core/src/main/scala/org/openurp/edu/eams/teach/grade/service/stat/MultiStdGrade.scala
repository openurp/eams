package org.openurp.edu.eams.teach.grade.service.stat

import org.beangle.commons.collection.CollectUtils
import org.beangle.commons.lang.Objects
import org.openurp.base.Semester
import org.openurp.edu.base.Adminclass
import org.openurp.edu.base.Student
import org.openurp.edu.base.Course
import org.openurp.edu.teach.grade.CourseGrade
import scala.collection.mutable.HashSet
import java.util.ArrayList
import org.beangle.commons.collection.Collections
import scala.collection.mutable.Buffer

class MultiStdGrade(var semester: Semester, grades: Map[Student, List[CourseGrade]], var ratio: java.lang.Float) {

  private var adminClass: Adminclass = _

  var courses: Buffer[Course] = CollectUtils.newArrayList[Course]

  var stdGrades: Buffer[StdGrade] = CollectUtils.newArrayList[StdGrade]

  stdGrades ++= (gradesMap.values)

  var extraGradeMap: collection.mutable.Map[String, List[CourseGrade]] = CollectUtils.newHashMap[String, List[CourseGrade]]

  var maxDisplay: java.lang.Integer = new java.lang.Integer(courses.size + maxExtra)


  val gradesMap = CollectUtils.newHashMap()

  val courseStdNumMap = CollectUtils.newHashMap()

  for ((key, value) <- grades) {
    val stdGrade = new StdGrade(key, value, null, null)
    gradesMap.put(key.id, stdGrade)
    for (grade <- value) {
      val courseStdNum = courseStdNumMap.get(grade.course).asInstanceOf[CourseStdNum]
      if (null == courseStdNum) {
        courseStdNumMap.put(grade.course, new CourseStdNum(grade.course, new java.lang.Integer(1)))
      } else {
        courseStdNum.count = (new java.lang.Integer(courseStdNum.count.intValue() + 1))
      }
    }
  }

  val courseStdNums = new ArrayList[CourseStdNum](courseStdNumMap.values)

  Collections.sort(courseStdNums)

  var maxStdCount = 0

  if (CollectUtils.isNotEmpty(courseStdNums)) {
    maxStdCount = (courseStdNums.get(0)).asInstanceOf[CourseStdNum].count
      .intValue()
  }

  for (i <- 0 until courseStdNums.size) {
    val rank = courseStdNums.get(i).asInstanceOf[CourseStdNum]
    if (new java.lang.Float(rank.count.intValue()).floatValue() /
      maxStdCount >
      ratio.floatValue()) {
      courses.add(rank.course)
    }
  }

  var maxExtra = 0

  var iter = stdGrades.iterator()
  while (iter.hasNext) {
    val stdGrade = iter.next()
    var myExtra = 0
    val extraGrades = CollectUtils.newArrayList()
    val commonCourseSet = new HashSet[Course](courses)
    var iterator = stdGrade.grades.iterator()
    while (iterator.hasNext) {
      val courseGrade = iterator.next()
      if (!commonCourseSet.contains(courseGrade.course)) {
        extraGrades.add(courseGrade)
        myExtra += 1
      }
    }
    if (myExtra > maxExtra) {
      maxExtra = myExtra
    }
    if (!extraGrades.isEmpty) {
      extraGradeMap.put(stdGrade.std.id.toString, extraGrades)
    }
  }

  def getAdminclass(): Adminclass = adminClass

  def setAdminclass(adminClass: Adminclass) {
    this.adminClass = adminClass
  }

  def sortStdGrades(cmpWhat: String, isAsc: Boolean) {
    if (null != stdGrades) {
      val cmp = new PropertyComparator(cmpWhat, isAsc)
      Collections.sort(stdGrades, cmp)
    }
  }

  def getExtraCourseNum(): Int = {
    getMaxDisplay.intValue() - getCourses.size
  }
}

class CourseStdNum(course2: Course, var count: java.lang.Integer) extends Comparable[_] {

  var course: Course = course2

  def getCount(): java.lang.Integer = count

  def setCount(count: java.lang.Integer) {
    this.count = count
  }

  def getCourse(): Course = course

  def setCourse(course: Course) {
    this.course = course
  }

  def compareTo(`object`: AnyRef): Int = {
    val myClass = `object`.asInstanceOf[CourseStdNum]
    Objects.compareBuilder.add(myClass.count, this.count)
      .toComparison()
  }
}