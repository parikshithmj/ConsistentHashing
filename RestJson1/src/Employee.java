

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Employee {

	String title;
	String name;
	int salary;
	public Employee(String title,String name,int salary) {
		this.title = title;
		this.name = name;
		this.salary = salary;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Employee title=" + title + ", name=" + name + ", salary="+salary;
	}
	
//	@Override
//	public Employee hashCode(){
//		//return 37*name.hashCode()*title.hashCode();
//		
//	}

}