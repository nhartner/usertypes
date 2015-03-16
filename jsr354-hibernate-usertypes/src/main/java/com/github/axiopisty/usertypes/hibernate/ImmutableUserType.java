package com.github.axiopisty.usertypes.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;

/**
 * @author Elliot Huntington
 */
public abstract class ImmutableUserType implements CompositeUserType {

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
  public Serializable disassemble(Object value, SessionImplementor implementor) throws HibernateException {
    return (Serializable)value;
  }

  @Override
  public Object assemble(Serializable cached, SessionImplementor implementor, Object owner) throws HibernateException {
    return cached;
  }

  @Override
  public Object replace(Object original, Object target, SessionImplementor implementor, Object owner) throws HibernateException {
    return original;
  }
}
