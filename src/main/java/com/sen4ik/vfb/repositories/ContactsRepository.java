package com.sen4ik.vfb.repositories;

import com.sen4ik.vfb.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("contactsRepository")
public interface ContactsRepository extends JpaRepository<Contact, Integer> {
    Optional<Contact> findById(Integer id);
    Optional<Contact> findByPhoneNumber(String phoneNumber);
    List<Contact> findBySelectedSendTimePacific(Double selectedSendTimePacific);
    List<Contact> findBySelectedSendTimePacificAndBibleTranslationAndSubscriptionConfirmed(Double selectedSendTimePacific, String bibleTranslation, Byte subscriptionConfirmed);
}
