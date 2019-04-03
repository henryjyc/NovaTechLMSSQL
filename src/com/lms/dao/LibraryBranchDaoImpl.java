package com.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lms.model.Branch;

public final class LibraryBranchDaoImpl implements LibraryBranchDao {
	private final PreparedStatement updateStatement;
	private final PreparedStatement deleteStatement;
	private final PreparedStatement findStatement;
	private final PreparedStatement getAllStatement;
	private final PreparedStatement createStatement;

	public LibraryBranchDaoImpl(final Connection dbConnection) throws SQLException {
		updateStatement = dbConnection.prepareStatement(
				"UPDATE `tbl_library_branch` SET `branchName` = ?, `branchAddress` = ? WHERE `branchId` = ?");
		deleteStatement = dbConnection.prepareStatement(
				"DELETE FROM `tbl_library_branch` WHERE `branchId` = ?");
		findStatement = dbConnection.prepareStatement(
				"SELECT * FROM `tbl_library_branch` WHERE `branchId` = ?");
		getAllStatement = dbConnection
				.prepareStatement("SELECT * FROM `tbl_library_branch`");
		createStatement = dbConnection.prepareStatement(
				"INSERT INTO `tbl_library_branch` (`branchName`, `branchAddress`) VALUES (?, ?)");
	}

	@Override
	public void update(final Branch branch) throws SQLException {
		synchronized (updateStatement) {
			if (branch.getName().isEmpty()) {
				updateStatement.setNull(1, Types.VARCHAR);
			} else {
				updateStatement.setString(1, branch.getName());
			}
			if (branch.getAddress().isEmpty()) {
				updateStatement.setNull(2, Types.VARCHAR);
			} else {
				updateStatement.setString(2, branch.getAddress());
			}
			updateStatement.setInt(3, branch.getId());
			updateStatement.executeUpdate();
		}
	}

	@Override
	public void delete(final Branch branch) throws SQLException {
		synchronized (deleteStatement) {
			deleteStatement.setInt(1, branch.getId());
			deleteStatement.executeUpdate();
		}
	}

	@Override
	public Branch get(final int id) throws SQLException {
		synchronized (findStatement) {
			findStatement.setInt(1, id);
			try (ResultSet result = findStatement.executeQuery()) {
				Branch retval = null;
				while (result.next()) {
					if (retval != null) {
						throw new IllegalStateException("Multiple results for key");
					} else {
						retval = new Branch(id,
								Optional.ofNullable(result.getString("branchName"))
										.orElse(""),
								Optional.ofNullable(result.getString("branchAddress"))
										.orElse(""));
					}
				}
				return retval;
			}
		}
	}

	@Override
	public List<Branch> getAll() throws SQLException {
		final List<Branch> retval = new ArrayList<>();
		synchronized (getAllStatement) {
			try (final ResultSet result = getAllStatement.executeQuery()) {
				while (result.next()) {
					retval.add(new Branch(result.getInt("branchId"),
							Optional.ofNullable(result.getString("branchName"))
									.orElse(""),
							Optional.ofNullable(result.getString("branchAddress"))
									.orElse("")));
				}
				return retval;
			}
		}
	}

	@Override
	public Branch create(final String branchName, final String branchAddress)
			throws SQLException {
		synchronized (createStatement) {
			if (branchName.isEmpty()) {
				createStatement.setNull(1, Types.VARCHAR);
			} else {
				createStatement.setString(1, branchName);
			}
			if (branchAddress.isEmpty()) {
				createStatement.setNull(2, Types.VARCHAR);
			} else {
				createStatement.setString(2, branchAddress);
			}
			createStatement.executeUpdate();
			try (final ResultSet result = createStatement.getGeneratedKeys()) {
				result.next();
				return new Branch(result.getInt("branchId"), branchName, branchAddress);
			}
		}
	}
}
