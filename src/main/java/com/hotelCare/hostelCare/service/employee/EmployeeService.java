package com.hotelCare.hostelCare.service.employee;
import com.hotelCare.hostelCare.dto.employee.EmployeeRequestDto;
import com.hotelCare.hostelCare.dto.employee.EmployeeResponseDto;
import com.hotelCare.hostelCare.utils.EmployeeSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeResponseDto addEmployee(EmployeeRequestDto dto, MultipartFile profilePicture);

    List<EmployeeResponseDto> getAllEmployees();

    EmployeeResponseDto getEmployeeById(UUID employeeId);

    EmployeeResponseDto updateEmployee(UUID employeeId, EmployeeRequestDto dto, MultipartFile profilePicture);

    void deleteEmployee(UUID employeeId);

    long totalEmployees();

    Page<EmployeeResponseDto> searchEmployees(EmployeeSearchRequest request);
}
