package org.openurp.edu.eams.teach.program.major.dao

import org.openurp.edu.base.code.CourseType



trait MajorCourseGroupDao {

  def getCourseType(planId: java.lang.Long, courseId: java.lang.Long): CourseType
}
