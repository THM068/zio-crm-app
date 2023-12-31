package com.crm.services

import com.crm.model.{Active, Contact, ContactDTO, ContactDb, Inactive}
import zio.ZIO

trait ContactService {

  def contacts(): List[ContactDTO]
  def activatefor(ids: List[String]): List[ContactDTO]
  def deActivatefor(ids: List[String]): List[ContactDTO]

  def deleteContact(id: String): Unit

  def getContact(id: String): Option[ContactDTO]

  def updateContact(contact: Contact): Contact
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

  override def deleteContact(id: String): Unit = ContactDb.contacts.remove(id)

  override def getContact(id: String): Option[ContactDTO] =
    ContactDb.contacts.get(id).map(_.toContactDTO)

  override def updateContact(contact: Contact): Contact = {
    ContactDb.contacts(contact.id) = contact
    contact
  }
}
