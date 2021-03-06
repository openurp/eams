package org.openurp.edu.eams.teach.election.service.helper

import java.io.StringWriter
import java.io.Writer

import org.beangle.commons.collection.Collections
import org.openurp.edu.eams.teach.election.model.ElectMailTemplate
import org.openurp.edu.teach.lesson.CourseTake
import freemarker.cache.StringTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template



object FreeMarkerHelper {

  def dynamicCompileTemplate(template: ElectMailTemplate, courseTake: CourseTake): ElectMailTemplate = {
    val attrs = Collections.newMap[Any]
    attrs.put("courseTake", courseTake)
    val title = dynamicCompile(classOf[ElectMailTemplate].getName + template.id + 
      ".title", template.getTitle, attrs)
    val content = dynamicCompile(classOf[ElectMailTemplate].getName + template.id + 
      ".content", template.getContent, attrs)
    val result = new ElectMailTemplate()
    result.setContent(content)
    result.setTitle(title)
    result
  }

  private def dynamicCompile(name: String, templateSource: String, data: Map[String, Any]): String = {
    val cfg = new Configuration()
    cfg.setNumberFormat("#")
    val loader = new StringTemplateLoader()
    if (null == data) {
      data = Collections.newMap[Any]
    }
    loader.putTemplate(name, templateSource)
    cfg.setTemplateLoader(loader)
    var template: Template = null
    try {
      template = cfg.getTemplate(name)
      val out = new StringWriter()
      template.process(data, out)
      out.toString
    } catch {
      case e: Exception => {
        e.printStackTrace()
        ""
      }
    }
  }
}
