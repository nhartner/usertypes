package com.github.axiopisty.usertypes.hibernate.jsr354;

import com.github.axiopisty.usertypes.wrapper.entities.MonetaryAmountWrapper;
import com.github.axiopisty.usertypes.wrapper.service.MonetaryAmountWrapperService;
import org.javamoney.moneta.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * @author Elliot Huntington
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MonetaryAmountUserTypeTest {


  private final static MonetaryAmountUserType MAUT = new MonetaryAmountUserType();

  private final static CurrencyUnit USD = MonetaryCurrencies.getCurrency("USD");
  private final static CurrencyUnit CNY = MonetaryCurrencies.getCurrency("CNY");

  private final static MonetaryAmount ONE_USD = Money.of(BigDecimal.ONE, USD);
  private final static MonetaryAmount ONE_CNY = Money.of(BigDecimal.ONE, CNY);

  private final static MonetaryAmountFormat USDF = MonetaryFormats.getAmountFormat(Locale.US);
  private final static MonetaryAmountFormat CNYF = MonetaryFormats.getAmountFormat(Locale.CHINA);

  @Inject
  private MonetaryAmountWrapperService service;

  @Test
  public void testSqlTypes() {
    int[] expected = {Types.NUMERIC, Types.VARCHAR};
    int[] actual = MAUT.sqlTypes();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testReturnedClass() {
    Class expected = MonetaryAmount.class;
    Class actual = MAUT.returnedClass();
    assertEquals(expected, actual);
  }

  @Test
  public void testEquals() {
    boolean actual = MAUT.equals(ONE_USD, ONE_CNY);
    assertFalse(String.format("%s == %s ? %s", USDF.format(ONE_USD), USDF.format(ONE_CNY), actual), actual);

    MonetaryAmount one = Money.parse("USD 1");
    actual = MAUT.equals(ONE_USD, one);
    assertTrue(String.format("%s == %s ? %s", USDF.format(ONE_USD), USDF.format(one), actual), actual);
  }

  @Test
  public void testHashCode() {
    int expected = ONE_USD.hashCode();
    int actual = MAUT.hashCode(ONE_USD);
    assertEquals(expected, actual);
  }

  @Test
  public void testIsMutable() {
    assertFalse(MAUT.isMutable());
  }

  @Test
  public void testDisassemble() {
    MAUT.disassemble(ONE_USD);
  }

  @Test
  public void testAssemble() {
    assertEquals(ONE_CNY, MAUT.assemble((Serializable) ONE_CNY, null));
  }

  @Test
  public void testReplace() {
    assertEquals(ONE_CNY, MAUT.replace(ONE_CNY, null, null));
  }

  @Test
  public void testSameValueDifferentCurrencyNotEqual() {
    assertNotEquals(String.format("%s should not equal %s", USDF.format(ONE_USD), CNYF.format(ONE_CNY)), ONE_USD, ONE_CNY);
  }

  @Test
  public void testPersist() {
    MonetaryAmount expected = ONE_USD;
    Long id = create(expected);
    assertPersistedAmount(id, expected);
    checkUpdate(id, null);
    checkUpdate(id, ONE_CNY);
    service.cleanDatabase();
  }

  private void checkUpdate(Long id, MonetaryAmount expected) {
    update(id, expected);
    assertPersistedAmount(id, expected);
  }

  private Long create(MonetaryAmount amount) {
    Long id = service.save(amount).getId();
    assertTrue("id should exist after saving the wrapper", id != null);
    return id;
  }

  private void update(Long id, MonetaryAmount value) {
    service.cleanCache();
    service.update(id, value);
  }

  private void assertPersistedAmount(Long id, MonetaryAmount expected) {
    service.cleanCache();
    assertEquals(expected, service.findById(id).getMonetaryAmount());
  }

  @Test
  public void testQueryByEqualMonetaryAmounts() {
    int NUMBER_OF_ITEMS = 5;
    int ITEM_INDEX = 2;
    List<MonetaryAmountWrapper> wrappers = cleanDatabaseAndInsertTestRecords(NUMBER_OF_ITEMS);
    MonetaryAmountWrapper threshold = wrappers.get(ITEM_INDEX);
    service.cleanCache();
    Long actual = service.queryByMonetaryAmount(threshold.getMonetaryAmount());
    Long expected = threshold.getId();
    assertEquals(expected, actual);
  }

  @Test
  public void testQueryByMonetaryAmountsGreaterThan() {
    int NUMBER_OF_ITEMS = 5;
    int ITEM_INDEX = 3;
    List<MonetaryAmountWrapper> wrappers = cleanDatabaseAndInsertTestRecords(NUMBER_OF_ITEMS);
    MonetaryAmount threshold = wrappers.get(ITEM_INDEX).getMonetaryAmount();
    service.cleanCache();
    List<MonetaryAmountWrapper> actual = service.getMonetaryAmountsGreaterThan(threshold);
    int expected = NUMBER_OF_ITEMS - ITEM_INDEX;
    assertEquals("MonetaryAmounts should be comparable", expected, actual.size());
  }

  private List<MonetaryAmountWrapper> cleanDatabaseAndInsertTestRecords(int count) {
    service.cleanDatabase();
    List<MonetaryAmountWrapper> list = new ArrayList<>();
    for(int i = 0; i < count; ++i) {
      list.add(service.save(ONE_USD.add(Money.of(new BigDecimal("" + i), USD))));
    }
    return list;
  }

}
