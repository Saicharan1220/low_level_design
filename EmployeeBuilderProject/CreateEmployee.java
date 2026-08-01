import javax.management.InvalidApplicationException;

public class CreateEmployee {

    public static void main(String[] args) throws InvalidApplicationException {
        Employee emp1= new Employee.EmployeeBuilder("Raj")
                .setAddress("hyderabad talangana")
                .setMobileNumber("9219199210")
                .setEmail("dummy@gmail.com")
                .build();
        Employee emp2=new Employee.EmployeeBuilder("sai").build();

        System.out.println(emp1.toString());
        System.out.println(emp2.toString());
    }
}
