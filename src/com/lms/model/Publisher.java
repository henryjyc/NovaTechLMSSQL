package com.lms.model;

import java.util.Objects;

/**
 * A publisher of books.
 *
 * @author Salem Ozaki
 * @author Jonathan Lovelace
 */
public final class Publisher {
	/**
	 * The ID number identifying this publisher in the database.
	 */
	private final int id;
	/**
	 * The name of the publisher.
	 */
	private String name;
	/**
	 * The address of the publisher.
	 */
	private String address;
	/**
	 * The publisher's phone number.
	 */
	private String phone;

	/**
	 * To construct a publisher object, the caller must at least supply its ID
	 * number and name.
	 *
	 * @param id   the ID number identifying this publisher in the database
	 * @param name the publisher's name
	 */
	public Publisher(final int id, final String name) {
		this(id, name, "", "");
	}

	/**
	 * To fully construct a publisher object, callers must supply its ID number,
	 * name, address, and phone number.
	 *
	 * @param id      the ID number identifying this publisher in the database
	 * @param name    the publisher's name
	 * @param address the publisher's address
	 * @param phone   the publisher's phone number
	 */
	public Publisher(final int id, final String name, final String address,
			final String phone) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.phone = phone;
	}

	/**
	 * Get the publisher's name, which will not be null.
	 * @return the publisher's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Change the publisher's name.
	 * @param name the publisher's new name, which must not be null.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the publisher's address, which will not be null.
	 * @return the publisher's address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Change the publisher's address.
	 * @param address the publisher's new address, which must not be null.
	 */
	public void setAddress(final String address) {
		this.address = address;
	}

	/**
	 * Get the publisher's phone number as a non-null string.
	 * @return the publisher's phone number.
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Change the publisher's phone number.
	 * @param phone the publisher's new phone number, which must not be null.
	 */
	public void setPhone(final String phone) {
		this.phone = phone;
	}

	/**
	 * Get the ID number that identifies this publisher in the database.
	 * @return the publisher ID number.
	 */
	public int getId() {
		return id;
	}

	/**
	 * We use only the ID for this object's hash-code.
	 */
	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * An object is equal to this one iff it is a Publisher with equal ID, name,
	 * address, and phone number.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Publisher) {
			return id == ((Publisher) obj).getId()
					&& Objects.equals(name, ((Publisher) obj).getName())
					&& Objects.equals(address, ((Publisher) obj).getAddress())
					&& Objects.equals(phone, ((Publisher) obj).getPhone());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Publisher: " + name + "(" + id + ") at " + address + " with phone: " + phone;
	}
}
