package com.github.axiopisty.usertypes.wrapper.service;

import com.github.axiopisty.usertypes.wrapper.entities.MonetaryAmountWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.money.MonetaryAmount;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Elliot Huntington
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class MonetaryAmountWrapperService {

  @PersistenceContext
  private EntityManager em;

  public Long save(MonetaryAmount value) {
    MonetaryAmountWrapper wrapper = new MonetaryAmountWrapper();
    wrapper.setMonetaryAmount(value);
    em.persist(wrapper);
    return wrapper.getId();
  }

  public void update(Long id, MonetaryAmount amount) {
    MonetaryAmountWrapper wrapper = findById(id);
    wrapper.setMonetaryAmount(amount);
  }

  public void cleanCache() {
    em.clear();
  }

  public void cleanDatabase() {
    em.createNamedQuery(MonetaryAmountWrapper.QUERIES.DELETE_ALL).executeUpdate();
  }

  public MonetaryAmountWrapper findById(Long id) {
    return em.find(MonetaryAmountWrapper.class, id);
  }

  public Long queryByMonetaryAmount(MonetaryAmount ma) {
    Query q = em.createNamedQuery(MonetaryAmountWrapper.QUERIES.FIND_BY_MONETARY_AMOUNT);
    q.setParameter("monetaryAmount", ma);
    return ((MonetaryAmountWrapper) q.getSingleResult()).getId();
  }
}
