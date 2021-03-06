package org.openurp.edu.eams.teach.lesson.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Comparator
import java.util.Date
import java.util.GregorianCalendar
import java.util.Vector
import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.commons.text.i18n.TextResource
import org.openurp.base.Semester
import org.openurp.base.model.SemesterBean
import org.openurp.edu.eams.base.util.WeekUnit
import org.beangle.commons.lang.time.YearWeekTime
import org.openurp.edu.eams.date.EamsDateUtil
import org.openurp.edu.eams.number.NumberSequence
import org.openurp.edu.teach.schedule.CourseActivity
import org.openurp.edu.teach.lesson.Lesson
import org.openurp.edu.eams.weekstate.SemesterWeekTimeBuilder
import org.openurp.edu.eams.weekstate.WeekStates
import scala.collection.mutable.Buffer
import scala.collection.mutable.ListBuffer
import org.beangle.commons.lang.time.WeekDays
import org.openurp.base.SemesterWeekTime
import org.beangle.commons.lang.time.WeekState
import org.openurp.base.CircleTime.CircleWeekTypes
import org.beangle.commons.lang.time.HourMinute
object YearWeekTimeUtil {

  def digests(occupyStr: String,
    from: Int,
    startWeek: Int,
    endWeek: Int): Buffer[WeekUnit] = {
    if (null == occupyStr || occupyStr.indexOf('1') == -1) {
      return null
    }
    var weekOccupyStr = occupyStr
    val occupyWeeks = new ListBuffer[WeekUnit]
    val initLength = weekOccupyStr.length
    val weekOccupy = new StringBuffer()
    if (from > 1) {
      val before = weekOccupyStr.substring(0, from - 1)
      weekOccupyStr = weekOccupyStr + before
    }
    var repeat = from + startWeek - 2
    if (repeat < 0) {
      repeat = 0
    }
    weekOccupy.append(Strings.repeat("0", repeat))
    weekOccupy.append(weekOccupyStr.substring(repeat, from + endWeek - 1))
    weekOccupy.append(Strings.repeat("0", initLength - weekOccupy.length))
    weekOccupy.append("000")
    if (weekOccupy.indexOf("1") == -1) {
      return occupyWeeks
    }
    var start = 0
    while ('1' != weekOccupy.charAt(start)) {
      start += 1
    }
    var i = start + 1
    while (i < weekOccupy.length) {
      val post = weekOccupy.charAt(start + 1)
      if (post == '0') {
        start = digestOdd(occupyWeeks, weekOccupy, from, start)
      }
      if (post == '1') {
        start = digestContinue(occupyWeeks, weekOccupy, from, start)
      }
      while (start < weekOccupy.length && '1' != weekOccupy.charAt(start)) {
        start += 1
      }
      i = start
    }
    occupyWeeks
  }

  private def digestOdd(occupyWeeks: Buffer[WeekUnit],
    weekOccupy: StringBuffer,
    from: Int,
    start: Int): Int = {
    var cycle = 0
    cycle = if ((start - from + 2) % 2 == 0) 3 else 2
    var i = start + 2
    while (i < weekOccupy.length) {
      if (weekOccupy.charAt(i) == '1') {
        if (weekOccupy.charAt(i + 1) == '1') {
          occupyWeeks += new WeekUnit(cycle, start - from + 2, i - 2 - from + 2)
          return i
        }
      } else {
        if (i - 2 == start) cycle = 1
        occupyWeeks += new WeekUnit(cycle, start - from + 2, i - 2 - from + 2)
        return i + 1
      }
      i += 2
    }
    i
  }

  private def digestContinue(occupyWeeks: Buffer[WeekUnit],
    weekOccupy: StringBuffer,
    from: Int,
    start: Int): Int = {
    val cycle = 1
    var i = start + 2
    while (i < weekOccupy.length) {
      if (weekOccupy.charAt(i) == '1') {
        if (weekOccupy.charAt(i + 1) != '1') {
          occupyWeeks += new WeekUnit(cycle, start - from + 2, i - from + 2)
          return i + 2
        }
      } else {
        occupyWeeks += new WeekUnit(cycle, start - from + 2, i - 1 - from + 2)
        return i + 1
      }
      i += 2
    }
    i
  }

  def digest(weekOccupyStr: String,
    from: Int,
    startWeek: Int,
    endWeek: Int,
    resourses: TextResource,
    format: String): String = {
    val weekUnitVector = YearWeekTimeUtil.digests(weekOccupyStr, from, startWeek, endWeek)
    var needI18N = false
    val weekRegular = Array("", "", "单", "双", "")
    val weekRegularKeys = Array("", "week.continuely", "week.odd", "week.even", "week.random")
    if (null != resourses) needI18N = true
    if (null != weekUnitVector && !weekUnitVector.isEmpty) {
      val weekUnits = new StringBuffer()
      for (weekUnit <- weekUnitVector) {
        if (weekUnit.start == weekUnit.end) {
          weekUnits.append(format.charAt(0)).append(weekUnit.start)
            .append(format.charAt(2))
        } else {
          if (needI18N) {
            if (null == weekRegular(weekUnit.cycle)) {
              weekRegular(weekUnit.cycle) = resourses(weekRegularKeys(weekUnit.cycle)).orNull
            }
          }
          weekUnits.append(weekRegular(weekUnit.cycle))
          weekUnits.append(format.charAt(0)).append(weekUnit.start)
            .append(format.charAt(1))
            .append(weekUnit.end)
            .append(format.charAt(2))
        }
        weekUnits.append(format.charAt(3))
      }
      if (weekUnits.lastIndexOf(format.charAt(3) + "") == weekUnits.length - 1) {
        weekUnits.substring(0, weekUnits.length - 1)
      } else {
        weekUnits.toString
      }
    } else {
      ""
    }
  }

  def digest(weekOccupyStr: String,
    from: Int,
    startWeek: Int,
    endWeek: Int,
    resourse: TextResource): String = {
    digest(weekOccupyStr, from, startWeek, endWeek, resourse, "[-] ")
  }

  //  def convertToYearWeekTimes(semester: Semester, courseTime: CourseTime*): Array[YearWeekTime] = {
  //    if (org.beangle.commons.lang.Arrays.isEmpty(courseTimes) ||
  //      semester == null) {
  //      return Array.ofDim[YearWeekTime](0)
  //    }
  //    val year = SemesterUtil.startYear(semester)
  //    val LastDay = year + "-12-31"
  //    val gregorianCalendar = new GregorianCalendar()
  //    gregorianCalendar.setTime(java.sql.Date.valueOf(LastDay))
  //    var endAtSat = false
  //    if (gregorianCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
  //      endAtSat = true
  //    }
  //    val unitList = Collections.newBuffer[YearWeekTime]
  //    for (courseTime <- courseTimes) {
  //      val weekState = courseTime.weekState
  //      val sb = new StringBuffer(Strings.repeat("0", semester.startWeek(WeekDays.Sun) - 1))
  //      sb.append(Strings.substring(weekState, 1, semester.weeks + 1))
  //        .append(Strings.repeat("0", ExamYearWeekTimeUtil.OVERALLWEEKS * 2 - sb.length))
  //      if (!endAtSat) {
  //        sb.insert(ExamYearWeekTimeUtil.OVERALLWEEKS, "0")
  //      }
  //      if (sb.substring(0, ExamYearWeekTimeUtil.OVERALLWEEKS).indexOf("1") !=
  //        -1) {
  //        val unit = new YearWeekTime()
  //        unit.year = year
  //        unit.day = courseTime.day
  //        unit.end = courseTime.end
  //        unit.begin = courseTime.start
  //        unit.state = sb.substring(0, ExamYearWeekTimeUtil.OVERALLWEEKS)
  //        unitList += unit
  //      }
  //      if (sb.substring(ExamYearWeekTimeUtil.OVERALLWEEKS, 2 * ExamYearWeekTimeUtil.OVERALLWEEKS)
  //        .indexOf("1") !=
  //        -1) {
  //        val unit = new YearWeekTime()
  //        unit.year = (year + 1)
  //        unit.weekday = courseTime.day
  //        unit.begin = courseTime.start
  //        unit.endTime = courseTime.end
  //        unit.newWeekState(sb.substring(ExamYearWeekTimeUtil.OVERALLWEEKS, 2 * ExamYearWeekTimeUtil.OVERALLWEEKS))
  //        unitList.add(unit)
  //      }
  //    }
  //    unitList.toArray(Array.ofDim[YearWeekTime](unitList.size))
  //  }
  //
  //  def convertToYearWeekTimes(lesson: Lesson, courseTimes: CourseTime*): Array[YearWeekTime] = {
  //    convertToYearWeekTimes(lesson.semester, courseTimes)
  //  }

  def buildYearWeekTimes(from: Int,
    startWeek: Int,
    endWeek: Int,
    cycle: Int): SemesterWeekTime = {
    val sb = new StringBuffer(Strings.repeat("0", from + startWeek - 2))
    var i = startWeek
    while (i <= endWeek) {
      if (isAccording(i, cycle)) {
        sb.append('1')
      } else {
        sb.append('0')
      }
      i += 1
    }
    sb.append(Strings.repeat("0", WeekStates.OVERALLWEEKS - sb.length))
    val unit = new SemesterWeekTime()
    unit.state = WeekState(sb.toString.reverse)
    unit
  }

  private def isAccording(num: Int, cycle: Int): Boolean = {
    if (cycle == CircleWeekTypes.Even) num % 2 == 0 else if (cycle == CircleWeekTypes.Odd) {
      num % 2 == 1
    } else if (cycle == CircleWeekTypes.Continuely) {
      true
    } else {
      false
    }
  }

  def buildFirstLessonDay(lesson: Lesson): Date = {
    val activities = Collections.newBuffer[CourseActivity]
    activities ++= (lesson.schedule.activities)
    if (activities.size > 1) {
      activities.sortWith { (activity1, activity2) =>
        val time1 = activity1.time
        val time2 = activity2.time
        if (time1.state.toString.lastIndexOf("1") == time2.state.toString.lastIndexOf("1")) {
          (time2.day.index - time1.day.index) < 0
        } else {
          if (time1.state.toString.lastIndexOf("1") < time2.state.toString.lastIndexOf("1")) true else false
        }
      }
    } else if (activities.isEmpty) {
      return null
    }
    val activity = activities(0)
    val unit = activity.time
    val calendar = new GregorianCalendar()
    calendar.set(Calendar.YEAR, unit.year)
    calendar.set(Calendar.WEEK_OF_YEAR, (unit.state.first + 1))
    calendar.set(Calendar.DAY_OF_WEEK, unit.day.index)
    return calendar.getTime
  }

  def buildCourseTime(startAt: Date, endAt: Date, semester: Semester): YearWeekTime = {
    val f = new SimpleDateFormat("HH:mm")
    val time = new YearWeekTime()
    time.begin = getTimeNumber(f.format(startAt))
    time.end = getTimeNumber(f.format(endAt))
    time.day = EamsDateUtil.getWeekday(startAt)
    time.state = getWeekState(startAt, endAt)
    time
  }

  def getWeekOfYear(date: Date): Int = {
    val c = new GregorianCalendar()
    c.setFirstDayOfWeek(Calendar.SUNDAY)
    c.setMinimalDaysInFirstWeek(1)
    c.setTime(date)
    c.get(Calendar.WEEK_OF_YEAR)
  }

  def getWeekState(startAt: Date, endAt: Date): WeekState = {
    val semesterYear = EamsDateUtil.year(startAt)
    val gc = new GregorianCalendar()
    gc.setTime(startAt)
    val activityYear = gc.get(java.util.Calendar.YEAR)
    val w = getWeekOfYear(startAt)
    val semesterWeek = getWeekOfYear(endAt)
    val weekState = new StringBuffer()
    if (semesterYear == activityYear) {
      var i = 1
      while (i <= WeekStates.OVERALLWEEKS) {
        if (i == w) {
          weekState.append("1")
        } else {
          weekState.append("0")
        }
        i += 1
      }
      val rs = weekState.substring(semesterWeek - 1) + Strings.repeat("0", WeekStates.OVERALLWEEKS - weekState.substring(semesterWeek - 1).length)
      WeekState(rs.reverse)
    } else {
      var i = 1
      while (i <= WeekStates.OVERALLWEEKS) {
        if (i == w) {
          weekState.append("1")
        } else {
          weekState.append("0")
        }
        i += 1
      }
      val weekStateStr = Strings.repeat("0", WeekStates.OVERALLWEEKS - semesterWeek)
      val rs = weekStateStr + weekState.substring(0, WeekStates.OVERALLWEEKS - weekStateStr.length)
      WeekState(rs.reverse)
    }
  }

  def getTimeNumber(time: String): HourMinute = HourMinute(time)

  def getTimeNumber(time: String, delimter: String): HourMinute = {
    HourMinute(time.replaceAll(delimter, ":"))
  }

  //  def main(args: Array[String]) {
  //    val format = new SimpleDateFormat("yyyy-MM-dd")
  //    val sb = new SemesterBean("2012-2013", "1", new java.sql.Date(format.parse("2012-09-03").getTime),
  //      new java.sql.Date(format.parse("2012-2-1").getTime))
  //    sb.firstWeekday = 2
  //    val t = YearWeekTimeUtil.buildYearWeekTimes(1, 3, 20, 1)
  //    println(sb.startWeek)
  //    println(t.weekState)
  //    t.weekday = 1
  //    t.endTime = 1900
  //    t.startTime = 1800
  //    println(convertToYearWeekTimes(sb, t)(0))
  //    println(convertToYearWeekTimes(sb, t)(1))
  //  }
}
