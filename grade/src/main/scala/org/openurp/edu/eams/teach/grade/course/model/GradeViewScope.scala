package org.openurp.edu.eams.teach.grade.course.model



import javax.persistence.ManyToMany
import org.beangle.commons.collection.Collections
import org.beangle.commons.entity.metadata.Model
import org.beangle.data.model.bean.LongIdBean
import org.openurp.edu.base.Project
import org.openurp.code.edu.Education
import org.openurp.edu.base.code.StdType
import org.openurp.edu.eams.core.model.ProjectBean




@SerialVersionUID(-5774562389434565471L)

class GradeViewScope extends LongIdBean {

  
  
  var projects: Set[Project] = _

  
  
  var educations: Set[Education] = _

  
  
  var stdTypes: Set[StdType] = _

  
  var enrollYears: String = _

  
  var checkEvaluation: Boolean = false

  def addProjects(project: Project) {
    if (null == this.projects) {
      this.projects = Collections.newSet[Any]
    }
    this.projects.add(project)
  }

  def addProjects(projectIds: Array[Integer]) {
    for (i <- 0 until projectIds.length) {
      addProjects(new ProjectBean(projectIds(i)))
    }
  }

  def clearProjects() {
    if (null == this.projects) {
      this.projects = Collections.newSet[Any]
    }
    this.projects.clear()
  }

  def addEducations(education: Education) {
    if (null == this.educations) {
      this.educations = Collections.newSet[Any]
    }
    this.educations.add(education)
  }

  def addEducations(educationIds: Array[Integer]) {
    for (i <- 0 until educationIds.length) {
      addEducations(new Education(educationIds(i)))
    }
  }

  def clearEducations() {
    if (null == this.educations) {
      this.educations = Collections.newSet[Any]
    }
    this.educations.clear()
  }

  def addStdTypes(stdType: StdType) {
    if (null == this.stdTypes) {
      this.stdTypes = Collections.newSet[Any]
    }
    this.stdTypes.add(stdType)
  }

  def addStdTypes(stdTypeIds: Array[Integer]) {
    for (i <- 0 until stdTypeIds.length) {
      addStdTypes(Model.newInstance(classOf[StdType], stdTypeIds(i)))
    }
  }

  def clearStdTypes() {
    if (null == this.stdTypes) {
      this.stdTypes = Collections.newSet[Any]
    }
    this.stdTypes.clear()
  }
}
