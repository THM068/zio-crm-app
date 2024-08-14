package com.crm.services

import com.crm.model.Project

object ProjectService {

  private val projects: Map[Int, Project] = List(
     Project(1, "CodeMorph"),
     Project(2, "IntelliBot"),
     Project(3, "SynthoGuard")
  ).map(p => p.id -> p).toMap

  def getProjects:List[Project] = projects.values.toList

  def getProjectIds = projects.keys.toSet
}
