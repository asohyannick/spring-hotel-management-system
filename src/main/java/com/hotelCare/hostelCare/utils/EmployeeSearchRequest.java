package com.hotelCare.hostelCare.utils;
import com.hotelCare.hostelCare.entity.employee.Employee;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSearchRequest {
    @Min(0)
    @Builder.Default
    private Integer page = 0;

    @Min(1)
    @Max(200)
    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDir = "desc";

    private String keyword;
    private String department;
    private String jobTitle;

    private Boolean active;
    private Boolean canAccessSystem;
    private Boolean isOnDuty;
    private Boolean isVerified;

    private LocalDate hireDateFrom;
    private LocalDate hireDateTo;

    private Double salaryMin;
    private Double salaryMax;

    private String salaryType;

    public Pageable toPageable() {
        Sort sort = Sort.unsorted();

        if (sortBy != null && !sortBy.isBlank()) {
            Sort.Direction direction =
                    "asc".equalsIgnoreCase(sortDir)
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC;

            sort = Sort.by(direction, sortBy);
        }

        return PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                sort
        );
    }

    public Specification<Employee> toSpecification() {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase().trim() + "%";
                predicate = cb.and(predicate,
                        cb.or(
                                cb.like(cb.lower(root.get("firstName")), like),
                                cb.like(cb.lower(root.get("lastName")), like),
                                cb.like(cb.lower(root.get("email")), like),
                                cb.like(cb.lower(root.get("phoneNumber")), like),
                                cb.like(cb.lower(root.get("jobTitle")), like),
                                cb.like(cb.lower(root.get("department")), like)
                        )
                );
            }

            if (department != null && !department.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("department"), department));
            }

            if (jobTitle != null && !jobTitle.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("jobTitle"), jobTitle));
            }

            if (active != null) {
                predicate = cb.and(predicate, cb.equal(root.get("active"), active));
            }

            if (canAccessSystem != null) {
                predicate = cb.and(predicate, cb.equal(root.get("canAccessSystem"), canAccessSystem));
            }

            if (isOnDuty != null) {
                predicate = cb.and(predicate, cb.equal(root.get("isOnDuty"), isOnDuty));
            }

            if (isVerified != null) {
                predicate = cb.and(predicate, cb.equal(root.get("isVerified"), isVerified));
            }

            if (salaryType != null && !salaryType.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("salaryType"), salaryType));
            }

            if (hireDateFrom != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("hireDate"), hireDateFrom));
            }

            if (hireDateTo != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("hireDate"), hireDateTo));
            }

            if (salaryMin != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("salary"), salaryMin));
            }

            if (salaryMax != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("salary"), salaryMax));
            }

            return predicate;
        };
    }
}
