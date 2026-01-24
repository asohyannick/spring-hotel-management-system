package com.hotelCare.hostelCare.controller.employee;
import com.hotelCare.hostelCare.config.customResponseMessge.CustomResponseMessage;
import com.hotelCare.hostelCare.dto.employee.EmployeeRequestDto;
import com.hotelCare.hostelCare.dto.employee.EmployeeResponseDto;
import com.hotelCare.hostelCare.service.employee.EmployeeService;
import com.hotelCare.hostelCare.utils.EmployeeSearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/${api.version}/employee")
@Tag(name = "Employee Management Endpoints", description = "Employee management endpoints")
public class EmployeeController {
    private final EmployeeService employeeService;
    @Operation(summary = "Add employee", description = "Creates a new employee. Supports profile picture upload (Cloudinary + local).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping(value = "/add-employee", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomResponseMessage<EmployeeResponseDto>> addEmployee(
            @Parameter(
                    description = "Employee JSON data",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EmployeeRequestDto.class))
            )
            @RequestPart("data") @Valid EmployeeRequestDto dto,

            @Parameter(
                    description = "Profile picture file (jpg/png)",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
    ) {
        EmployeeResponseDto created = employeeService.addEmployee(dto, profilePicture);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CustomResponseMessage<>(
                        "Employee created successfully",
                        HttpStatus.CREATED.value(),
                        created
                )
        );
    }

    @Operation(summary = "Fetch all employees")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employees fetched successfully")
    })
    @GetMapping("/fetch-employees")
    public ResponseEntity<CustomResponseMessage<List<EmployeeResponseDto>>> getAllEmployees() {
        List<EmployeeResponseDto> employees = employeeService.getAllEmployees();

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "Employees fetched successfully",
                        HttpStatus.OK.value(),
                        employees
                )
        );
    }

    @Operation(summary = "Fetch one employee", description = "Fetch employee by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/fetch-employee/{employeeId}")
    public ResponseEntity<CustomResponseMessage<EmployeeResponseDto>> getEmployeeById(
            @Parameter(description = "Employee ID") @PathVariable UUID employeeId
    ) {
        EmployeeResponseDto employee = employeeService.getEmployeeById(employeeId);

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "Employee fetched successfully",
                        HttpStatus.OK.value(),
                        employee
                )
        );
    }

    @Operation(summary = "Update employee", description = "Updates employee data and optionally replaces profile picture.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PutMapping(value = "/update-employee/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomResponseMessage<EmployeeResponseDto>> updateEmployee(
            @Parameter(description = "Employee ID") @PathVariable UUID employeeId,

            @Parameter(
                    description = "Employee JSON data",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EmployeeRequestDto.class))
            )
            @RequestPart("data") @Valid EmployeeRequestDto dto,

            @Parameter(
                    description = "Profile picture file (jpg/png)",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
    ) {
        EmployeeResponseDto updated = employeeService.updateEmployee(employeeId, dto, profilePicture);

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "Employee updated successfully",
                        HttpStatus.OK.value(),
                        updated
                )
        );
    }

    @Operation(summary = "Delete employee", description = "Deletes employee by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/delete-employee/{employeeId}")
    public ResponseEntity<CustomResponseMessage<Object>> deleteEmployee(
            @Parameter(description = "Employee ID") @PathVariable UUID employeeId
    ) {
        employeeService.deleteEmployee(employeeId);

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "Employee deleted successfully",
                        HttpStatus.OK.value(),
                        null
                )
        );
    }

    @Operation(summary = "Total employees", description = "Returns total number of employees")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total employees fetched successfully")
    })
    @GetMapping("/total-employees")
    public ResponseEntity<CustomResponseMessage<Long>> totalEmployees() {
        long total = employeeService.totalEmployees();

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "Total employees fetched successfully",
                        HttpStatus.OK.value(),
                        total
                )
        );
    }

    @Operation(
            summary = "Search employees",
            description = "Search employees with filtering + sorting + pagination using EmployeeSearchRequest parameters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employees search results fetched successfully")
    })
    @GetMapping("/search-employee")
    public ResponseEntity<CustomResponseMessage<Page<EmployeeResponseDto>>> searchEmployees(
            @Valid @ModelAttribute EmployeeSearchRequest request
    ) {
        Page<EmployeeResponseDto> results = employeeService.searchEmployees(request);

        return ResponseEntity.ok(
                new CustomResponseMessage<>(
                        "Employees search results fetched successfully",
                        HttpStatus.OK.value(),
                        results
                )
        );
    }
}