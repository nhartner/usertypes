package com.github.axiopisty.usertypes.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;

/**
 * @author Elliot Huntington
 */
public abstract class ImmutableUserType implements UserType {

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    return x == y || (x != null && y != null && x.equals(y));
  }

  @Override
  public int hashCode(Object o) throws HibernateException {
    return o == null ? 0 : o.hashCode();
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable)value;
  }

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }
}
