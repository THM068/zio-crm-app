package com.crm.services

import com.crm.model.{Active, Contact, ContactDTO, ContactDb, Inactive}
import zio.ZIO

trait ContactService {

  def contacts(): List[ContactDTO]
  def activatefor(ids: List[String]): List[ContactDTO]
  def deActivatefor(ids: List[String]): List[ContactDTO]
}

object ContactService extends ContactService {
  override def activatefor(ids: List[String]): List[ContactDTO] = {
    for(id <- ids) {
      val contact = ContactDb.contacts(id)
      ContactDb.contacts(id) = contact.copy(status = Active)
    }
    contactDtoList
  }

  override def deActivatefor(ids: List[String]): List[ContactDTO] = {
    for(id <- ids) {
      val contact = ContactDb.contacts(id)
      ContactDb.contacts(id) = contact.copy(status = Inactive)
    }
    contactDtoList
  }

  override def contacts(): List[ContactDTO] = contactDtoList
  private def contactDtoList = ContactDb.contacts.values.map { contact =>
    contact.toContactDTO
  }.toList

}
