package ru.artemlychko.rest.service.impl;

import ru.artemlychko.rest.model.Department;
import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.repository.DepartmentRepository;
import ru.artemlychko.rest.repository.impl.DepartmentRepositoryImpl;
import ru.artemlychko.rest.service.DepartmentService;
import ru.artemlychko.rest.servlet.dto.DepartmentInDto;
import ru.artemlychko.rest.servlet.dto.DepartmentOutDto;
import ru.artemlychko.rest.servlet.dto.DepartmentUpdateDto;
import ru.artemlychko.rest.servlet.mapper.DepartmentDtoMapper;
import ru.artemlychko.rest.servlet.mapper.impl.DepartmentDtoMapperImpl;

import java.util.List;

public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository = DepartmentRepositoryImpl.getInstance();
    private static final DepartmentDtoMapper departmentDtoMapper = DepartmentDtoMapperImpl.getInstance();
    private static DepartmentService instance;

    private DepartmentServiceImpl() {
    }

    public static synchronized DepartmentService getInstance() {
        if (instance == null) {
            instance = new DepartmentServiceImpl();
        }
        return instance;
    }

    private void checkExistsDepartment(Long departmentId) throws NotFoundException {
        if (!departmentRepository.existsById(departmentId)) {
            throw new NotFoundException("Department not found");
        }
    }

    @Override
    public DepartmentOutDto save(DepartmentInDto departmentInDto) {
        Department department = departmentRepository.save(departmentDtoMapper.map(departmentInDto));
        return departmentDtoMapper.map(departmentRepository.findById(department.getId()).orElse(department));
    }

    @Override
    public void update(DepartmentUpdateDto departmentUpdateDto) throws NotFoundException {
        if (departmentUpdateDto == null || departmentUpdateDto.getId() == null) {
            throw new IllegalArgumentException("Illegal argument");
        }
        checkExistsDepartment(departmentUpdateDto.getId());
        departmentRepository.update(departmentDtoMapper.map(departmentUpdateDto));
    }

    @Override
    public DepartmentOutDto findById(Long departmentId) throws NotFoundException{
        checkExistsDepartment(departmentId);
        Department department = departmentRepository.findById(departmentId).orElseThrow();
        return departmentDtoMapper.map(department);
    }

    @Override
    public List<DepartmentOutDto> findAll() {
        List<Department> departmentList = departmentRepository.findAll();
        return departmentDtoMapper.map(departmentList);
    }

    @Override
    public void delete(Long departmentId) throws NotFoundException {
        checkExistsDepartment(departmentId);
        departmentRepository.deleteById(departmentId);
    }
}
