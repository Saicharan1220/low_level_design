import javax.management.InvalidApplicationException;
import java.security.InvalidParameterException;
import java.util.Optional;
import java.util.UUID;

public class Employee {
    private final String employeeId;

    private final String employeeName;

    private final String address;

    private final String contactNumber;

    private final String emailId;

    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmailId() {
        return emailId;
    }



    private Employee(EmployeeBuilder employeeBuilder){
        this.employeeId=employeeBuilder.employeeId;
        this.employeeName=employeeBuilder.employeeName;
        this.address = employeeBuilder.address;
        this.contactNumber = employeeBuilder.contactNumber;
        this.emailId = employeeBuilder.emailId;

    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", address='" + address + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", emailId='" + emailId + '\'' +
                '}';
    }

    public static class EmployeeBuilder{
        private final String employeeId;
        private final String employeeName;

        private String address;

        private String contactNumber;

        private String emailId;


        public EmployeeBuilder( String employeeName){
            this.employeeId= UUID.randomUUID().toString();
            this.employeeName=employeeName;
        }

        public EmployeeBuilder setAddress(String address){
            this.address=address;
            return this;
        }

        public EmployeeBuilder setMobileNumber(String mobileNumber){
            this.contactNumber=mobileNumber;
            return this;
        }
        public EmployeeBuilder setEmail(String email){
            this.emailId=email;
            return this;
        }

        public Employee build() throws InvalidApplicationException {

            if (null != this.contactNumber && contactNumber.trim().length() != 10) {
                throw new IllegalArgumentException("contact number is not valid");
            }
            if (null != this.emailId && !emailId.trim().endsWith("@gmail.com")) {
                throw new IllegalArgumentException("Email Id is not valid");
            }

            return new Employee(this);
        }


    }

}

