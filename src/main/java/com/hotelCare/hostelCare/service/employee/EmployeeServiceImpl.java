package com.hotelCare.hostelCare.service.employee;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hotelCare.hostelCare.dto.employee.EmployeeRequestDto;
import com.hotelCare.hostelCare.dto.employee.EmployeeResponseDto;
import com.hotelCare.hostelCare.entity.employee.Employee;
import com.hotelCare.hostelCare.exception.BadRequestException;
import com.hotelCare.hostelCare.exception.NotFoundException;
import com.hotelCare.hostelCare.mappers.employeeMapper.EmployeeMapper;
import com.hotelCare.hostelCare.repository.employeeRepository.EmployeeRepository;
import com.hotelCare.hostelCare.service.storage.FileStorageService;
import com.hotelCare.hostelCare.utils.EmployeeSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final FileStorageService fileStorageService;
    private final Cloudinary cloudinary;

    public EmployeeResponseDto addEmployee(EmployeeRequestDto dto, MultipartFile profilePicture) {

        if (employeeRepository.existsByEmail(dto.email())) {
            throw new BadRequestException("Employee with this email already exists: " + dto.email());
        }

        Employee employee = employeeMapper.toEntity(dto);

        if (profilePicture != null && !profilePicture.isEmpty()) {
            fileStorageService.uploadProfilePicture(profilePicture);

            String cloudUrl = uploadToCloudinary(profilePicture);
            employee.setProfilePic(cloudUrl);
        }

        if (employee.getActive() == null) employee.setActive(true);
        if (employee.getCanAccessSystem() == null) employee.setCanAccessSystem(false);
        if (employee.getIsOnDuty() == null) employee.setIsOnDuty(false);
        if (employee.getIsVerified() == null) employee.setIsVerified(false);

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeById(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));
        return employeeMapper.toResponseDto(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDto updateEmployee(UUID employeeId, EmployeeRequestDto dto, MultipartFile profilePicture) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));
        if (dto.email() != null && !dto.email().equalsIgnoreCase(employee.getEmail())) {
            if (employeeRepository.existsByEmail(dto.email())) {
                throw new BadRequestException("Employee with this email already exists: " + dto.email());
            }
        }
        employeeMapper.updateEmployeeFromDto(dto, employee);
        if (profilePicture != null && !profilePicture.isEmpty()) {
            fileStorageService.uploadProfilePicture(profilePicture);
            String newCloudUrl = uploadToCloudinary(profilePicture);
            employee.setProfilePic(newCloudUrl);
        }

        Employee updated = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(updated);
    }

    @Override
    public void deleteEmployee(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));

        employeeRepository.delete(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public long totalEmployees() {
        return employeeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> searchEmployees(EmployeeSearchRequest request) {
        return employeeRepository.search(request).map(employeeMapper::toResponseDto);
    }

    private String uploadToCloudinary(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "employees",
                            "resource_type", "image"
                    )
            );

            Object secureUrl = result.get("secure_url");
            if (secureUrl == null) {
                throw new BadRequestException("Cloudinary upload failed: secure_url not returned");
            }
            return secureUrl.toString();

        } catch (Exception e) {
            throw new BadRequestException("Failed to upload employee profile picture to Cloudinary", e);
        }
    }
}

