package comp1206.sushi.common;

import comp1206.sushi.common.Supplier;

import java.util.Objects;

public class Supplier extends Model {

	private String name;
	private Postcode postcode;
	private Number distance;

	public Supplier(String name, Postcode postcode) {
		this.name = name;
		this.postcode = postcode;
	}

	public Supplier(){}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Postcode getPostcode() {
		return this.postcode;
	}
	
	public void setPostcode(Postcode postcode) {
		this.postcode = postcode;
	}

	public Number getDistance() {
		return postcode.getDistance();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Supplier supplier = (Supplier) o;
		return Objects.equals(name, supplier.name) &&
				Objects.equals(postcode, supplier.postcode) &&
				Objects.equals(distance, supplier.distance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, postcode, distance);
	}
}
