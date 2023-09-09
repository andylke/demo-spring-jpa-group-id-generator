package com.github.andylke.demo.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.jdbc.AbstractReturningWork;

public class AccountNumberGenerator implements PersistentIdentifierGenerator {

  private static final long serialVersionUID = 1L;

  @Override
  public Optimizer getOptimizer() {
    return null;
  }

  @Override
  public Object generate(SharedSessionContractImplementor session, Object object) {
    final Account account = (Account) object;

    IntegralDataTypeHolder accountNumber =
        (IntegralDataTypeHolder)
            session
                .getTransactionCoordinator()
                .createIsolationDelegate()
                .delegateWork(
                    new AbstractReturningWork<>() {
                      @Override
                      public IntegralDataTypeHolder execute(Connection connection)
                          throws SQLException {
                        return nextAccountNumber(connection, account.getCustomerNumber());
                      }
                    },
                    true);
    return accountNumber.makeValue();
  }

  protected IntegralDataTypeHolder nextAccountNumber(Connection connection, Long customerNumber)
      throws SQLException {
    IntegralDataTypeHolder nextSequence = selectForUpdate(connection, customerNumber);
    if (nextSequence != null) {
      nextSequence.increment();
      update(connection, customerNumber, nextSequence);
    } else {
      nextSequence = IdentifierGeneratorHelper.getIntegralDataTypeHolder(Long.class);
      nextSequence.initialize(1);
      insert(connection, customerNumber, nextSequence);
    }

    final IntegralDataTypeHolder accountNumber =
        IdentifierGeneratorHelper.getIntegralDataTypeHolder(Long.class);
    accountNumber.initialize(
        Long.valueOf(String.format("%d%03d", customerNumber, nextSequence.makeValue())));
    return accountNumber;
  }

  private IntegralDataTypeHolder selectForUpdate(Connection connection, Long customerNumber)
      throws SQLException {
    try (PreparedStatement ps =
        connection.prepareStatement(
            "SELECT next_sequence FROM account_seq WHERE customer_number = ?")) {
      ps.setLong(1, customerNumber);
      ResultSet result = ps.executeQuery();
      if (result.next() == false) {
        return null;
      }

      IntegralDataTypeHolder value =
          IdentifierGeneratorHelper.getIntegralDataTypeHolder(Long.class);
      value.initialize(result.getLong(1));
      return value;
    }
  }

  private void update(
      Connection connection, Long customerNumber, IntegralDataTypeHolder nextSequence)
      throws SQLException {
    try (PreparedStatement ps =
        connection.prepareStatement(
            "UPDATE account_seq SET next_sequence = ? WHERE customer_number = ?")) {
      nextSequence.bind(ps, 1);
      ps.setLong(2, customerNumber);
      ps.executeUpdate();
    }
  }

  private void insert(
      Connection connection, Long customerNumber, IntegralDataTypeHolder nextSequence)
      throws SQLException {
    try (PreparedStatement ps =
        connection.prepareStatement(
            "INSERT INTO account_seq (customer_number, next_sequence) VALUES(?, ?)")) {
      ps.setLong(1, customerNumber);
      nextSequence.bind(ps, 2);
      ps.executeUpdate();
    }
  }
}
