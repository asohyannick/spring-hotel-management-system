package com.hotelCare.hostelCare.mappers.employeeMapper;
import com.hotelCare.hostelCare.dto.employee.EmployeeRequestDto;
import com.hotelCare.hostelCare.dto.employee.EmployeeResponseDto;
import com.hotelCare.hostelCare.entity.employee.Employee;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeResponseDto toResponseDto(Employee employee);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Employee toEntity(EmployeeRequestDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEmployeeFromDto(EmployeeRequestDto dto, @MappingTarget Employee employee);
}
