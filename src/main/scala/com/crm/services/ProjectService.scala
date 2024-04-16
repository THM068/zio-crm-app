package com.crm.services

import com.crm.model.Project

object ProjectService {

  private val projects: Map[Int, Project] = List(
    new Project(1, "CodeMorph"),
    new Project(2, "IntelliBot"),
    new Project(3, "SynthoGuard")
  ).map(p => p.id -> p).toMap

  def getProjects:List[Project] = projects.values.toList

  def getProjectIds = projects.keys.toSet
}
