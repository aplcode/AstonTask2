package ru.artemlychko.rest.service.impl;

import ru.artemlychko.rest.model.Employee;
import ru.artemlychko.rest.exception.NotFoundException;
import ru.artemlychko.rest.repository.EmployeeRepository;
import ru.artemlychko.rest.repository.impl.EmployeeRepositoryImpl;
import ru.artemlychko.rest.service.EmployeeService;
import ru.artemlychko.rest.servlet.dto.EmployeeInDto;
import ru.artemlychko.rest.servlet.dto.EmployeeOutDto;
import ru.artemlychko.rest.servlet.dto.EmployeeUpdateDto;
import ru.artemlychko.rest.servlet.mapper.EmployeeDtoMapper;
import ru.artemlychko.rest.servlet.mapper.impl.EmployeeDtoMapperImpl;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();
    private static final EmployeeDtoMapper employeeDtoMapper = EmployeeDtoMapperImpl.getInstance();
    private static EmployeeService instance;

    private EmployeeServiceImpl() {
    }

    public static synchronized EmployeeService getInstance() {
        if (instance == null) {
            instance = new EmployeeServiceImpl();
        }
        return instance;
    }

    private void checkExistEmployee(Long employeeId) throws NotFoundException {
        if (!employeeRepository.existsById(employeeId)) {
            throw new NotFoundException("Employee not found");
        }
    }

    @Override
    public EmployeeOutDto save(EmployeeInDto employeeInDto) {
        Employee employee = employeeRepository.save(employeeDtoMapper.map(employeeInDto));
        return employeeDtoMapper.map(employeeRepository.findById(employee.getId()).orElse(employee));
    }

    @Override
    public void update(EmployeeUpdateDto employeeUpdateDto) throws NotFoundException {
        if (employeeUpdateDto == null || employeeUpdateDto.getId() == null) {
            throw new IllegalArgumentException("Illegal argument");
        }
        checkExistEmployee(employeeUpdateDto.getId());
        employeeRepository.update(employeeDtoMapper.map(employeeUpdateDto));
    }

    @Override
    public EmployeeOutDto findById(Long employeeId) throws NotFoundException {
        checkExistEmployee(employeeId);
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        return employeeDtoMapper.map(employee);
    }

    @Override
    public List<EmployeeOutDto> findAll() {
        List<Employee> allEmployees = employeeRepository.findAll();
        return employeeDtoMapper.map(allEmployees);
    }

    @Override
    public void delete(Long employeeId) throws NotFoundException {
        checkExistEmployee(employeeId);
        employeeRepository.deleteById(employeeId);
    }
}
