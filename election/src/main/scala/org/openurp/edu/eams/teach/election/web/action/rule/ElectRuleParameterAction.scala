package org.openurp.edu.eams.teach.election.web.action.rule

import org.beangle.data.model.Entity
import org.beangle.ems.rule.Rule
import org.beangle.ems.rule.RuleParameter
import org.openurp.edu.eams.web.action.BaseAction



class ElectRuleParameterAction extends BaseAction {

  protected def getEntityName(): String = classOf[RuleParameter].getName

  protected def editSetting(entity: Entity[_]) {
    val rule = getEntity(classOf[Rule], getShortName + ".rule")
    val ruleParameter = entity.asInstanceOf[RuleParameter]
    ruleParameter.setRule(rule)
  }
}
