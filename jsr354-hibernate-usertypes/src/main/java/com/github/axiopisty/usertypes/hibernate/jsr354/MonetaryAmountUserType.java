package com.github.axiopisty.usertypes.hibernate.jsr354;

import com.github.axiopisty.usertypes.hibernate.ImmutableUserType;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * <p>
 *   A hibernate {@link UserType} for {@code javax.money.MonetaryAmount} defined in
 *   <a href="https://jcp.org/en/jsr/detail?id=354">JSR-354</a>.
 * </p>
 *
 * @author Elliot Huntington
 *
 * @see UserType
 */
public class MonetaryAmountUserType extends ImmutableUserType {

  private final static int[] SQL_TYPES = {Types.NUMERIC, Types.VARCHAR};

  @Override
  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  @Override
  public Class returnedClass() {
    return MonetaryAmount.class;
  }

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor sessionImplementor, Object owner) throws HibernateException, SQLException {
    if(names.length != SQL_TYPES.length) {
      throw new HibernateException("Expected " + SQL_TYPES.length + " column names but got " + names.length);
    }
    final BigDecimal number = rs.getBigDecimal(names[0]);
    final String currencyCode = rs.getString(names[1]);

    final MonetaryAmount amount;
    if(number == null ^ currencyCode == null) {
      throw new HibernateException("both the value and the currency must be set, or both must be null");
    } else if (number == null) {
      amount = null;
    } else {
      amount = Money.of(number, MonetaryCurrencies.getCurrency(currencyCode));
    }
    return amount;
  }

  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
    final BigDecimal amount;
    final String currencyCode;
    if(value == null) {
      amount = null;
      currencyCode = null;
    } else {
      final MonetaryAmount monetaryAmount = (MonetaryAmount)value;
      amount = monetaryAmount.getNumber().numberValue(BigDecimal.class);
      currencyCode = monetaryAmount.getCurrency().getCurrencyCode();
    }
    st.setBigDecimal(index, amount);
    st.setString(index + 1, currencyCode);
  }
}
