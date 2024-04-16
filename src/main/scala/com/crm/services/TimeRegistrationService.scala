package com.crm.services

import java.time.{Duration, LocalDate}

object TimeRegistrationService {

  private var registrations: Map[ProjectDate, Duration] = Map()

  def addOrUpdateRegistration(projectId: Int, date: LocalDate, duration: Duration): Unit =
    registrations += ( ProjectDate(projectId, date) -> duration )

  def  getTotal(projectIds: Set[Int] , dates: Set[LocalDate] ): Duration =
    registrations
      .filterKeys( pd => projectIds.contains(pd.projectId) && dates.contains(pd.date) )
      .values
      .fold(Duration.ZERO)(_ plus _)
}

case class ProjectDate (projectId: Int, date: LocalDate)
