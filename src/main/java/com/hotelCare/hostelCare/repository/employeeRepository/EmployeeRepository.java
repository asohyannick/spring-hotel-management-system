package com.hotelCare.hostelCare.repository.employeeRepository;
import com.hotelCare.hostelCare.entity.employee.Employee;
import com.hotelCare.hostelCare.utils.EmployeeSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Employee> findByActiveTrue();

    List<Employee> findByActiveFalse();

    List<Employee> findByDepartment(String department);

    List<Employee> findByJobTitle(String jobTitle);

    List<Employee> findByCanAccessSystemTrue();

    List<Employee> findByIsOnDutyTrue();

    List<Employee> findBySalaryType(String salaryType);

    List<Employee> findByHireDateBetween(
           LocalDate startDate,
           LocalDate endDate
    );

    default Page<Employee> search(EmployeeSearchRequest request) {
        return findAll(
                request.toSpecification(),
                request.toPageable()
        );
    }
}
