package com.hotelCare.hostelCare.repository.employeeRepository;
import com.hotelCare.hostelCare.entity.employee.Employee;
import com.hotelCare.hostelCare.utils.EmployeeSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID>, JpaSpecificationExecutor<Employee> {
    boolean existsByEmail(String email);
    default Page<Employee> search(EmployeeSearchRequest request) {
        return findAll(
                request.toSpecification(),
                request.toPageable()
        );
    }
}
