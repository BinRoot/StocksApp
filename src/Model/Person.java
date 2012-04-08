package Model;

public class Person {
	String name;
	int networth;
	int credits;
	
	public Person(String name, int networth) {
		this.name = name;
		this.networth = networth;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNetWorth() {
		return networth;
	}
	public void setNetWorth(int networth) {
		this.networth = networth;
	}
	
	public void setCredits(int credits) {
		this.credits = credits;
	}
	public int getCredits() {
		return credits;
	}
	
	
}
